package com.sigma.university.cloud.sample.delete;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@Service
public class ObjectStorageService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${storage.bucketName}")
    private String bucketName;

    @PostConstruct
    public void initialize() {
        //Validate bucket exists
        Assert.isTrue(amazonS3.doesBucketExistV2(bucketName), "S3 bucket " + bucketName + " does not exist");
    }

    public void deleteObject(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

}
