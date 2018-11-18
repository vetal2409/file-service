package com.sigma.university.cloud.sample;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfiguration {

    @Bean
    public AmazonS3 amazonS3(@Value("${aws.s3.region}") String region) {
        AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder.standard()
                .withRegion(region);

        return amazonS3ClientBuilder.build();
    }

    @Bean
    public AmazonSQS amazonSQS(@Value("${aws.sqs.region}") String region) {

        AmazonSQSClientBuilder amazonSQSClientBuilder = AmazonSQSClientBuilder.standard()
                .withRegion(region);

        return amazonSQSClientBuilder.build();
    }

}
