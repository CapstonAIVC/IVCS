import os
from sqlite3 import Time
import time
import threading
from tkinter import CENTER
from cv2 import line
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
import matplotlib.dates as mdates
from scipy.signal import savgol_filter

ROOT_PATH = './DATA'

class AnalyizeData(threading.Thread):
    def __init__(self, measure, cctvname, start_time, end_time):
        threading.Thread.__init__(self)
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
        
        if start_path == end_path:
            for path in os.listdir(start_path):
                if path.split('.')[0] >= self.start_hour and path.split('.')[0] <= self.end_hour:
                    csv_path_list.append(os.path.join(start_path, path))
        else:   
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
    
    # 시간 문자열로 바꾸기
    def change_time(self,time):
        t = int(time)
        if(t<10):
            return '0'+str(t)
        else:
            return str(t)
    
    # csv 파일 전처리
    def csv_preprocess(self,csv_path):
        path_data = csv_path.split('/')
        year, month, day, hour = path_data[-4], path_data[-3], path_data[-2], path_data[-1].split('.')[0]   
        df = pd.read_csv(csv_path)
        
        # 'Time' column value 중 'min-sec' 을 'year-month-day hour:min:sec'으로 바꾸기
        df['Time'] = df['Time'].apply(lambda x: year + '-' + month + '-' + day + ' ' + hour + ':' + self.change_time(x.split('-')[0]) + ':' + self.change_time(x.split('-')[1]))
        # 'Count' column value 에서 '[' 와 ']' 제거, 숫자를 소수점 3번째  자리에서 반올림
        df['Count'] = df['Count'].apply(lambda x: round(float(x.replace('[','').replace(']','')),2))
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
        df['Time'] = pd.to_datetime(df['Time'].str.strip(), format='%Y-%m-%d %H:%M:%S', errors='raise')
        df.index = df['Time']
        # print(df.head())
        # plot df into time series graph
        
        
        
        t1 = time.time()
        
        # change df['Count'] to np.array
        # df_count = df['Count'].values
        # df_time = df.index.values
        # np_time = np.array(df_time)
        # np_count = np.array(df_count)
        # np_smooth = savgol_filter(np_count, 180, 3)
        # #plot 의 x축을 np_time으로 설정 간격을 5분으로 설정
        # fig, ax = plt.subplots()
        # fig.set_size_inches(10, 5)
        # ax.plot(np_time, np_smooth, label='count')
        # # fig.autofmt_xdate()
        # # fig.tight_layout()
        # fig.savefig('./1415data.png')
        
        # plt.figure(figsize=(20,10))
        # plt.plot(np_smooth)
        # plt.savefig('./smooth.png')
        
        df_summary = pd.DataFrame()
           
          
        # df_summary['Count_Mean_1min'] = df.Count.resample('1T').max()
        # df_summary['Count_Mean_1min'].plot(figsize=(30,10), label='1min', linewidth=2, fontsize=20)
        # plt.savefig('./Count_Mean_1min.png')
        
        # df_summary['Count_Mean_1day'] = df.Count.resample('1D').mean()
        # df_summary['Count_Mean_1day'].plot(figsize=(30,10))
        # plt.savefig('./Count_Mean_1day.png')
        
        
        df_summary['Count_Mean_1hour'] = df.Count.resample('1H').mean()
        df_summary['Count_Mean_1hour'].plot(figsize=(30,10), label='1hour', linewidth=2, fontsize=20)
        plt.savefig('./Count_Mean_1hour.png')
        for data in df_summary['Count_Mean_1hour']:
            print(f"data : {data}")
        
        # # 30min
        # df_summary['Count_Mean_30min'] = df.Count.resample('30T').mean()
        # df_summary['Count_Mean_30min'].plot(figsize=(30,10), label='30min', linewidth=2, fontsize=20)
        # # plt.savefig('./30min.png')
        # plt.savefig('./24_2530min.png')
        
        # df_summary['Count_Mean_5min'] = df.Count.resample('5T').mean()
        # df_summary['Count_Mean_5min'].plot(figsize=(30,10), label='5min', linewidth=2, fontsize=20)
        # plt.savefig('./Count_Mean_5min.png')
        
        # df_summary['Count_Mean_10min'] = df.Count.resample('10T').mean()
        # plt.plot(df_summary['Count_Mean_10min'])
        # plt.savefig('./Count_Mean_10min.png')
        
        # df_summary['Count_Mean_30min'] = df.Count.resample('30T').mean()
        # plt.plot(df_summary['Count_Mean_30min'])
        # plt.savefig('./Count_Mean_30min.png')
        
        # df['Count'].plot(figsize=(30,10), label='Count', linewidth=2, fontsize=20)
        # plt.savefig('./Count.png')
        
        # df_summary['Count_mean_5sec'] = df.Count.resample('5S').mean()
        # df_summary['Count_mean_5sec'].plot(figsize=(30,10), label='5sec', linewidth=2, fontsize=20)
        # plt.savefig('./Count_mean_5sec.png')
        
        # df_summary['Count_mean_30sec'] = df.Count.resample('30S').max()
        # df_summary['Count_mean_30sec'].plot(figsize=(30,10), label='30sec', linewidth=2, fontsize=20)
        # plt.legend(loc='best')
        # plt.savefig('./Count_mean_30sec.png')
        
        ## rolling
        # df_summary['Count_mean_30sec'] = df.Count.rolling(30,center=True).mean()
        # df_summary['Count_mean_30sec'].plot(figsize=(30,10), label='30sec', linewidth=2, fontsize=20)
        # plt.legend(loc='best')
        # plt.savefig('./03rolling.png')
        t2 = time.time()
        print('plot time : ', t2-t1)
        
        

if __name__ == '__main__':
    start_time = '2022-04-24_13:00:00'
    end_time = '2022-04-24_15:00:00'
    cctvname = '[경부선] 공세육교'
    
    matplotlib.use('agg')
    makeThread=AnalyizeData('period', cctvname, start_time, end_time)
    makeThread.demon = True
    makeThread.start()
        