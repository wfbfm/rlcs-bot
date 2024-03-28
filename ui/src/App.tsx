import React, { useEffect, useState } from 'react';
import './App.css';
import Series from './model/series';
import SeriesEvent from './model/seriesEvent';
import { SeriesEventContainer } from './seriesEventContainer';
import { Box, Center, Flex, HStack, Heading, Image, Spacer, Text, VStack } from '@chakra-ui/react';
import ReactTwitchEmbedVideo from "react-twitch-embed-video"
import NavBar from './navBar';
import blueLogo from './Karmine_Corp_lightmode.png';
import orangeLogo from './Team_Vitality_2023_lightmode.png';
import { SeriesContainer } from './seriesContainer';
import SideBar from './sideBar';

const App: React.FC = () => {
  const [socket, setSocket] = useState<WebSocket | null>(null);
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

  function getLastNumberFromSeriesId(seriesId: string): number {
    const parts = seriesId.split('-');
    const lastPart = parts[parts.length - 1];
    return parseInt(lastPart, 10);
  }

  return (
    <Flex>
      <Box position='fixed' width='20%' height='100%' zIndex={999} boxShadow={'md'}>
        <SideBar></SideBar>
      </Box>
      <Spacer></Spacer>
      <Box width='80%' p={4}>
        <VStack p={4}>
          <Box borderRadius='md' overflow={'hidden'} width='100%'>
            <ReactTwitchEmbedVideo height='300px' width='100%' channel='rocketleague' layout='video' autoplay={false}></ReactTwitchEmbedVideo>
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
        </VStack>
        <Flex justifyContent='space-between'>
          <Center>
            <HStack>
            </HStack>
          </Center>
          <VStack width='25%'>
            <Center>
              <HStack>
                <Box p={4} minW='10px'>
                </Box>
                <Box>
                  {Object.values(series)
                    .sort((a, b) => {
                      const eventIdA = getLastNumberFromSeriesId(a._source.seriesId);
                      const eventIdB = getLastNumberFromSeriesId(b._source.seriesId);
                      return eventIdB - eventIdA;
                    })
                    .map((series, index) => (
                      <Box key={index} p={4}>
                        <SeriesContainer series={series} />
                      </Box>
                    ))}
                </Box>
              </HStack>
            </Center>
          </VStack>
        </Flex>
      </Box>
    </Flex>
  );
};

export default App;
