package com.francis.microservices.twitterkafkaservice.runner.impl;

import com.francis.microservices.config.TwitterToKafkaServiceConfigData;
import com.francis.microservices.twitterkafkaservice.exception.TwitterKafkaException;
import com.francis.microservices.twitterkafkaservice.listener.TwitterKafkaStatusListener;
import com.francis.microservices.twitterkafkaservice.runner.StreamRunner;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "twitter-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);


    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;

    //MOCK
    private static final Random RANDOM = new Random();
    private static final String [] WORDS = new String[] {
      "a",
      "b",
      "c",
      "d",
      "e",
      "f",
    };
    private static final String tweetAsJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String dateFormat = "EEE MMM dd HH:mm:ss zzz yyyy";

    @Override
    public void start() {
        String [] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
        int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
        long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
        LOG.info("Starting mock {}", Arrays.toString(keywords));
        try {
            simulateTweet(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void simulateTweet(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) throws RuntimeException{
        Executors.newSingleThreadExecutor().submit(() ->{
            try {
                while (true){
                    String formattedTweet = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweet);
                    twitterKafkaStatusListener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            }catch (TwitterException e){
                LOG.error("Error", e);
            }

        });

    }

    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
        String [] params = new String[]{
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return formatTweetAsjson(params);
    }

    private String formatTweetAsjson(String[] params) {
        String tweet =  tweetAsJson;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) +minTweetLength;
        return getRandomTweet(keywords, tweet, tweetLength);
    }

    private String getRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength /2){
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }

    private void sleep(long sleepInMs){
        try {
            Thread.sleep(sleepInMs);
        } catch (InterruptedException e) {
            throw new TwitterKafkaException("error while sleeping");
        }
    }



}
