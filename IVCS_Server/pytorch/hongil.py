import os
import time
import threading
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
import matplotlib.dates as mdates


ROOT_PATH = './DATA'

class MakePlot(threading.Thread):
    def __init__(self, measure, cctvname, x_mark, time_info):
        threading.Thread.__init__(self)
        self.measure = measure
        self.cctvname = cctvname
        self.x_mark = x_mark

        if self.measure == 'period': pass # 분석 단위가 기간일 경우 아직 만들지 않음
        else:
            time_tmp = time_info.split('_')
            self.year = time_tmp[0]
            self.month = time_tmp[1]
            self.day = time_tmp[2]
            self.start_time = int(time_tmp[3])
            self.end_time = int(time_tmp[4])

    def run(self):
        if self.measure == 'period': pass
        else:
            path = ROOT_PATH+'/'+self.cctvname+'/'+self.year+'/'+self.month+'/'+self.day+'/'
            # xdata = []
            # ydata = []
            df = pd.DataFrame()
            while(self.start_time <= self.end_time):
                tmp_csv = pd.read_csv(path+str(self.start_time)+'.csv')

                xdata_tmp = [str(self.start_time)+'-'+i for i in tmp_csv['Time']]
                tmp_csv['Time'] = xdata_tmp

                ydata_tmp = [round(float(i[1:-1]),3) for i in tmp_csv['Count']]
                tmp_csv['Count'] = ydata_tmp

                if df.empty: df=tmp_csv
                else:
                    df = pd.concat([df, tmp_csv], ignore_index=True)

                #평균
                # criteria = 5

                # count_tmp = 0
                # ct_len = 0
                # start_flag = -1
                # for idx, time in enumerate(tmp_csv['Time']):
                #     time_tmp = int(time.split('-')[0])
                #     if time_tmp%criteria==0 and start_flag!=-1:
                #         x_result = str(self.start_time)+":"+str(time_tmp)
                #         xdata.append(x_result)
                #         y_result = count_tmp/float(ct_len)
                #         ydata.append(str(y_result))

                #         count_tmp = 0
                #         ct_len = 0
                #         start_flag = -1
                    
                #     else:
                #         if start_flag==-1 and (time_tmp%criteria)>0: start_flag=1

                #         count_tmp+=float(tmp_csv['Count'][idx][1:-1])
                #         ct_len+=1

                self.start_time+=1

            # plt.scatter(df['Time'], df['Count'])
            
            plt.plot(df['Count'])
            plt.grid()
            plt.savefig('./plz.png')
            



if __name__ == '__main__':
    measure = 'hour'
    cctvname = '[경부선] 공세육교'
    x_mark = 'hour'
    time_info = '2022_4_24_3_12'

    matplotlib.use('agg')
    makeThread=MakePlot(measure=measure, cctvname=cctvname, x_mark=x_mark, time_info=time_info)
    makeThread.demon = True
    makeThread.start()