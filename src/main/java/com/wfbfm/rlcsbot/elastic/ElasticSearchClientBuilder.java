package com.wfbfm.rlcsbot.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.ELASTIC_API_KEY;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.ELASTIC_SEARCH_SERVER;

public abstract class ElasticSearchClientBuilder
{
    public static ElasticsearchClient getElasticsearchClient()
    {
        final ElasticsearchClient client;
        final RestClient restClient = RestClient
                .builder(HttpHost.create(ELASTIC_SEARCH_SERVER))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + ELASTIC_API_KEY)
                })
                .build();

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

        client = new ElasticsearchClient(transport);
        return client;
    }
}
