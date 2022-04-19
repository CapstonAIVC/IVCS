import time
# from types import NoneType
from model import FCN_rLSTM
import torch
import requests
import json
import cv2
import sys
from threading import Thread
import torchvision
from torchvision import transforms
import numpy as np
from PIL import Image

## 모든 목록의 index는 같습니다.
cctvname = []
cctvurl = []
streamingList = []
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
    
def setStreaming():
    for url in cctvurl :
        streamingList.append(ThreadedCamera(url))

def addFramesByTensor(index):
    for i in range(0,len(streamingList)):
        frame = streamingList[i].get_frame()
        pil = Image.fromarray(frame)
        ten = trans(pil)
        ten = torch.reshape(ten,(1,1,3,120,160))
        if index == -1:
            tensorList[i].append(ten)
        else:
            tensorList[i][index] = ten

def popFrames():
    for i in range(0,len(streamingList)):
        tensorList[i].pop(0)


if __name__ == '__main__':

    setInfo()
    setStreaming()
    model = setmodel()

    for i in range(5):
        addFramesByTensor(-1)

    ## index는 tensorList 안의 list에 이번에 교체할 위치이다.
    index = 0
    while True:
        time.sleep(1)
        addFramesByTensor(index)
        if index == 4:
            index = 0
        else:
            index += 1

        for i in range(len(tensorList)):
            ## queue 
            X = tensorList[i][index] ## 리스트 중 첫 프레임
            for j in range(index+1, 5): ## 두번째부터 4까지
                X = torch.cat((X,tensorList[i][j]), 0)
            for j in range(0, index): ## 0부터 index까지
                X = torch.cat((X,tensorList[i][j]), 0)

            mask = torch.zeros([5,1,120,160])
            with torch.no_grad():
                density_pred, count_pred = model(X, mask=mask)
            print(cctvname[i] + "\'s predict is "+str(count_pred))
        

        # sio.emit('modelOutput', {"cctvname": "테스트이름", "time":"20xx-0x-xx", "count":str(count_pred[4][0].item())})
        # sio.emit('modelOutput', str(count_pred[4][0].item()))

        print("done\n\n")
