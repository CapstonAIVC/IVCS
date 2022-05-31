# -*- coding: utf-8 -*-
from datetime import datetime
from time import time
from pytz import timezone
import os
import copy

import requests
import json
import threading

# from flask import Flask
import socketio
import eventlet
import eventlet.wsgi
from flask import Flask, request, send_file
from flask_cors import CORS

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib
import io

sio = socketio.Server(cors_allowed_origins='*')
app = Flask(__name__)
CORS(app, resource={r"/req_plot":{"origins":"*"}})

HOST = 'localhost'
PORT = 4000
ROOT_PATH = './DATA'

response = requests.get('http://localhost:3000/getUrl')
total_info = eval(json.loads(response.text))
cctvname = total_info['cctvname']
data = {}
latest = [-1,-1,-1,-1,-1]
count = [-1,-1,-1,-1,-1]
input_img = [-1,-1,-1,-1,-1]
density = [-1,-1,-1,-1,-1]

# time_tmp = -1 # 이전 시간 정보 저장
time_tmp = datetime.now(timezone("Asia/Seoul"))

# @sio.on('model_output')
# def get_data(sid, output):
#     global time_tmp, data, latest, cctvname
#     output = json.loads(output)
#     print(output)
#     current_time = datetime.now(timezone("Asia/Seoul"))

#     # if time_tmp.minute != current_time.minute: #테스트를 위한 1분 간격 저장
#     if time_tmp.hour != current_time.hour: #1시간 간격 데이터  저저장
#         time_tmp = current_time
#         save_data = copy.deepcopy(data)
#         for cctv in data.keys(): data[cctv]=[]
#         saveThread=SaveCSV(save_data, time_tmp)
#         saveThread.start()
    
#     time_info = str(current_time.minute) + '-' + str(current_time.second)

#     tmp = []
#     for idx, cctv in enumerate(cctvname):
#         data[cctv].append([time_info, output[idx]])
#         tmp.append(output[idx])
#     latest = tmp

@sio.on('model_output')
def get_data(sid, count_result_json, input_img_result, density_result):
    global time_tmp, data, count, input_img, density
    count_result = json.loads(count_result_json)
    # input_img_result = json.loads(input_img_result_json)
    # density_result = json.loads(density_result_json)
    print(count_result)
    current_time = datetime.now(timezone("Asia/Seoul"))

    # if time_tmp.minute != current_time.minute: #테스트를 위한 1분 간격 저장
    if time_tmp.hour != current_time.hour: #1시간 간격 데이터  저저장
        time_tmp = current_time
        save_data = copy.deepcopy(data)
        for cctv in data.keys(): data[cctv]=[]
        saveThread=SaveCSV(save_data, time_tmp)
        saveThread.start()
    
    time_info = str(current_time.minute) + '-' + str(current_time.second)

    count_tmp = []
    input_img_tmp = []
    density_tmp = []
    for idx, cctv in enumerate(cctvname):
        data[cctv].append([time_info, count_result[idx]])
        count_tmp.append(count_result[idx])
        input_img_tmp.append(input_img_result[idx])
        density_tmp.append(density_result[idx])

    count = count_tmp
    input_img = input_img_tmp
    density = density_tmp

# @sio.on('req_counting')
# def startCounting(sid, cctvIdx):
#     global count, input_img, density
#     # sio.emit('res_counting', str(round(latest[int(cctvIdx)[0]], 3)), sid)
#     sio.emit('res_counting', str(round(count[int(cctvIdx)])), sid)

@sio.on('req_counting')
def startCounting(sid, cctvIdx):
    global count, input_img, density
    # sio.emit('res_counting', str(round(latest[int(cctvIdx)[0]], 3)), sid)
    sio.emit('res_counting', data=(str(round(count[int(cctvIdx)])), input_img[int(cctvIdx)], density[int(cctvIdx)]), room=sid)

# @sio.on('req_counting_mobile')
# def startCounting(sid, cctvIdx):
#     global count, input_img, density
#     # sio.emit('res_counting', str(round(latest[int(cctvIdx)[0]], 3)), sid)
#     sio.emit('res_counting', data=(str(round(count[int(cctvIdx)])), str(input_img[int(cctvIdx)]), str(density[int(cctvIdx)])), room=sid)
    
