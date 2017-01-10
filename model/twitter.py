import tweepy
import config
from alchemyapi import AlchemyAPI

class Twitter:
    def __init__(self):
        twitterconfig = config.read(section='twitter')
        auth = tweepy.OAuthHandler(twitterconfig['oauth1'],twitterconfig['oauth2'])
        auth.set_access_token(twitterconfig['token1'],twitterconfig['token2'])
        self.api = tweepy.API(auth)
        self.alchemyapi = AlchemyAPI()

    def fetch_tweets(self,userid, count):
        tweets = self.api.search_users(userid)[0].timeline()

        for tweet in tweets[:count]:
            response = self.alchemyapi.sentiment("text",str(tweet.text)).get("docSentiment")
            yield (tweet.text, response.get("type"))

if __name__ == "__main__":
    twitter = Twitter()
    for tweet_sentiment in twitter.fetch_tweets('ashishbhatiya18',5):
        print(tweet_sentiment)
