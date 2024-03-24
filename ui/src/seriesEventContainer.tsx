import React, { useEffect, useState } from 'react';
import SeriesEvent from './model/seriesEvent';
import { Box, Center, Divider, HStack, Stack, Text, VStack } from '@chakra-ui/react';
import Series from './model/series';
import { MinusIcon } from '@chakra-ui/icons';


const seriesScoreIcons = (seriesWinningGameScore: number, seriesScore: number, colour: string, filledFirst: boolean) => {
    const icons = [];

    const emptyIconCount = seriesWinningGameScore - seriesScore;

    for (let i = 1; i < seriesWinningGameScore + 1; i++) {
        const isFilled = filledFirst ? i <= seriesScore : i > emptyIconCount;
        const iconName = isFilled ? 'filledIcon' : 'emptyIcon';
        const iconColour = isFilled ? colour : 'grey';

        icons.push(
            <MinusIcon key={`${iconName}-${colour}-${i}`} name={iconName} color={iconColour} />
        );
    }
    return icons;
};

export const SeriesEventContainer: React.FC<{ seriesEvent: SeriesEvent, series: Series }> = ({ seriesEvent, series }) => {
    return (
        <Box maxW='500px' borderWidth='1px' borderRadius='lg' overflow='hidden'>
            <Box height='5px' bg='grey'></Box>
            <Box>
                <Box bg='gray.200' p={2}>
                    <HStack>
                        <VStack>
                            <Text as='b' fontSize='sm'>{series._source.blueTeam.teamName}</Text>
                            <HStack>
                                {seriesScoreIcons(series._source.seriesWinningGameScore, seriesEvent._source.seriesScore.blueScore, 'blue.500', false)}
                            </HStack>
                        </VStack>
                        <Box p={2} bg='blue.500' borderRadius='lg'>
                            <Text as='b' fontSize='m' color={'white'}>
                                {seriesEvent._source.currentGame.score.blueScore}
                            </Text>
                        </Box>
                        <Box p={2} bg='orange.300' borderRadius='lg'>
                            <Text as='b' fontSize='m' color={'white'}>
                                {seriesEvent._source.currentGame.score.orangeScore}
                            </Text>
                        </Box>
                        <VStack>
                            <Text as='b' fontSize='sm'>{series._source.orangeTeam.teamName}</Text>
                            <HStack>
                                {seriesScoreIcons(series._source.seriesWinningGameScore, seriesEvent._source.seriesScore.orangeScore, 'orange.300', true)}
                            </HStack>
                        </VStack>
                    </HStack>
                </Box>
                <Box bg='black.100' p={4}>
                    <Text fontSize='sm'>
                        {seriesEvent._source.commentary}
                    </Text>
                </Box>
            </Box>
        </Box>
    );
};