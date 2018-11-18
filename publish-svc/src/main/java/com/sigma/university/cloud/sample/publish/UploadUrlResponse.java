package com.sigma.university.cloud.sample.publish;

import java.net.URL;

public class UploadUrlResponse {

    private URL url;
    private String objectKey;

    public URL getUrl() {
        return url;
    }

    public UploadUrlResponse setUrl(URL url) {
        this.url = url;
        return this;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public UploadUrlResponse setObjectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }
}
