import config
import json
import requests


class GoogleFit:

    def __init__(self):
        fitconfig = config.read(section='google_fit')
        self.url = fitconfig['url']
        self.access_token = fitconfig['access_token']

    def get_walk_metrics(self, userid, weeks=1):
        response = requests.get(self.url+'/users/'+str(userid)+'?accessToken='+self.access_token+'&weekDuration='+str(weeks))
        return json.loads(response.text)

if __name__ == "__main__":
    fit = GoogleFit()
    print(fit.get_walk_metrics(123))
