package com.francis.microservices.twitterkafkaservice.runner.impl;

import com.francis.microservices.config.TwitterToKafkaServiceConfigData;
import com.francis.microservices.twitterkafkaservice.listener.TwitterKafkaStatusListener;
import com.francis.microservices.twitterkafkaservice.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.annotation.PreDestroy;
import java.util.Arrays;

//@AllArgsConstructor
@Component
@ConditionalOnProperty(name = "twitter-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true) //from app.yml
public class TwitterKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);


    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;
    private TwitterStream twitterStream;

    public TwitterKafkaStreamRunner(TwitterToKafkaServiceConfigData configData,
                                    TwitterKafkaStatusListener statusListener) {
        this.twitterToKafkaServiceConfigData = configData;
        this.twitterKafkaStatusListener = statusListener;
    }


    @Override
    public void start() throws TwitterException {
        twitterStream =new TwitterStreamFactory().getInstance();
        twitterStream.addListener(twitterKafkaStatusListener);
        addFilter();
    }

    @PreDestroy
    public void shutDown(){
        if (twitterStream != null){
            LOG.info(" Closing stream");
            twitterStream.shutdown();
        }
    }

    private void addFilter() {
        String [] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        LOG.info("Started {}", Arrays.toString(keywords));
    }


}
