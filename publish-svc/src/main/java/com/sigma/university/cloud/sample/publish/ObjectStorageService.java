package com.sigma.university.cloud.sample.publish;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class ObjectStorageService {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${storage.bucketName}")
    private String bucketName;

    @Value("${storage.uploadUrlExpireTimeout}")
    private long urlExpireTimeout;

    @PostConstruct
    public void initialize() {
        //Validate bucket exists
        Assert.isTrue(amazonS3.doesBucketExistV2(bucketName), "S3 bucket " + bucketName + " does not exist");
    }

    public UploadUrlResponse generateUploadUrl(UploadUrlRequest uploadUrlRequest) {
        Instant now = Instant.now();
        String key = generateObjectKey();

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(Date.from(now.plusMillis(urlExpireTimeout)));

        URL uploadUrl = amazonS3.generatePresignedUrl(request);

        return new UploadUrlResponse()
                .setUrl(uploadUrl)
                .setObjectKey(key);
    }

    public boolean objectExists(String key) {
        return amazonS3.doesObjectExist(bucketName, key);
    }

    public void deleteObject(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    protected String generateObjectKey() {
        return UUID.randomUUID().toString();
    }

    public static class DocumentPublishedEvent {

        private String objectKey;

        public String getObjectKey() {
            return objectKey;
        }

        public DocumentPublishedEvent setObjectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }
    }

}
