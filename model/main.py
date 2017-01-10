from facebook import Facebook
from twitter import Twitter
from mortality import Mortality
from sms import SMS,Classifier
from google_fit import GoogleFit
from glassdoor import Glassdoor
from fico import FICO
import config
import statistics

class Model:
    def __init__(self):
        self.facebook = Facebook()
        self.twitter = Twitter()
        self.mortality = Mortality()
        self.sms = SMS()
        self.classifier = Classifier()
        self.fit = GoogleFit()
        self.glassdoor = Glassdoor()
        self.fico = FICO()
        self.config = config.read(section='model')

    def _fb_info(self, user):
        fb_info = {}
        output = self.facebook.get_response(user,['location','age_range'])
        fb_info['location'] = output['location']['name']
        fb_info['age_range'] = int(output['age_range']['min'])
        fb_info['tagged_places'] = [(place[2],place[3]) for place in self.facebook.get_employment(user)]
        fb_info['employers'] = [employer[0] for employer in self.facebook.get_employment(user)]
        return fb_info

    def _tweet_info(self, user, count=10):
        return [tweet for tweet in self.twitter.fetch_tweets(user,count)]

    def _sms_info(self, phone_number, offset=0):
        messages = [message['Text'] for message in self.sms.search(phone_number, offset=0, setoffset=False)]
        return [x for x in zip(messages,self.classifier.predict(messages))]

    def _health_info(self, user, weeks=1):
        return self.fit.get_walk_metrics(user, weeks)

    def _glassdoor_info(self, company):
        return self.glassdoor.getCompensationRating(company)

    def _fico_info(self, user):
        return self.fico.get_score(user)

    def _mortality_info(self, country):
        return int(self.mortality.search(country,['Rank 2014'])[0]['Rank 2014'])

    def score(self, userids):
        final_score = 0
        output = {}
        damping_factor = float(self.config['damping_factor'])
        fb_info = self._fb_info(userids['fb_id'])
        '''
        This is to compute delta from ideal age
        '''
        output['age_range'] = int(fb_info['age_range'])
        final_score += float(int(fb_info['age_range']) - int(self.config['ideal_age']))/damping_factor
        '''
        This is to compute delta from ideal ratio of new city fb checkins w.r.t to total checkins
        '''
        base_location = fb_info['location']
        checkins = [x[0].lower() for x in fb_info['tagged_places']]
        unique_checkins = set(checkins) if base_location.split(',')[0].lower() not in checkins else set(checkins) - 1
        checkin_ratio = float(len(unique_checkins))/len(checkins)
        output['checkin_ratio'] = checkin_ratio
        final_score += (float(self.config['fb_loc_checkin_ratio']) - checkin_ratio)/damping_factor
        '''
        This is to compute the mortality rate of the base location
        '''
        country = base_location.split(',')[1].strip()
        mortality_rank = self._mortality_info(country)
        output['mortality_rank'] = mortality_rank
        final_score += float(mortality_rank - int(self.config['mortality_min_rank']))/damping_factor
        '''
        This is to compute asset/liability ratio
        '''
        phone_number = userids['phone_number']
        sms_classified = self._sms_info(phone_number)
        positives = sum([1 if x[1] in ['financial positive'] else 0 for x in sms_classified])
        negatives = sum([1 if x[1] in ['financial negative'] else 0 for x in sms_classified])
        asset_liability_ratio = float(positives)/(negatives+1)
        output['asset_liability_ratio'] = asset_liability_ratio
        final_score += (asset_liability_ratio - float(self.config['asset_liability_ratio']))/damping_factor
        '''
        This is to compute emotional sentiment on twitter
        '''
        twitter_id = userids['tweet_id']
        tweets_sentiments = self._tweet_info(twitter_id)
        positives = sum([1 if x[1] in ['positive','neutral'] else 0 for x in tweets_sentiments])
        ratio = float(positives)/(len(tweets_sentiments)-positives)
        output['sentiment_ratio'] = ratio
        final_score += (ratio - float(self.config['tweet_sentiment_ratio']))/damping_factor
        '''
        This is to compute using FICO Score
        '''
        fico_score = self._fico_info(userids['fico_id'])
        output['fico_score'] = fico_score
        final_score += (fico_score - int(self.config['fico_score_threshold']))/damping_factor
        '''
        This is used to compute Health score
        '''
        health_indices = self._health_info(userids['fit_id'], weeks=int(self.config['health_report_weeks']))
        health_index_median = statistics.median(health_indices)
        output['health_index'] = health_index_median
        final_score += (health_index_median - int(self.config['health_threshold']))/damping_factor
        '''
        This is used to compute average salary ratings
        '''
        employers = set([employer.split(' ')[0].strip() for employer in fb_info['employers']])
        sal_index = statistics.median([statistics.median(self._glassdoor_info(employer)) for employer in employers])
        output['sal_index'] = sal_index
        final_score += (sal_index - int(self.config['comp_rating_threshold']))/damping_factor

        output['final_score'] = 800 - final_score
        return output

if __name__ == "__main__":
    model = Model()
    print(model.score(config.read(section='user1')))
