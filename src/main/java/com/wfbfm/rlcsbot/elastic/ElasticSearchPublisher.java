package com.wfbfm.rlcsbot.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.annotations.VisibleForTesting;
import com.wfbfm.rlcsbot.series.Series;
import com.wfbfm.rlcsbot.series.SeriesEvent;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

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

        try
        {
            createIndices();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unable to check or create Elastic indices", e);
        }
    }

    private void createIndices() throws IOException
    {
        final List<String> indices = Arrays.asList(ELASTIC_INDEX_SERIES, ELASTIC_INDEX_SERIES_EVENT);
        for (final String index : indices)
        {
            if (!client.indices().exists(ExistsRequest.of(e -> e.index(index))).value())
            {
                client.indices().create(c -> c.index(index));
            }
        }
    }

    public void uploadNewSeries(final Series series)
    {
        try
        {
            final IndexResponse response = client.index(i -> i.index(ELASTIC_INDEX_SERIES)
                    .id(series.getSeriesId())
                    .document(series));
            logger.log(Level.INFO, response.toString());
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unable to upload new series", e);
        }
    }

    public void updateSeries(final Series series)
    {
        try
        {
            final UpdateResponse<Series> response = client.update(u -> u.index(ELASTIC_INDEX_SERIES)
                            .id(series.getSeriesId())
                            .doc(series),
                    Series.class);
            logger.log(Level.INFO, response.toString());
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unable to update series", e);
        }
    }

    public void uploadNewSeriesEvent(final SeriesEvent seriesEvent)
    {
        try
        {
            final IndexResponse response = client.index(i -> i.index(ELASTIC_INDEX_SERIES_EVENT)
                    .id(seriesEvent.getEventId())
                    .document(seriesEvent));
            logger.log(Level.INFO, response.toString());
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unable to upload new series event", e);
        }
    }

    public SeriesEvent searchForSeriesEvent(final String seriesEventId)
    {
        try
        {
            final GetResponse<SeriesEvent> response = client.get(s -> s
                    .index(ELASTIC_INDEX_SERIES_EVENT)
                    .id(seriesEventId),
                    SeriesEvent.class);
            return response.source();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unable to search for existing series event", e);
        }
        return null;
    }

    public void updateSeriesEvent(final SeriesEvent seriesEvent)
    {
        try
        {
            final UpdateResponse<SeriesEvent> response = client.update(u -> u.index(ELASTIC_INDEX_SERIES_EVENT)
                            .id(seriesEvent.getEventId())
                            .doc(seriesEvent),
                    SeriesEvent.class);
            logger.log(Level.INFO, response.toString());
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unable to update series event", e);
        }
    }

    @VisibleForTesting
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
