import requests
import json
import urllib
import config

class Mortality:
    def __init__(self):
        mortality_config = config.read(section='mortality')
        self.url = mortality_config['url']

    def search(self, country,fields):
        response = requests.get(self.url+'/search?Country='+country+'&fields='+','.join(['Country']+fields))
        return json.loads(response.text)

if __name__ =="__main__":
    mortality = Mortality()
    print(mortality.search('Argentina',['Rank 2011','Rank 2014']))
