package com.sigma.university.cloud.sample.publish;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class IndexingService {

    private static final String DOC_TYPE = "_doc";

    private static final String ATTR_OBJECT_KEY = "key";
    private static final String ATTR_METADATA = "metadata";

    @Value("${storage.indexName}")
    private String indexName;

    @Autowired
    private RestHighLevelClient esClient;

    public void index(String key, Map<String, String> metadata) {
        Map<String, Object> source = new HashMap<>();
        source.put(ATTR_OBJECT_KEY, key);
        source.put(ATTR_METADATA, metadata);

        try {
            esClient.index(
                    new IndexRequest(indexName, DOC_TYPE, key)
                            .source(source)
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to index document", e);
        }
    }

    public boolean isDocumentIndexed(String key) {
        try {
            return esClient.exists(new GetRequest(indexName, DOC_TYPE, key));
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect index");
        }
    }

    public void delete(String key) {
        try {
            esClient.delete(new DeleteRequest(indexName, DOC_TYPE, key));
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect index");
        }
    }
}
