package com.wfbfm.rlcsbot.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public abstract class ElasticSearchClientBuilder
{
    public static ElasticsearchClient getElasticsearchClient()
    {
        final ElasticsearchClient client;
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD));
        final RestClient restClient = RestClient
                .builder(HttpHost.create("https://" + ELASTICSEARCH_HOST + ":9200"))
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

        client = new ElasticsearchClient(transport);

        final List<String> indices = Arrays.asList(ELASTIC_INDEX_SERIES, ELASTIC_INDEX_SERIES_EVENT);
        createIndices(client, indices);
        return client;
    }

    private static void createIndices(final ElasticsearchClient client, final List<String> indices)
    {
        for (final String index : indices)
        {
            try
            {
                if (!client.indices().exists(ExistsRequest.of(e -> e.index(index))).value())
                {
                    client.indices().create(c -> c.index(index));
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
