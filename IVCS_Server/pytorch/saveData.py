from datetime import datetime
from pytz import timezone
import os

import requests
import json

from flask import Flask
from flask_socketio import SocketIO

app = Flask(__name__)
socketio = SocketIO(app)

HOST = 'localhost'
PORT = 5000
ROOT_PATH = './'

response = requests.get('http://localhost:3000/getUrl')
total_info = eval(json.loads(response.text))
cctvname = total_info['cctvname']
data = {'cctv'}

def get_time():
    time = datetime.now(timezone("Asia/Seoul"))

@socketio.on('model_output')
def get_data(output):
    # print('received data: ' + output)


@socketio.on('req_data')
def send_data(cctv_time):


if __name__ == "__main__":
    socketio.run(app, debug=True, host=HOST, port=PORT)