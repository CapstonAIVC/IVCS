import time
from model import FCN_BLA, FCN, Encoder, Decoder
import torch
import requests
import json
import cv2
from threading import Thread
import threading
import torchvision
from torchvision import transforms
import numpy as np
from PIL import Image

import socketio

sio_saveData = socketio.Client()
sio_saveData.connect('http://localhost:4000', wait_timeout = 10)

@sio_saveData.event
def connect():
    print("Data API Server connected")

@sio_saveData.event
def connect_error(data):
    print("The connection failed!")

@sio_saveData.event
def disconnect():
    print("Data API Server disconnected")

## 모든 목록의 index는 같습니다.
cctvname = []
cctvurl = []
streamingList = []
TOTAL_CCTV_NUM = 1

## tesorList의 각 원소는 tensor를 가지는 list이다.
tensorList = []
trans = transforms.Compose([transforms.Resize((120,160)), 
                        transforms.ToTensor(),
                        # transforms.Normalize((0.5,0.5,0.5), (0.5,0.5,0.5))
                        ])

# class ThreadedCamera(object):
#     def __init__(self, src=0):
#         self.frame = None
#         self.status = None
#         self.tmp = None


#         self.capture = cv2.VideoCapture(src)
#         # FPS = 1/X
#         # X = desired FPS
#         self.FPS = 1/self.capture.get(cv2.CAP_PROP_FPS)
#         self.FPS_MS = int(self.FPS * 1000)

#         self.capture.set(cv2.CAP_PROP_BUFFERSIZE, 2)
#         self.capture.set(cv2.CAP_PROP_FPS, self.FPS)

#         # Start frame retrieval thread
#         self.thread = Thread(target=self.update, args=())
#         self.thread.daemon = True
#         self.thread.start()

#     def update(self):
#         while True:
#             if self.capture.isOpened():
#                 (self.status, tmp) = self.capture.read()
#                 if self.status:
#                     self.frame = tmp
#             time.sleep(0.5)

#     def get_frame(self):
#         return self.frame

class ThreadedCamera(threading.Thread):
    def __init__(self, src, th_name):
        threading.Thread.__init__(self)
        self.frame = None
        self.status = None
        self.tmp = None
        self.th_name = th_name

        self.capture = cv2.VideoCapture(src)
        self.FPS = 1/self.capture.get(cv2.CAP_PROP_FPS)
        self.FPS_MS = int(self.FPS * 1000)

        self.capture.set(cv2.CAP_PROP_BUFFERSIZE, 2)
        self.capture.set(cv2.CAP_PROP_FPS, self.FPS)

    def run(self):
        while True:
            if self.capture.isOpened():
                (self.status, tmp) = self.capture.read()
                if self.status:
                    self.frame = tmp
            time.sleep(0.5)

    def get_frame(self):
        return self.frame

def setmodel():
    # model = FCN_rLSTM(temporal=True, image_dim=(torch.zeros([120,160], dtype=torch.int32).shape))
    model = FCN_BLA(FCN, Encoder, Decoder, image_dim=[120,160])
    model.load_state_dict(torch.load('./model.pth', map_location=torch.device('cpu')))
    print("model loaded")

    # model.eval()  # set model to evaluation mode

    return model

def setInfo():
    global TOTAL_CCTV_NUM

    response = requests.get('http://localhost:3000/getUrl')
    total_info = eval(json.loads(response.text))
    for name in total_info['cctvname']:
        cctvname.append(name)
    for url in total_info['cctvurl']:
        cctvurl.append(url)
        tensorList.append([])
    # set TOTAL_CCTV_NUM (실제 구동할땐 주석풀기)
    TOTAL_CCTV_NUM = len(cctvurl)
    
def setStreaming():
    global cctvurl, cctvname
    # for url in cctvurl:
    #     streamingList.append(ThreadedCamera(url))
    for idx, url in enumerate(cctvurl):
        streamingList.append(ThreadedCamera(url, cctvname[idx]))

        # Start frame retrieval thread
        streamingList[idx].setDaemon(True)
        streamingList[idx].start()

def addFramesByTensor(index):
    global tensorList, TOTAL_CCTV_NUM

    for i in range(0, TOTAL_CCTV_NUM):
        frame = streamingList[i].get_frame()
        pil = Image.fromarray(frame)
        ten = trans(pil)
        ten = torch.reshape(ten,(3,120,160))
        ten.unsqueeze(0)
        if index == -1:
            for _ in range(5): tensorList[i].append(ten)
        else:
            tensorList[i][index] = ten

def popFrames():
    global TOTAL_CCTV_NUM

    for i in range(0,TOTAL_CCTV_NUM):
        tensorList[i].pop(0)

# if __name__ == '__main__':

#     setInfo()
#     setStreaming()
#     model = setmodel()

#     # for i in range(5):
#     #     addFramesByTensor(-1)

#     ## index는 tensorList 안의 list에 이번에 교체할 위치이다.
#     index = 0
#     while True:
#         addFramesByTensor(index)
#         if index == 4:
#             index = 0
#         else:
#             index += 1

#         result = []
#         for i in range(TOTAL_CCTV_NUM):
#             ## queue 
#             X = tensorList[i][index] ## 리스트 중 첫 프레임
#             for j in range(index+1, 5): ## 두번째부터 4까지
#                 X = torch.cat((X,tensorList[i][j]), 0)
#             for j in range(0, index): ## 0부터 index까지
#                 X = torch.cat((X,tensorList[i][j]), 0)

