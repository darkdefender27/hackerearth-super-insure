import config
import requests
import json
import statistics

class Glassdoor:
    def __init__(self):
        glassdoor_config = config.read(section='glassdoor')
        self.url = glassdoor_config['url']

    def getCompensationRating(self, company):
        response = requests.get(self.url.replace('cisco',company), headers={'User-Agent' : "Magic Browser"})
        return statistics.median([float(employer['compensationAndBenefitsRating']) for employer in response.json()['response']['employers']])

if __name__ =="__main__":
    glassdoor = Glassdoor()
    print(glassdoor.getCompensationRating('barclays'))
