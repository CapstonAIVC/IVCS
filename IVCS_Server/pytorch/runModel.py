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
import io
import queue

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
MAX_LEN = 5

## tesorList의 각 원소는 tensor를 가지는 list이다.
tensorList = []
trans = transforms.Compose([transforms.Resize((120,160)), 
                        transforms.ToTensor(),
                        ])


class ThreadedCamera(threading.Thread):
    def __init__(self, src, th_name):
        threading.Thread.__init__(self)
        self.q = queue.Queue()
        self.frame = None
        self.status = None
        self.tmp = None
        self.th_name = th_name
        self.src = src

        self.capture = cv2.VideoCapture(self.src)
        self.FPS = 1/self.capture.get(cv2.CAP_PROP_FPS)
        self.FPS_MS = int(self.FPS * 1000)

        self.capture.set(cv2.CAP_PROP_BUFFERSIZE, 150)
        # self.capture.set(cv2.CAP_PROP_FPS, self.FPS)

    def run(self):
        (self.status, tmp) = self.capture.read()
        count = 4
        while count > 0:
            self.q.put(tmp)
            count -= 1
        while True:
            # self.cap = cv2.VideoCapture(self.src)
            # if self.cap.isOpened():
            #     (self.status, tmp) = self.cap.read()
            #     if self.status:
            #         self.frame = tmp
            # time.sleep(1)
            # if self.capture.isOpened():
            #     (self.status, tmp) = self.capture.read()
            #     if self.status:
            #         self.frame = tmp
            # time.sleep(0.5)
            while True:
                (self.status, tmp) = self.capture.read()
                if self.status:
                    self.frame = tmp
                else:
                    break
            time.sleep(1)

    def get_frame(self):
        self.q.put(self.frame)
        return self.q.get()

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
    global tensorList, TOTAL_CCTV_NUM, MAX_LEN

    for i in range(0, TOTAL_CCTV_NUM):
        frame = streamingList[i].get_frame()
        pil = Image.fromarray(frame)
        ten = trans(pil)
        ten = torch.reshape(ten,(3,120,160))
        ten.unsqueeze(0)
        if index == -1:
            for _ in range(MAX_LEN): tensorList[i].append(ten)
        else:
            tensorList[i][index] = ten

def popFrames():
    global TOTAL_CCTV_NUM, tensorList

    for i in range(0,TOTAL_CCTV_NUM):
        tensorList[i].pop(0)

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
            for _ in range(MAX_LEN-1): mask = torch.cat((mask, mask_tmp), 0)
        else:
            for _ in range(MAX_LEN): mask = torch.cat((mask, mask_tmp), 0)

    mask = torch.reshape(mask, (TOTAL_CCTV_NUM, MAX_LEN, 1, 120, 160))
    print(mask.shape)

    for i in range(MAX_LEN):
        addFramesByTensor(-1)

    ## index는 tensorList 안의 list에 이번에 교체할 위치이다.
    index = 0
    flag = False
    while True:
        addFramesByTensor(index)
        if index == MAX_LEN-1:
            index = 0
        else:
            index += 1
            flag = True

        input_img = []
        result = []
        density_result = []
        for i in range(0, 2):
            # queue
            if i == 0:
                X = tensorList[i][index] ## 리스트 중 첫 프레임
                for j in range(index+1, MAX_LEN): ## 두번째부터 4까지
                    X = torch.cat((X,tensorList[i][j]), 0)
                for j in range(0, index): ## 0부터 index까지
                    X = torch.cat((X,tensorList[i][j]), 0)
            else:
                for j in range(index+1, MAX_LEN): ## 두번째부터 4까지
                    X = torch.cat((X,tensorList[i][j]), 0)
                for j in range(0, index+1): ## 0부터 index까지
                    X = torch.cat((X,tensorList[i][j]), 0)

        X = torch.reshape(X, (TOTAL_CCTV_NUM, MAX_LEN, 3, 120, 160))

        with torch.no_grad():
            density_pred, count_pred = model(X, mask=mask)

        # if flag and index == 0:
        #     torchvision.utils.save_image(X[0,-1,:,:,:], './1.png')
        #     torchvision.utils.save_image(X[1,-1,:,:,:], './2.png')
        #     torchvision.utils.save_image(X[0,-1,-1,:,:]*mask[0,-1,:,:,:], './1_masking.png')
        #     torchvision.utils.save_image(X[1,-1,-1,:,:]*mask[1,-1,:,:,:], './2_masking.png')
        #     torchvision.utils.save_image(density_pred[0,-1,:,:,:]*5, './1_density.png')
        #     torchvision.utils.save_image(density_pred[1,-1,:,:,:]*5, './2_density.png')
        #     ferret_1 = cv2.imread('./1_density.png')
        #     HSV = cv2.cvtColor(ferret_1, cv2.COLOR_BGR2HSV)
        #     highlight = cv2.bitwise_not(HSV)
        #     cv2.imwrite('1_density_hsv.png',HSV)
        #     cv2.imwrite('1_density_hl.png',highlight)
        #     ferret_2 = cv2.imread('./2_density.png')
        #     HSV = cv2.cvtColor(ferret_2, cv2.COLOR_BGR2HSV)
        #     highlight = cv2.bitwise_not(HSV)
        #     cv2.imwrite('2_density_hsv.png',HSV)
        #     cv2.imwrite('2_density_hl.png',highlight)
        #     print(count_pred)
        #     break
        
        for idx in range(TOTAL_CCTV_NUM):
            input_tmp = io.BytesIO()
            density_tmp = io.BytesIO()
            torchvision.utils.save_image(X[idx,-1,-1,:,:]*mask[idx,-1,:,:,:], input_tmp, format='png')
            torchvision.utils.save_image(density_pred[idx,-1,:,:,:]*5, density_tmp, format='png')
            
            input_img.append(input_tmp.getvalue())
            result.append(count_pred.tolist()[idx][0])
            density_result.append(density_tmp.getvalue())

        print(result)
        result_json = json.dumps(result)

        sio_saveData.emit('model_output', data=(result_json, input_img, density_result))
        time.sleep(1)
        # sio_saveData.emit('model_output', result_json)

        # print(str(result[0])+"\n")
############################################################################################################