#             mask = torch.zeros([5,1,120,160])
#             with torch.no_grad():
#                 density_pred, count_pred = model(X, mask=mask)
#             # print(cctvname[i] + "\'s predict is "+str(count_pred))
#             result.append(count_pred.tolist()[-1])

#         # print(result)
#         # print(type(result))
#         # result_json = json.dumps(result)
#         # sio_saveData.emit('model_output', result_json)
        

#         # sio.emit('modelOutput', {"cctvname": "테스트이름", "time":"20xx-0x-xx", "count":str(count_pred[4][0].item())})
#         # sio.emit('modelOutput', str(count_pred[4][0].item()))

#         print("done\n\n")


############################################################################################################
# [경부선] 공세육교 24시간 데이터 수집을 위한 코드

# async def main(model):
#     mask = Image.open('./our_mask.png').convert('L')
#     mask = np.array(mask)
#     mask = torch.Tensor(mask)
#     mask_tmp = torch.Tensor(mask)
#     for i in range(4): mask = torch.cat((mask, mask_tmp), 0)
#     mask = mask.unsqueeze(1)

#     for i in range(5):
#         addFramesByTensor(-1)

#     ## index는 tensorList 안의 list에 이번에 교체할 위치이다.
#     index = 0
#     while True:
#         addFramesByTensor(index)
#         if index == 4:
#             index = 0
#         else:
#             index += 1

#         result = []
#         for i in range(TOTAL_CCTV_NUM):
#             ## queue 
#             X = tensorList[i][index] ## 리스트 중 첫 프레임
#             for j in range(index+1, 5): ## 두번째부터 4까지
#                 X = torch.cat((X,tensorList[i][j]), 0)
#             for j in range(0, index): ## 0부터 index까지
#                 X = torch.cat((X,tensorList[i][j]), 0)

#             with torch.no_grad():
#                 density_pred, count_pred = model(X, mask=mask)
            
#             result.append(count_pred.tolist()[0])


#         result_json = json.dumps(result)
#         await sio_saveData.emit('model_output', result_json)

#         print(str(result[0])+"\n")

# if __name__ == '__main__':
#     setInfo()
#     setStreaming()
#     model = setmodel()

#     main(model)

if __name__ == '__main__':
    setInfo()
    setStreaming()
    model = setmodel()
    model.eval()  # set model to evaluation mode

    for cctv_idx in range(len(cctvname)):
        mask_tmp = Image.open('./'+cctvname[cctv_idx]+'_mask.png').convert('L')
        mask_tmp = np.array(mask_tmp)
        mask_tmp = (torch.Tensor(mask_tmp)) / 255
        if cctv_idx == 0:
            mask = mask_tmp
            for i in range(4): mask = torch.cat((mask, mask_tmp), 0)
        else:
            for i in range(5): mask = torch.cat((mask, mask_tmp), 0)

    mask = torch.reshape(mask, (2, 5, 1, 120, 160))
    print(mask.shape)

    # mask = Image.open('./[남해선]초전2교_mask.png').convert('L')
    # mask = np.array(mask)
    # mask = torch.Tensor(mask)
    # mask = mask/255
    # mask_tmp = torch.Tensor(mask)
    # for i in range(4): mask = torch.cat((mask, mask_tmp), 0)
    # mask = torch.reshape(mask, (5, 120, 160))
    # mask = mask.unsqueeze(1)
    # # mask = mask.unsqueeze(0)
    # mask = Image.open('./[경부선]판교1_mask.png').convert('L')
    # mask = np.array(mask)
    # mask = torch.Tensor(mask)
    # mask = mask/255
    # mask_tmp = torch.Tensor(mask)
    # for i in range(4): mask = torch.cat((mask, mask_tmp), 0)
    # mask = torch.reshape(mask, (5, 120, 160))
    # mask = mask.unsqueeze(1)

    for i in range(5):
        addFramesByTensor(-1)

    print(np.shape(tensorList))

    ## index는 tensorList 안의 list에 이번에 교체할 위치이다.
    index = 0
    while True:
        addFramesByTensor(index)
        if index == 4:
            index = 0
        else:
            index += 1

        result = []
        for i in range(0, 2):
            ## queue
            if i == 0:
                X = tensorList[i][index] ## 리스트 중 첫 프레임
                for j in range(index+1, 5): ## 두번째부터 4까지
                    X = torch.cat((X,tensorList[i][j]), 0)
                for j in range(0, index): ## 0부터 index까지
                    X = torch.cat((X,tensorList[i][j]), 0)
            else:
                for j in range(index+1, 5): ## 두번째부터 4까지
                    X = torch.cat((X,tensorList[i][j]), 0)
                for j in range(0, index+1): ## 0부터 index까지
                    X = torch.cat((X,tensorList[i][j]), 0)

        # X = X.transpose(0,1)
        X = torch.reshape(X, (2, 5, 3, 120, 160))

        with torch.no_grad():
            density_pred, count_pred = model(X, mask=mask)
        
        # result.append(count_pred.tolist()[4][0])
        for idx in range(TOTAL_CCTV_NUM):
            result.append(count_pred.tolist()[idx][0])

        print(result)
        result_json = json.dumps(result)
        sio_saveData.emit('model_output', result_json)

        # print(str(result[0])+"\n")
############################################################################################################