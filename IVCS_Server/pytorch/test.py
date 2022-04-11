import sys
import torch
import json
from urllib.request import urlopen
import requests

# Global Setting
api_key = "94643db94d744a2a850cdf9663965164"

def getUrl(url, cctv_id):
    with urlopen(url) as url:
        data = json.loads(url.read().decode())

    for cctvname in data['response']['data']:
        if cctvname['cctvname']==cctv_id:
            web_url = cctvname['cctvurl']

    response=requests.get(web_url)
    url = response.url
    
    return url

# def main():
#     live_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=1&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"
#     video_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=2&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"
#     img_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=3&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"

#     data_type = int(sys.argv[1])
#     cctvname = sys.argv[2]
#     if data_type==1:
#         url = live_sample
#     elif data_type==2:
#         url = video_sample
#     else:
#         url = img_sample

#     request_url = getUrl(url, cctvname)

#     print(request_url)

if __name__ == '__main__':
    live_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=1&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"
    video_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=2&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"
    img_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=3&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"

    data_type = int(sys.argv[1])
    cctvname = sys.argv[2]
    if data_type==1:
        url = live_sample
    elif data_type==2:
        url = video_sample
    else:
        url = img_sample

    request_url = getUrl(url, cctvname)

    print(request_url)