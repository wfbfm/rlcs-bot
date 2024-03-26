import React, { useEffect, useState } from 'react';
import './App.css';
import Series from './model/series';
import SeriesEvent from './model/seriesEvent';
import { SeriesEventContainer } from './seriesEventContainer';
import { Box, HStack } from '@chakra-ui/react';
import ReactTwitchEmbedVideo from "react-twitch-embed-video"

const App: React.FC = () => {
  const [socket, setSocket] = useState<WebSocket | null>(null);
  // FIXME: these can't be lists, they need to be maps - as the same ID can be sent down multiple times on update
  const [seriesEvents, setSeriesEvents] = useState<{ [eventId: string]: SeriesEvent }>({});
  const [series, setSeries] = useState<{ [seriesId: string]: Series }>({});
  const [messages, setMessages] = useState<(String)[]>([]);

  useEffect(() => {
    const ws = new WebSocket('ws://localhost:8887');
    setSocket(ws);

    return () => {
      ws.close();
    };
  }, []);

  useEffect(() => {
    if (!socket) {
      return;
    }

    socket.onmessage = (event) => {
      const socketData = JSON.parse(event.data);
      if (socketData._index === 'seriesevent') {
        setSeriesEvents((prevSeriesEvents) => {
          const updatedSeriesEvents = { ...prevSeriesEvents };
          updatedSeriesEvents[socketData._source.eventId] = socketData;
          return updatedSeriesEvents;
        });
      } else if (socketData._index === 'series') {
        setSeries((prevSeries) => {
          const updatedSeries = { ...prevSeries };
          updatedSeries[socketData._source.seriesId] = socketData;
          return updatedSeries;
        });
      }
      setMessages((prevMessages) => [...prevMessages, event.data]);
    }

    return () => {
      socket.onmessage = null;
    };
  }, [socket]);

  return (
    <div>
      <Box minW='500px' maxH='300px'>
        <ReactTwitchEmbedVideo height='300px' width='500px' channel='rocketleague' layout='video' autoplay={false}></ReactTwitchEmbedVideo>
      </Box>
      <h1>RLCS Commentary App</h1>
      <h2>All series events</h2>
      <HStack>
        <Box p={4} minW='10px'>
        </Box>
        <Box>
          {Object.values(seriesEvents)
            .sort((a, b) => {
              const regex = /Event(\d+)-/;
              const eventIdA = parseInt((a._source.eventId.match(regex) || [])[1], 10);
              const eventIdB = parseInt((b._source.eventId.match(regex) || [])[1], 10);
              return eventIdB - eventIdA;
            })
            .map((seriesEvent, index) => (
              <Box key={index} p={4}>
                <SeriesEventContainer seriesEvent={seriesEvent} series={series[seriesEvent._source.seriesId]} />
              </Box>
            ))}
        </Box>
      </HStack>
      <h2>All series</h2>
      <div>
        {Object.values(series).map((series, index) => (
          <div key={index}>{JSON.stringify(series)}</div>
        ))}
      </div>
    </div>
  );
};

export default App;
