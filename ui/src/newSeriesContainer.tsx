import React, { useEffect, useState } from 'react';
import SeriesEvent from './model/seriesEvent';
import { Badge, Box, Center, Divider, Flex, HStack, Icon, Image, Spacer, Stack, Text, VStack, useColorMode, useColorModeValue } from '@chakra-ui/react';
import Series from './model/series';

const getLogoName = (teamName: string | undefined, isLightMode: boolean): string | undefined => {
    if (!teamName) {
        return undefined;
    }
    const modeSuffix = isLightMode ? '_lightmode' : '_darkmode';
    return `${teamName}${modeSuffix}.png`
};


export const NewSeriesContainer: React.FC<{ seriesEvent: SeriesEvent, series: Series, logos: { [logoName: string]: string }, isMobile: boolean | undefined }> = ({ seriesEvent, series, logos, isMobile }) => {
    const { colorMode, toggleColorMode } = useColorMode();
    const headerColour = useColorModeValue('gray.200', 'gray.800')
    const commentaryColour = useColorModeValue('gray.100', 'gray.600')
    const blueBannerColour = useColorModeValue('blue.200', 'blue.600')
    const orangeBannerColour = useColorModeValue('orange.300', 'orange.400')
    const isLightMode = colorMode === 'light';
    const blueLogoName = getLogoName(series?._source.blueTeam.teamName, isLightMode);
    const orangeLogoName = getLogoName(series?._source.orangeTeam.teamName, isLightMode);
    const blueLogo = blueLogoName ? logos[blueLogoName] : undefined;
    const orangeLogo = orangeLogoName ? logos[orangeLogoName] : undefined;

    return (
        <>
            <Center width='100%' borderWidth='1px' borderRadius='lg' overflow='hidden'>
                <Box width='100%'>
                    <Center width='100%' bg={headerColour} p={2}>
                        <Flex justifyContent='space-between' alignItems='center' width='100%'>
                            {!isMobile &&
                                <>
                                    <Spacer></Spacer>
                                    <Spacer></Spacer>
                                </>
                            }
                            <VStack alignItems='center'>
                                <Image src={`${blueLogo}`} boxSize={10}></Image>
                                <Text as='b' fontSize='sm'>{series._source.blueTeam.teamName}</Text>
                            </VStack>
                            <Spacer></Spacer>
                            <Text as='b' fontSize='m' align={'center'}>
                                NEW SERIES
                                <br></br>
                                Best of {`${series._source.bestOf}`}
                            </Text>
                            <Spacer></Spacer>
                            <VStack alignItems='center'>
                                <Image src={`${orangeLogo}`} boxSize={10}></Image>
                                <Text as='b' fontSize='sm'>{series._source.orangeTeam.teamName}</Text>
                            </VStack>
                            {!isMobile &&
                                <>
                                    <Spacer></Spacer>
                                    <Spacer></Spacer>
                                </>
                            }
                        </Flex>
                    </Center>
                    <Box bg={commentaryColour} p={4}>
                        <Text fontSize='sm'>
                            {seriesEvent._source.commentary}
                        </Text>
                    </Box>
                </Box>
            </Center>
        </>
    );
};