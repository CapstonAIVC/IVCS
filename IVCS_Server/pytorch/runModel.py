import time
from model import FCN_rLSTM
import torch
import requests
import json
import cv2
import sys
from threading import Thread


cctvname = []
cctvurl = []
streamingList = []

class ThreadedCamera(object):
    def __init__(self, src=0):
        self.capture = cv2.VideoCapture(src)
        self.capture.set(cv2.CAP_PROP_BUFFERSIZE, 2)

        # FPS = 1/X
        # X = desired FPS
        self.FPS = 1/self.capture.get(cv2.CAP_PROP_FPS)
        self.FPS_MS = int(self.FPS * 1000)

        # Start frame retrieval thread
        self.thread = Thread(target=self.update, args=())
        self.thread.daemon = True
        self.thread.start()

    def update(self):
        while True:
            if self.capture.isOpened():
                (self.status, self.frame) = self.capture.read()
            time.sleep(self.FPS)

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
    
def setStreaming():
    for url in cctvurl :
        streamingList.append(ThreadedCamera(url))
        # cap = cv2.VideoCapture(url)
        # cap.set(cv2.CAP_PROP_FPS, 1)
        # cap.set(cv2.CAP_PROP_POS_MSEC, 1000)
        # cap.set(cv2.CAP_PROP_BUFFERSIZE, 2)
        # cap.set(cv2.CAP_PROP_FRAME_WIDTH, 160)
        # cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 120)
        # streamingList.append(cap)



if __name__ == '__main__':

    setInfo()
    setStreaming()
    # model = setmodel()

    seq = 1
    while True:
        time.sleep(1)

        frame = streamingList[0].get_frame()
        cv2.imwrite("./test/{}.jpg".format(seq), frame)
        # X = torch.zeros([5,1,3,120,160])
        # mask = torch.zeros([5,1,120,160])
        # with torch.no_grad():
        #     density_pred, count_pred = model(X, mask=mask)
        # sio.emit('modelOutput', {"cctvname": "테스트이름", "time":"20xx-0x-xx", "count":str(count_pred[4][0].item())})
        # sio.emit('modelOutput', str(count_pred[4][0].item()))
        seq += 1
        print("done")