package com.sigma.university.cloud.sample.search;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Instant;
import java.util.Date;

@Service
public class ObjectStorageService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${storage.bucketName}")
    private String bucketName;

    @Value("${storage.downloadUrlExpireTimeout}")
    private long urlExpireTimeout;

    public ObjectDownloadResponse generateDownloadUrl(String objectKey) {
        Instant now = Instant.now();
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(Date.from(now.plusMillis(urlExpireTimeout)));

        URL downloadUrl = amazonS3.generatePresignedUrl(request);

        return new ObjectDownloadResponse()
                .setDownloadUrl(downloadUrl);
    }

}
