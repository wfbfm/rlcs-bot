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


export const SeriesVictoryContainer: React.FC<{ seriesEvent: SeriesEvent, series: Series, logos: { [logoName: string]: string } }> = ({ seriesEvent, series, logos }) => {
    const { colorMode, toggleColorMode } = useColorMode();
    const commentaryColour = useColorModeValue('gray.100', 'gray.600')
    const blueBannerColour = useColorModeValue('blue.200', 'blue.600')
    const orangeBannerColour = useColorModeValue('orange.300', 'orange.400')
    const isLightMode = colorMode === 'light';
    const blueLogoName = getLogoName(series?._source.blueTeam.teamName, isLightMode);
    const orangeLogoName = getLogoName(series?._source.orangeTeam.teamName, isLightMode);
    const blueLogo = blueLogoName ? logos[blueLogoName] : undefined;
    const orangeLogo = orangeLogoName ? logos[orangeLogoName] : undefined;

    const winner = series._source.seriesScore.teamInLead;
    const teamName = winner === 'BLUE' ? series._source.blueTeam.teamName : series._source.orangeTeam.teamName;
    const logo = winner === 'BLUE' ? blueLogo : orangeLogo;
    const bannerBackground = winner === 'BLUE' ? blueBannerColour : orangeBannerColour;


    return (
        <>
            <Center width='100%' borderWidth='1px' borderRadius='lg' overflow='hidden'>
                <Box width='100%'>
                    <Center width='100%' bg={bannerBackground} p={2}>
                        <Center width='100%'>
                            <VStack alignItems='center'>
                                <Image src={`${logo}`} boxSize={10}></Image>
                                <Text as='b' fontSize='sm'>{`${teamName}`}</Text>
                            </VStack>
                            <Box width='5%'>
                            </Box>
                            <Text as='b' fontSize='m' align={'center'}>
                                SERIES VICTORY
                            </Text>
                        </Center>
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