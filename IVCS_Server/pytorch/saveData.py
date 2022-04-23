from datetime import datetime
from pytz import timezone
import os
import copy

import requests
import json
import threading

from flask import Flask
from flask_socketio import SocketIO

import pandas as pd

app = Flask(__name__)
socketio = SocketIO(app)

HOST = 'localhost'
PORT = 5000
ROOT_PATH = './'

response = requests.get('http://localhost:3000/getUrl')
total_info = eval(json.loads(response.text))
cctvname = total_info['cctvname']
data = {}
latest = []

# time_tmp = -1 # 이전 시간 정보 저장
time_tmp = datetime.now(timezone("Asia/Seoul"))

@socketio.on('model_output')
def get_data(output):
    global time_tmp, data, latest, cctvname
    print(output)
    current_time = datetime.now(timezone("Asia/Seoul"))

    if time_tmp.hour != current_time.hour:
        time_tmp = current_time
        save_data = copy.deepcopy(data)
        data.clear()
        saveThread=SaveCSV(save_data, time_tmp)
        saveThread.start()
        
        
    
    time_info = str(current_time.minute) + '-' + str(current_time.second)

    tmp = []
    for idx, cctv in enumerate(cctvname):
        # data[cctv].append([time_info, output[idx]])
        # tmp.append(output[idx])
        data[cctv].append([time_info, output])
        tmp.append(output)
    latest = tmp

@socketio.on('request_counting')
def startCounting( cctvIdx = 0 ):
    socketio.emit('counting', latest[cctvIdx], request.sid)


# @socketio.on('req_data')
# def send_data(cctv_time):

# cctv ID에 따른 저장 경로 생성
def make_cctv_dir():
    global data, cctvname

    for cctv in cctvname:
        if not os.path.isdir(ROOT_PATH+'/'+cctv):
            os.mkdir(ROOT_PATH+'/'+cctv)
        data.update({cctv:[]})

    print(data)

class SaveCSV(threading.Thread):
    def __init__(self, data, time_info):
        threading.Thread.__init__(self)
        self.data=data
        self.time=time_info

    def make_dir(self, cctv):
        year = self.time.year
        month = self.time.month
        day = self.time.day

        if not os.path.isdir(ROOT_PATH+'/'+cctv+'/'+str(year)): os.mkdir(ROOT_PATH+'/'+cctv+'/'+str(year))
        if not os.path.isdir(ROOT_PATH+'/'+cctv+'/'+str(year)+'/'+str(month)): os.mkdir(ROOT_PATH+'/'+cctv+'/'+str(year)+'/'+str(month))
        if not os.path.isdir(ROOT_PATH+'/'+cctv+'/'+str(year)+'/'+str(month)+'/'+str(day)): os.mkdir(ROOT_PATH+'/'+cctv+'/'+str(year)+'/'+str(month)+'/'+str(day))

    def run(self):
        for cctv in cctvname:
            self.make_dir(cctv)

            df = pd.DataFrame( self.data[cctv], columns = ['Time', 'Count'] )
            df.to_csv(ROOT_PATH+'/'+cctv+'/'+str(self.time.year)+'/'+str(self.time.month)+'/'+str(self.time.day)+'/'+str(self.time.hour)+'.csv', mode='w')
        

if __name__ == "__main__":
    # if time_tmp == -1: time_tmp = datetime.now(timezone("Asia/Seoul"))
    if not os.path.isdir(ROOT_PATH):
        os.mkdir(ROOT_PATH)
    make_cctv_dir()

    socketio.run(app, debug=True, host=HOST, port=PORT)