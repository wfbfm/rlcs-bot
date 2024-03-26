import React, { useEffect, useState } from 'react';
import './App.css';
import Series from './model/series';
import SeriesEvent from './model/seriesEvent';
import { SeriesEventContainer } from './seriesEventContainer';
import { Box, Center, Flex, HStack, Heading, Image, Text, VStack } from '@chakra-ui/react';
import ReactTwitchEmbedVideo from "react-twitch-embed-video"
import NavBar from './navBar';
import blueLogo from './Karmine_Corp_lightmode.png';
import orangeLogo from './Team_Vitality_2023_lightmode.png';

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
    <Box>
      <Box position='fixed' width='100%' height='30px' zIndex={999} boxShadow={'md'}>
        <NavBar></NavBar>
      </Box>
      <Box height='30px' p={2}></Box>
      <Box minW='100%' maxH='300px'>
        <ReactTwitchEmbedVideo height='300px' width='100%' channel='rocketleague' layout='video' autoplay={false}></ReactTwitchEmbedVideo>
      </Box>
      <Flex justifyContent='space-between'>
        <Box width={'20%'} color={'gray'}>
          <HStack>
            <Image maxWidth='8%' src={blueLogo}></Image>
            <Image maxWidth='8%' src={orangeLogo}></Image>
          </HStack>
        </Box>
        <Center>
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
        </Center>
        <VStack width='25%'>
          <Heading>All series</Heading>
          <Text fontSize='xs'>
            {Object.values(series).map((series, index) => (
              <Box key={index}>{JSON.stringify(series)}</Box>
            ))}
          </Text>
        </VStack>
      </Flex>
    </Box>
  );
};

export default App;
