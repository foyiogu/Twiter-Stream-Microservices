package com.francis.microservices.twitterkafkaservice.exception;

public class TwitterKafkaException extends RuntimeException{

    public TwitterKafkaException() {
        super();
    }

    public TwitterKafkaException(String message) {
        super(message);
    }

    public TwitterKafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}
