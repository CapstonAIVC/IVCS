# from msvcrt import setmode
import cv2
import time
import socketio
from model import FCN_rLSTM
import torch
import requests
import json

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
    

sio = socketio.Client()
sio.connect('http://localhost:3000')

@sio.event
def connect():
    print("Address Server connected")

@sio.event
def connect_error(data):
    print("The connection failed!")

@sio.event
def disconnect():
    print("Address Server disconnected")

if __name__ == '__main__':
    model = setmodel()
    num = 1

    response = requests.get('http://localhost:3000/getUrl')
    total_info = eval(json.loads(response.text))
    cctvname = total_info['cctvname']
    cctvurl = total_info['cctvurl']

    # while True:
    #     print(num)
    #     num += 1

    #     # time.sleep(1)

    #     X = torch.zeros([5,1,3,120,160])
    #     mask = torch.zeros([5,1,120,160])
    #     with torch.no_grad():
    #         density_pred, count_pred = model(X, mask=mask)
    #     # sio.emit('modelOutput', {"cctvname": "테스트이름", "time":"20xx-0x-xx", "count":str(count_pred[4][0].item())})
    #     sio.emit('modelOutput', str(count_pred[4][0].item()))
    #     print("done")