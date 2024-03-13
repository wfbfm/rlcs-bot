package com.wfbfm.rlcsbot.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.ELASTIC_API_KEY;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.ELASTIC_SEARCH_SERVER;

public class ElasticSearchPublisher
{
    private final ElasticsearchClient client;
    private final Logger logger = Logger.getLogger(ElasticSearchPublisher.class.getName());

    public ElasticSearchPublisher()
    {
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
    }

    public void createIndex()
    {
        try
        {
            client.indices().create(c -> c.index("series-snapshot"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void uploadSeriesSnapshot(final SeriesSnapshot seriesSnapshot, final String id)
    {
        try
        {
            final IndexResponse response = client.index(i -> i.index("series-snapshot").id(id).document(seriesSnapshot));
            logger.log(Level.INFO, response.toString());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
