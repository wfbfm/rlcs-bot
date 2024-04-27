import React, { useEffect, useState } from 'react';
import './App.css';
import Series from './model/series';
import SeriesEvent from './model/seriesEvent';
import { SeriesEventContainer } from './seriesEventContainer';
import { Box, Button, Center, Collapse, Flex, HStack, Heading, Icon, IconButton, Image, Link, Spacer, Text, VStack, useBreakpointValue, useColorModeValue } from '@chakra-ui/react';
import ReactTwitchEmbedVideo from "react-twitch-embed-video"
import { SeriesContainer } from './seriesContainer';
import { IoLogoTwitch } from "react-icons/io";
import SideBar from './sideBar';
import { SeriesHeader } from './seriesHeader';
import { NewSeriesContainer } from './newSeriesContainer';
import { SeriesVictoryContainer } from './seriesVictoryContainer';

const App: React.FC = () =>
{
  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [seriesEvents, setSeriesEvents] = useState<{ [eventId: string]: SeriesEvent }>({});
  const [series, setSeries] = useState<{ [seriesId: string]: Series }>({});
  const [currentSeries, setCurrentSeries] = useState<Series | null>(null);
  const [logos, setLogos] = useState<{ [logoName: string]: string }>({});
  const [showTwitch, setShowTwitch] = React.useState(false);
  const appPort = 8080;
  // FIXME;
  // const appPort = process.env.APP_PORT;

  useEffect(() =>
  {
  // FIXME: not localhost
    const ws = new WebSocket('wss://localhost:' + appPort);
    setSocket(ws);

    return () =>
    {
      ws.close();
    };
  }, []);

  useEffect(() =>
  {
    if (!socket)
    {
      return;
    }

    socket.onmessage = (event) =>
    {
      const socketData = JSON.parse(event.data);

      if (socketData.payloadType === 'image')
      {
        setLogos((prevLogos) =>
        {
          const updatedLogos = { ...prevLogos };
          updatedLogos[socketData.imageName] = socketData.base64Image;
          return updatedLogos;
        });
        return;
      }

      const rlcsData = socketData.payload;

      if (rlcsData._index === 'seriesevent')
      {
        setSeriesEvents((prevSeriesEvents) =>
        {
          const updatedSeriesEvents = { ...prevSeriesEvents };
          updatedSeriesEvents[rlcsData._source.eventId] = rlcsData;
          return updatedSeriesEvents;
        });
      } else if (rlcsData._index === 'series')
      {
        setSeries((prevSeries) =>
        {
          const updatedSeries = { ...prevSeries };
          updatedSeries[rlcsData._source.seriesId] = rlcsData;
          setCurrentSeries(rlcsData);
          return updatedSeries;
        });
      }
    }

    return () =>
    {
      socket.onmessage = null;
    };
  }, [socket]);

  function getLastNumberFromSeriesId(seriesId: string): number
  {
    const parts = seriesId.split('-');
    const lastPart = parts[parts.length - 1];
    return parseInt(lastPart, 10);
  }

  function collapsableTwitchStream()
  {
    const handleTwitchButton = () => setShowTwitch(!showTwitch);

    return (
      <Box borderRadius='md' overflow={'hidden'} width='100%'>
        <Center>
          <Button onClick={handleTwitchButton} leftIcon={<Icon as={IoLogoTwitch}></Icon>}
            aria-label={showTwitch ? 'Hide Twitch' : 'Show Twitch'} colorScheme='purple'>
            {showTwitch ? 'Hide Twitch' : 'Show Twitch'}
          </Button>
        </Center>
        <Collapse in={showTwitch}>
          <ReactTwitchEmbedVideo height='300px' width='100%' channel='rocketleague' layout='video' autoplay={false}></ReactTwitchEmbedVideo>
        </Collapse>
      </Box>
    );
  }

  const isMobile = useBreakpointValue({ base: true, sm: false });
  const backgroundColour = useColorModeValue('gray.100', 'gray.900');
  const mainWidth = isMobile ? '100%' : '80%';

  return (
    <Flex>
      {!isMobile &&
      <>
      <Box bg={backgroundColour} position='fixed' width='20%' height='100%' zIndex={999} boxShadow={'md'}>
        <SideBar></SideBar>
        <Flex flexDirection={'column'} height='90%'>
          <VStack flex='1'>
            <Box>
              {Object.values(series)
                .sort((a, b) =>
                {
                  const seriesIdA = getLastNumberFromSeriesId(a._source.seriesId);
                  const seriesIdB = getLastNumberFromSeriesId(b._source.seriesId);
                  return seriesIdB - seriesIdA;
                })
                .map((series, index) => (
                  <Box key={index} p={4}>
                    <SeriesContainer series={series} logos={logos} />
                  </Box>
                ))}
            </Box>
          </VStack>

          <Box p={4}>
            <Text fontSize='xs'>
              RL Commentary Service is a fan-made hobby project and is not a reliable source.
              <br></br>
              <br></br>
              Scores are determined by real-time parsing of the <b><Link color={'blue.500'} href='https://www.twitch.tv/rocketleague'>official Twitch broadcast</Link></b> - similarly,
              the text feed is populated from a raw AI transcription of live audio commentary.
              <br></br>
              All reference data is sourced from <b><Link color={'blue.500'} href='https://liquipedia.net/rocketleague/Main_Page'>Liquipedia</Link></b>.
            </Text>
          </Box>

        </Flex>
      </Box>
      <Spacer></Spacer>
      </>
      }

      <Box width={mainWidth} overflow='hidden' p={0}>
        <Box p={0} position='fixed' width={mainWidth} height='10%' zIndex={999} borderBottom='1px solid gray' bg={useColorModeValue('gray.100', 'gray.900')} overflow='hidden'>
          <Center p={4}>
            <SeriesHeader series={currentSeries} logos={logos} isMobile={isMobile}/>
          </Center>
        </Box>


        <VStack p={4} marginTop={isMobile ? '20%' : '6%'} height='100%'>
          {collapsableTwitchStream()}
          <Box minW='50%'>
            <VStack height='100%'>
              {Object.values(seriesEvents)
                .sort((a, b) =>
                {
                  const regex = /Event(\d+)-/;
                  const seriesIdA = getLastNumberFromSeriesId(a._source.seriesId);
                  const seriesIdB = getLastNumberFromSeriesId(b._source.seriesId);
                  // Compare series IDs first
                  if (seriesIdA !== seriesIdB)
                  {
                    return seriesIdB - seriesIdA; // Sort by decreasing series ID
                  }
                  const eventIdA = parseInt((a._source.eventId.match(regex) || [])[1], 10);
                  const eventIdB = parseInt((b._source.eventId.match(regex) || [])[1], 10);
                  return eventIdB - eventIdA;
                })
                .map((seriesEvent, index) => (
                  <Box key={index} p={4} width={isMobile ? '100%' : '60%'} height='100%'>
                    {seriesEvent._source.evaluation === 'NEW_SERIES' ?
                    <NewSeriesContainer seriesEvent={seriesEvent} series={series[seriesEvent._source.seriesId]} logos={logos} isMobile={isMobile} />
                    :
                    seriesEvent._source.evaluation === 'SERIES_COMPLETE' ?
                    <SeriesVictoryContainer seriesEvent={seriesEvent} series={series[seriesEvent._source.seriesId]} logos={logos} />
                    :
                    <SeriesEventContainer seriesEvent={seriesEvent} series={series[seriesEvent._source.seriesId]} logos={logos} isMobile={isMobile} />
                    }
                  </Box>
                ))}
            </VStack>
          </Box>
        </VStack>
      </Box>
    </Flex>
  );
};

export default App;
