package com.sigma.university.cloud.sample.search;

import java.util.Map;

public class ObjectSearchRequest {

    private Map<String, String> metadata;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public ObjectSearchRequest setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }
}
