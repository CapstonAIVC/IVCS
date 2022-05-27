import sys
import json
from urllib.request import urlopen
import requests

if __name__ == '__main__':
    data_type = int(sys.argv[1])
    api_key = sys.argv[2]

    live_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=1&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"
    video_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=2&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"
    img_sample = "https://openapi.its.go.kr:9443/cctvInfo?apiKey="+api_key+"&type=ex&cctvType=3&minX=127.100000&maxX=128.890000&minY=34.100000&maxY=39.100000&getType=json"
    
    if data_type==1:
        url = live_sample
    elif data_type==2:
        url = video_sample
    else:
        url = img_sample

    with urlopen(url) as url:
        data = json.loads(url.read().decode())

    cctvinfo_dict = {"cctvname":[], "cctvurl":[]}

    cctvinfo_dict['cctvname'].append('[남해선]초전2교')
    cctvinfo_dict['cctvname'].append('[경부선]판교1')
    for json_data in data['response']['data']:
        if json_data['cctvname']=='[남해선] 초전2교' or json_data['cctvname']=='[경부선] 판교1':
            response=requests.get(json_data['cctvurl'])
            cctvinfo_dict['cctvurl'].append(response.url)
        if len(cctvinfo_dict['cctvurl']) == 2: break

    print(str(cctvinfo_dict))