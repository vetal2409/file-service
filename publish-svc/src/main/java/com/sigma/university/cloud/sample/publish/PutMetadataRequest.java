package com.sigma.university.cloud.sample.publish;

import java.util.Map;

public class PutMetadataRequest {

    private Map<String, String> metadata;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public PutMetadataRequest setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }
}
