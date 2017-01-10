import requests
import json
import urllib
import config
import pickle
from nltk.stem.snowball import PorterStemmer
from nltk.corpus import stopwords
import nltk
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn import svm
import numpy as np

class SMSOffset:
    def __init__(self):
        offset_config = config.read(section='sms_offset')
        self.url = offset_config['url']

    def get_current(self, number):
        response = requests.get(self.url+'/search?Number='+number+'&fields='+','.join(['Offset']))
        return json.loads(response.text)

    def update(self, number, offset):
        response = requests.patch(self.url+'/Number/'+number,json={'Offset':str(offset)}, headers={'Content-Type':'application/json'})
        return response.status_code

class SMS:
    def __init__(self):
        sms_config = config.read(section='sms')
        self.url = sms_config['url']
        self.SMSOffset = SMSOffset()

    def search(self, number, offset=0, setoffset=True):
        if offset == 0:
            offset = int(self.SMSOffset.get_current(number)[0]['Offset'])
        response = requests.get(self.url+'?Number='+str(number)+'&offset='+str(offset))
        output = json.loads(response.text)
        if setoffset is True:
            self.SMSOffset.update(number,str(offset+len(output)))
        return output

class Classifier:
    def __init__(self):
        f = open('bin/feature_engg.pickle','rb')
        self.feature_engineer = pickle.load(f)
        f.close()
        f = open('bin/classifier.pickle','rb')
        self.classfier = pickle.load(f)
        f.close()
        self.stemmer = PorterStemmer()
        self.stopwords = stopwords.words("english")

    def predict(self,input_array):
        return self.classfier.predict(self.feature_engineer.transform(input_array)).tolist() if len(input_array) > 0 else []


    def apply_stop_stem_case(self,message):
        message_tokens = nltk.word_tokenize(message)
        filtered_msg_tokens = []
        for token in message_tokens:
            if token not in self.stopwords and type(token) != int:
                filtered_msg_tokens.append(token)
        stems = [self.stemmer.stem(token) for token in filtered_msg_tokens]
        return stems

if __name__ =="__main__":
    sms = SMS()
    classifier = Classifier()
    messages = sms.search('9702486064')
    output = classifier.predict([message['Text'] for message in messages])
    for label in zip([message['Text'] for message in messages], output):
        print(label)