@app.route('/req_plot', methods=['POST'])
def res_plot_png():
    global cctvname

    params = request.get_json()
    # print(params)

    measure_method = params['measure_method']
    cameraid = params['cameraid']
    start = params['start']
    end = params['end']

    task = AnalyizeData(measure_method, cctvname[int(cameraid)], start, end)
    result = task.run()
    print(type(result.getvalue()))

    # move to beginning of file so `send_file()` it will read from start 
    result.seek(0)

    return send_file(result, mimetype='image/png')

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
        self.data = data
        self.time = time_info

        # 2자리 폴더명 & 파일명 생성을 위한 코드
        self.year = str(self.time.year)
        if int(self.time.month) < 10: self.month = '0'+str(self.time.month)
        else: self.month = str(self.time.month)
        if int(self.time.day) < 10: self.day = '0'+str(self.time.day)
        else: self.day = str(self.time.day)
        if int(self.time.hour) < 10: self.hour = '0'+str(self.time.hour)
        else: self.hour = str(self.time.hour)

    def make_dir(self, cctv):
        if not os.path.isdir(ROOT_PATH+'/'+cctv+'/'+self.year): os.mkdir(ROOT_PATH+'/'+cctv+'/'+self.year)
        if not os.path.isdir(ROOT_PATH+'/'+cctv+'/'+self.year+'/'+self.month): os.mkdir(ROOT_PATH+'/'+cctv+'/'+self.year+'/'+self.month)
        if not os.path.isdir(ROOT_PATH+'/'+cctv+'/'+self.year+'/'+self.month+'/'+self.day): os.mkdir(ROOT_PATH+'/'+cctv+'/'+self.year+'/'+self.month+'/'+self.day)

    def run(self):
        for cctv in cctvname:
            self.make_dir(cctv)

            df = pd.DataFrame( self.data[cctv], columns = ['Time', 'Count'] )
            print(df)

            df.to_csv(ROOT_PATH+'/'+cctv+'/'+self.year+'/'+self.month+'/'+self.day+'/'+self.hour+'.csv', mode='w')

# class AnalyizeData(threading.Thread):
class AnalyizeData():
    def __init__(self, measure, cctvname, start_time, end_time):
        # threading.Thread.__init__(self)
        self.measure = measure
        self.cctvname = cctvname
        self.start_time = start_time
        self.end_time = end_time
        #split time with date and hour
        self.start_date, self.start_hour = self.start_time.split('_')
        self.end_date, self.end_hour = self.end_time.split('_')


    # csv 파일 경로 리스트 반환
    def get_csv_path_list(self):
        csv_path_list = []
        start_year, start_month, start_day = self.start_date.split('-')
        end_year, end_month, end_day = self.end_date.split('-')
        start_path = os.path.join(ROOT_PATH, self.cctvname, start_year, start_month, start_day)
        end_path = os.path.join(ROOT_PATH, self.cctvname, end_year, end_month, end_day)

        for path in os.listdir(start_path):
            start_hour = int(self.start_hour.split(':')[0])
            if path.split('.')[-1] == 'csv' and int(path.split('.')[0]) >= start_hour:
                csv_path_list.append(os.path.join(start_path, path))


        for path in os.listdir(end_path):
            end_hour = int(self.end_hour.split(':')[0])
            if path.split('.')[-1] == 'csv' and int(path.split('.')[0]) < end_hour:
                csv_path_list.append(os.path.join(end_path, path))

        csv_path_list.sort()
        return csv_path_list

    # csv 파일 전처리
    def csv_preprocess(self,csv_path):
        path_data = csv_path.split('/')
        year, month, day, hour = path_data[-4], path_data[-3], path_data[-2], path_data[-1].split('.')[0]   
        df = pd.read_csv(csv_path)

        # 'Time' column value 중 'min-sec' 을 'year-month-day hour:min:sec'으로 바꾸기
        df['Time'] = df['Time'].apply(lambda x: year + '-' + month + '-' + day + ' ' + hour + ':' + x.split('-')[0] + ':' + x.split('-')[1])
        df['Count'] = df['Count'].apply(lambda x: round(float(x.replace('[','').replace(']','')),3))
        return df


    # concat all csv file from csv_path into one dataframe by using csv_preprocess
    def get_dataframe(self, csv_path_list):
        df_list = []
        for csv_path in csv_path_list:
            df_temp = self.csv_preprocess(csv_path)
            df_list.append(df_temp)
        df = pd.concat(df_list)
        return df


    def run(self):
        csv_path_list = self.get_csv_path_list()
        df = self.get_dataframe(csv_path_list)
        df = df.drop(['Unnamed: 0'], axis=1)
        
        plt.figure(figsize=(15,5))
        plt.plot(df['Count'])
        
        img_buf = io.BytesIO()
        plt.savefig(img_buf, format='png')

        return img_buf

        

if __name__ == "__main__":
    if not os.path.isdir(ROOT_PATH):
        os.mkdir(ROOT_PATH)
    make_cctv_dir()

    matplotlib.use('agg')

    # wrap Flask application with engineio's middleware
    app = socketio.Middleware(sio, app)

    # deploy as an eventlet WSGI server
    eventlet.wsgi.server(eventlet.listen(('', PORT)), app)

