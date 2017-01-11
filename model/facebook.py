import config
import json
import requests
from datetime import datetime

class Facebook:
    def __init__(self):
        fbconfig = config.read(section='fb')
        self.url = fbconfig['url']
        self.access_token = fbconfig['access_token']

    def get_response(self, user, fields):
        url_request = self.url+'/'+user+'?fields='
        url_request += ','.join(fields)
        url_request += '&access_token='+self.access_token
        response = requests.get(url_request)
        if response.ok:
            return json.loads(response.text)
        else:
            None
    def get_places(self,user):
        url_request = self.url+'/'+user+'/tagged_places'
        url_request += '?access_token='+self.access_token
        response = requests.get(url_request)
        if response.ok:
            resp_json = json.loads(response.text)
            for data in resp_json['data']:
                date_format = "%Y-%m-%dT%H:%M:%S+0000"
                date = datetime.strptime(data['created_time'], date_format)
                yield (date.strftime("%m/%d/%Y"),data['place']['name'],data['place'].get('location').get('city'),data['place'].get('location').get('country'))
        else:
            yield None

    def get_employment(self, user):
        for employment in self.get_response(user,['work'])['work']:
            yield (employment['employer'].get('name'),employment['position'].get('name'), employment['start_date'], employment.get('end_date'))

if __name__ == "__main__":
    facebook = Facebook()
    print(facebook.get_response('me',['id','location','name','age_range','devices']))
    for place in facebook.get_places('me'):
        print(place)
    for employer in facebook.get_employment('me'):
        print(employer)
