import React, { useEffect, useState } from 'react';
import { Badge, Box, Center, Divider, HStack, Image, Icon, Stack, Text, VStack, useColorMode, useColorModeValue } from '@chakra-ui/react';
import Series from './model/series';
import { MinusIcon, TimeIcon } from '@chakra-ui/icons';

const seriesScoreIcons = (seriesWinningGameScore: number, seriesScore: number, colour: string, filledFirst: boolean) =>
{
    const icons = [];

    const emptyIconCount = seriesWinningGameScore - seriesScore;

    for (let i = 1; i < seriesWinningGameScore + 1; i++)
    {
        const isFilled = filledFirst ? i <= seriesScore : i > emptyIconCount;
        const iconName = isFilled ? 'filledIcon' : 'emptyIcon';
        const iconColour = isFilled ? colour : 'grey';

        icons.push(
            <MinusIcon boxSize={6} key={`${iconName}-${colour}-${i}`} name={iconName} color={iconColour} />
        );
    }
    return icons;
};

const gameScoreDisplay = (series: Series) =>
{
    if (series._source.currentGame != null && series._source.currentGame.score != null)
    {
        return (
            <HStack>
                <Box p={1.5} bg='blue.500' borderRadius='lg'>
                    <Text as='b' fontSize='m' color={'white'}>
                        {series._source.currentGame.score.blueScore}
                    </Text>
                </Box>
                <VStack p={2} spacing={0}>
                    <TimeIcon boxSize={3.5} m={0}></TimeIcon>
                    <Text as='b' fontSize='sm' m={0}>{series._source.currentGame.clock.displayedTime}</Text>
                </VStack>
                <Box p={1.5} bg='orange.400' borderRadius='lg'>
                    <Text as='b' fontSize='m' color={'white'}>
                        {series._source.currentGame.score.orangeScore}
                    </Text>
                </Box>
            </HStack>
        )
    }
    return (<></>);
}

const getLogoName = (teamName: string | undefined, isLightMode: boolean): string | undefined =>
{
    if (!teamName)
    {
        return undefined;
    }
    const modeSuffix = isLightMode ? '_lightmode' : '_darkmode';
    return `${teamName}${modeSuffix}.png`
};

export const SeriesHeader: React.FC<{ series: Series | null, logos: { [logoName: string]: string } }> = ({ series, logos }) =>
{
    const backgroundColour = useColorModeValue('gray.100', 'gray.900');
    const { colorMode, toggleColorMode } = useColorMode();
    const isLightMode = colorMode === 'light';
    const blueLogoName = getLogoName(series?._source.blueTeam.teamName, isLightMode);
    const orangeLogoName = getLogoName(series?._source.orangeTeam.teamName, isLightMode);
    const blueLogo = blueLogoName ? logos[blueLogoName] : undefined;
    const orangeLogo = orangeLogoName ? logos[orangeLogoName] : undefined;

    if (series != null)
    {
        return (

            <Box width='100%' overflow='hidden'>
                <Box>
                    <Center bg={backgroundColour} p={2}>
                        <HStack>
                            {blueLogo ? <Image src={`${blueLogo}`} boxSize={10}></Image> : null}
                            <VStack spacing={0}>
                                <Text as='b' fontSize='sm'>{series._source.blueTeam.teamName}</Text>
                                <HStack>
                                    {seriesScoreIcons(series._source.seriesWinningGameScore, series._source.seriesScore.blueScore, 'blue.500', false)}
                                </HStack>
                            </VStack>
                            {gameScoreDisplay(series)}
                            <VStack spacing={0}>
                                <Text as='b' fontSize='sm'>{series._source.orangeTeam.teamName}</Text>
                                <HStack>
                                    {seriesScoreIcons(series._source.seriesWinningGameScore, series._source.seriesScore.orangeScore, 'orange.400', true)}
                                </HStack>
                            </VStack>
                            {orangeLogo ? <Image src={`${orangeLogo}`} boxSize={10}></Image> : null}
                        </HStack>
                    </Center>
                </Box>
            </Box>
        );
    }
    return (<></>);
};