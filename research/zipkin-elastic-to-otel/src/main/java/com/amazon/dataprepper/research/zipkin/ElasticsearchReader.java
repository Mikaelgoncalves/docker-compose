package com.amazon.dataprepper.research.zipkin;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElasticsearchReader {
    private String scrollId = null;
    private final String indexPattern;
    private final SearchSourceBuilder searchSourceBuilder;
    private final TimeValue keepAlive = TimeValue.timeValueMinutes(1);
    private long total;

    public ElasticsearchReader(final String indexPattern, final String field, final String value) {
        this.indexPattern = indexPattern;
        if (field != null && value != null) {
            searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery(field, value));
        } else {
            searchSourceBuilder = null;
        }
    }

    public long getTotal() {
        return total;
    }

    public List<Map<String, Object>> nextBatch(final RestHighLevelClient restHighLevelClient) throws IOException {
        if (scrollId == null) {
            final SearchRequest searchRequest = new SearchRequest(indexPattern);
            searchRequest.scroll(keepAlive);
            if (searchSourceBuilder != null) {
                searchRequest.source(searchSourceBuilder);
            }
            final SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // Update total hits
            total = searchResponse.getHits().getTotalHits().value;
            updateScrollId(searchResponse);
            final SearchHits hits = searchResponse.getHits();
            return Arrays.stream(hits.getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());
        } else {
            final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(keepAlive);
            final SearchResponse searchScrollResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            updateScrollId(searchScrollResponse);
            final SearchHits hits = searchScrollResponse.getHits();
            return Arrays.stream(hits.getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());
        }
    }

    private void updateScrollId(final SearchResponse searchResponse) {
        scrollId = searchResponse.getScrollId();
    }

    public void clearScroll(final RestHighLevelClient restHighLevelClient) throws IOException {
        final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
    }
}
