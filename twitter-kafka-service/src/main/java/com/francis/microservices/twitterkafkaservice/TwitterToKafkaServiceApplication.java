package com.francis.microservices.twitterkafkaservice;

import com.francis.microservices.twitterkafkaservice.init.StreamInitializer;
import com.francis.microservices.twitterkafkaservice.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.francis.microservices")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
//    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final StreamRunner streamRunner;
    private final StreamInitializer streamInitializer;


    public TwitterToKafkaServiceApplication(
//            TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
            StreamRunner streamRunner, StreamInitializer streamInitializer ) {
//        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.streamRunner = streamRunner;
        this.streamInitializer = streamInitializer;
    }


    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("App starts ... ");
//        LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {})));
//        LOG.info("App starts again... ");
//        LOG.info(" i am {}",twitterToKafkaServiceConfigData.getWelcomeMessage());
        streamInitializer.init();
        streamRunner.start();
    }
}
