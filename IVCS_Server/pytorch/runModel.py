import time
from model import FCN_rLSTM
import torch
import requests
import json
import cv2
from threading import Thread
import torchvision
from torchvision import transforms
import numpy as np
from PIL import Image

import socketio

sio_saveData = socketio.Client()
sio_saveData.connect('http://localhost:5000')

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

class ThreadedCamera(object):
    def __init__(self, src=0):
        self.frame = None
        self.status = None
        self.tmp = None

        self.capture = cv2.VideoCapture(src)
        # FPS = 1/X
        # X = desired FPS
        self.FPS = 1/self.capture.get(cv2.CAP_PROP_FPS)
        self.FPS_MS = int(self.FPS * 1000)

        self.capture.set(cv2.CAP_PROP_BUFFERSIZE, 2)
        self.capture.set(cv2.CAP_PROP_FPS, self.FPS)

        # Start frame retrieval thread
        self.thread = Thread(target=self.update, args=())
        self.thread.daemon = True
        self.thread.start()

    def update(self):
        while True:
            if self.capture.isOpened():
                (self.status, tmp) = self.capture.read()
                if self.status:
                    self.frame = tmp
            time.sleep(0.5)

    def get_frame(self):
        return self.frame




def setmodel():
    args = {"model_path":'./model/fcn_rlstm_only_wct.pth', "dataset":"WebCamT", "data_path":"./data/WebCamT/origin2_pickle",
       "lr":1e-3, "ct":False, "epochs":500, "batch_size":16, "img_shape":[120,160] ,"lambda":1e-3, "gamma":1e3, "max_len":5,
       "weight_decay":0, "use_cuda":False, "use_tensorboard":True, "tb_img_shape":[120,160], "n2show":2, "seed":42,
       "log_dir":'./log'}

    model = FCN_rLSTM(temporal=True, image_dim=(torch.zeros([120,160], dtype=torch.int32).shape))
    model.load_state_dict(torch.load('./model.pth', map_location=torch.device('cpu')))
    print("model loaded")

    model.eval()  # set model to evaluation mode

    return model

def setInfo():
    response = requests.get('http://localhost:3000/getUrl')
    total_info = eval(json.loads(response.text))
    for name in total_info['cctvname']:
        cctvname.append(name)
    for url in total_info['cctvurl']:
        cctvurl.append(url)
        tensorList.append([])
    # # set TOTAL_CCTV_NUM (실제 구동할땐 주석풀기)
    # TOTAL_CCTV_NUM = len(cctvname)
    
def setStreaming():
    for url in cctvurl :
        streamingList.append(ThreadedCamera(url))

def addFramesByTensor(index):
    for i in range(0,TOTAL_CCTV_NUM):
        frame = streamingList[i].get_frame()
        pil = Image.fromarray(frame)
        ten = trans(pil)
        ten = torch.reshape(ten,(1,1,3,120,160))
        if index == -1:
            tensorList[i].append(ten)
        else:
            tensorList[i][index] = ten

def popFrames():
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
if __name__ == '__main__':

    setInfo()
    setStreaming()
    model = setmodel()

    mask = Image.open('./our_mask.png').convert('L')
    mask = np.array(mask)
    mask = torch.Tensor(mask)
    mask_tmp = torch.Tensor(mask)
    for i in range(4): mask = torch.cat((mask, mask_tmp), 0)
    mask = mask.unsqueeze(1)

    for i in range(5):
        addFramesByTensor(-1)

    ## index는 tensorList 안의 list에 이번에 교체할 위치이다.
    index = 0
    while True:
        addFramesByTensor(index)
        if index == 4:
            index = 0
        else:
            index += 1

        result = []
        for i in range(TOTAL_CCTV_NUM):
            ## queue 
            X = tensorList[i][index] ## 리스트 중 첫 프레임
            for j in range(index+1, 5): ## 두번째부터 4까지
                X = torch.cat((X,tensorList[i][j]), 0)
            for j in range(0, index): ## 0부터 index까지
                X = torch.cat((X,tensorList[i][j]), 0)

            with torch.no_grad():
                density_pred, count_pred = model(X, mask=mask)
            
            result.append(count_pred.tolist()[0])


        result_json = json.dumps(result)
        sio_saveData.emit('model_output', result_json)

        print(str(result[0])+"\n")
############################################################################################################