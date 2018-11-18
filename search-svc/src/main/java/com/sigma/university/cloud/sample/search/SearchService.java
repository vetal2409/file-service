package com.sigma.university.cloud.sample.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SearchService {

    private static final String DOC_TYPE = "_doc";

    private static final String ATTR_OBJECT_KEY = "key";
    private static final String ATTR_METADATA = "metadata";

    private static final String METADATA_KEY_FORMAT = ATTR_METADATA + ".%s.keyword";

    @Value("${storage.indexName}")
    private String indexName;

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    public List<ObjectSearchResponse> search(ObjectSearchRequest request) {
        BoolQueryBuilder termsQuery = QueryBuilders.boolQuery();
        request.getMetadata().entrySet().stream()
                .map(metaEntry -> QueryBuilders.termQuery(
                        String.format(METADATA_KEY_FORMAT, metaEntry.getKey()),
                        metaEntry.getValue()
                )).forEachOrdered(termsQuery::must);

        try {
            SearchHits hits = esClient.search(
                    new SearchRequest(indexName)
                            .types(DOC_TYPE)
                            .source(
                                    new SearchSourceBuilder().query(
                                            QueryBuilders.boolQuery().filter(termsQuery)
                                    )
                            )
            ).getHits();

            return Arrays.stream(hits.getHits())
                    .map(this::mapDocument)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Exception searching document document", e);
        }
    }

    private ObjectSearchResponse mapDocument(SearchHit documentFields) {
        JsonNode source;
        try {
            source = objectMapper.readTree(documentFields.getSourceAsString());
            return new ObjectSearchResponse()
                    .setObjectKey(source.get(ATTR_OBJECT_KEY).asText())
                    .setMetadata(
                            StreamSupport.stream(
                                    Spliterators.spliteratorUnknownSize(source.get(ATTR_METADATA).fields(), 0),
                                    false
                            ).collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue().asText()))
                    );
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse document", e);
        }
    }
}
