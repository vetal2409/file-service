package com.sigma.university.cloud.sample.delete;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IndexService {

    private static final String DOC_TYPE = "_doc";

    private static final String ATTR_OBJECT_KEY = "key";
    private static final String ATTR_METADATA = "metadata";

    @Value("${storage.indexName}")
    private String indexName;

    @Autowired
    private RestHighLevelClient esClient;

    public void delete(String key) {
        try {
            esClient.delete(
                    new DeleteRequest(indexName, DOC_TYPE, key)
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to index document", e);
        }
    }
}
