package com.sigma.university.cloud.sample.search;

import java.util.Map;

public class ObjectSearchResponse {

    private String objectKey;
    private Map<String, String> metadata;

    public String getObjectKey() {
        return objectKey;
    }

    public ObjectSearchResponse setObjectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public ObjectSearchResponse setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }
}
