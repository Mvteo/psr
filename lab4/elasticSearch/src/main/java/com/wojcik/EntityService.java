package com.wojcik;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract public class EntityService<T> {

        abstract Class<T> getEntityType();

        private final RestHighLevelClient client;
        private final ObjectMapper objectMapper;

        protected EntityService(RestHighLevelClient client,
                              ObjectMapper objectMapper) {
            this.client = client;
            this.objectMapper = objectMapper;
        }

        public Optional<T> getById(Long id) throws IOException {
            var request = new GetRequest(getName());
            request.id(id.toString());
            var result = client.get(request, RequestOptions.DEFAULT);
            if (!result.isExists())  {
                return Optional.empty();
            }
            return Optional.ofNullable(objectMapper.readValue(result.getSourceAsString(),
                    getEntityType()));
        }

        public List<T> getAll() throws IOException {
            var request = new SearchRequest(getName());
            var hits = client.search(request, RequestOptions.DEFAULT).getHits().getHits();
            var list = new ArrayList<T>();
            for (SearchHit hit : hits) {
                list.add(objectMapper.readValue(hit.getSourceAsString(), getEntityType()));
            }
            return list;
        }

        public void add(T entity, Long id) throws IOException {
            var indexRequest = new IndexRequest(getName());
            indexRequest.id(id.toString());
            indexRequest.source(objectMapper.writeValueAsString(entity), XContentType.JSON);
            var status =  client.index(indexRequest, RequestOptions.DEFAULT).status().getStatus();
        }

        public void deleteById(Long id) throws IOException {
            var delRequest = new DeleteRequest(getName());
            delRequest.id(id.toString());
            var status = client.delete(delRequest, RequestOptions.DEFAULT).status().getStatus();
        }

        private String getName() {
            return getEntityType().getSimpleName().toLowerCase();
        }


}
