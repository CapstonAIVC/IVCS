import os
from sqlite3 import Time
import threading
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
import matplotlib.dates as mdates

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
        print("----------csv path list-------------")
        for path in csv_path_list:
            print(path)
        df = self.get_dataframe(csv_path_list)
        # plot df into time series graph
        print(df.head())
        
        
        
        
        
                
        
        
        
        
        
        
if __name__ == '__main__':
    start_time = '2022-04-24_04:00:00'
    end_time = '2022-04-25_13:00:00'
    cctvname = '[경부선] 공세육교'
    
    matplotlib.use('agg')
    makeThread=AnalyizeData('period', cctvname, start_time, end_time)
    makeThread.demon = True
    makeThread.start()
        