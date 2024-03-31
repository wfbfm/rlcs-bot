import React, { useEffect, useState } from 'react';
import SeriesEvent from './model/seriesEvent';
import { Badge, Box, Center, Divider, HStack, Icon, Stack, Text, VStack, useColorMode, useColorModeValue } from '@chakra-ui/react';
import Series from './model/series';
import { MinusIcon, TimeIcon } from '@chakra-ui/icons';
import { MdOutlineSportsScore } from "react-icons/md";
import { IoFootball } from "react-icons/io5";


const seriesScoreIcons = (seriesWinningGameScore: number, seriesScore: number, colour: string, filledFirst: boolean) => {
    const icons = [];

    const emptyIconCount = seriesWinningGameScore - seriesScore;

    for (let i = 1; i < seriesWinningGameScore + 1; i++) {
        const isFilled = filledFirst ? i <= seriesScore : i > emptyIconCount;
        const iconName = isFilled ? 'filledIcon' : 'emptyIcon';
        const iconColour = isFilled ? colour : 'grey';

        icons.push(
            <MinusIcon boxSize={6} key={`${iconName}-${colour}-${i}`} name={iconName} color={iconColour} />
        );
    }
    return icons;
};

function getBackgroundColour(evaluationResult: string, colourMode: string): string {
    if (evaluationResult === 'BLUE_GOAL') {
        return colourMode === 'light' ? 'blue.100' : 'blue.900';
    }
    if (evaluationResult === 'BLUE_GAME') {
        return colourMode === 'light' ? 'blue.200' : 'blue.800';
    }
    if (evaluationResult === 'ORANGE_GOAL') {
        return colourMode === 'light' ? 'orange.100' : 'orange.600';
    }
    if (evaluationResult === 'ORANGE_GAME') {
        return colourMode === 'light' ? 'orange.200' : 'orange.600';
    }
    return colourMode === 'light' ? 'gray.200' : 'gray.800';
}

export const SeriesEventContainer: React.FC<{ seriesEvent: SeriesEvent, series: Series }> = ({ seriesEvent, series }) => {
    const evaluationResult:string = seriesEvent._source.evaluation;
    const headerColour = useColorModeValue('gray.200', 'gray.800')
    const commentaryColour = useColorModeValue('gray.100', 'gray.600')

    return (
        <Center width='100%' borderWidth='1px' borderRadius='lg' overflow='hidden'>
            <Box width='100%'>
                <Center width='100%' bg={headerColour} p={2}>
                    <HStack>
                        <Center minW='20px'>
                        {evaluationResult === 'BLUE_GOAL' && <Icon boxSize={6} as={IoFootball} color={'blue.500'}></Icon>}
                        {evaluationResult === 'BLUE_GAME' && <Icon boxSize={6} as={MdOutlineSportsScore} color={'blue.500'}></Icon>}
                        </Center>
                        <VStack spacing={0}>
                            <Text as='b' fontSize='sm'>{series._source.blueTeam.teamName}</Text>
                            <HStack>
                                {seriesScoreIcons(series._source.seriesWinningGameScore, seriesEvent._source.seriesScore.blueScore, 'blue.500', false)}
                            </HStack>
                        </VStack>
                        <VStack>
                            <HStack>
                                <Box p={1.5} bg='blue.500' borderRadius='lg'>
                                    <Text as='b' fontSize='m' color={'white'}>
                                        {seriesEvent._source.currentGame.score.blueScore}
                                    </Text>
                                </Box>
                                <VStack p={2} spacing={0}>
                                    <TimeIcon boxSize={3.5} m={0}></TimeIcon>
                                    <Text as='b' fontSize='sm' m={0}>{seriesEvent._source.currentGame.clock.displayedTime}</Text>
                                </VStack>
                                <Box p={1.5} bg='orange.400' borderRadius='lg'>
                                    <Text as='b' fontSize='m' color={'white'}>
                                        {seriesEvent._source.currentGame.score.orangeScore}
                                    </Text>
                                </Box>
                            </HStack>
                            <HStack>
                            </HStack>
                        </VStack>
                        <VStack spacing={0}>
                            <Text as='b' fontSize='sm'>{series._source.orangeTeam.teamName}</Text>
                            <HStack>
                                {seriesScoreIcons(series._source.seriesWinningGameScore, seriesEvent._source.seriesScore.orangeScore, 'orange.400', true)}
                            </HStack>
                        </VStack>
                        <Center minW='20px'>
                        {evaluationResult === 'ORANGE_GOAL' && <Icon boxSize={6} as={IoFootball} color={'orange.400'}></Icon>}
                        {evaluationResult === 'ORANGE_GAME' && <Icon boxSize={6} as={MdOutlineSportsScore} color={'orange.400'}></Icon>}
                        </Center>
                    </HStack>
                </Center>
                <Box bg={commentaryColour} p={4}>
                    <Text fontSize='sm'>
                        {seriesEvent._source.commentary}
                    </Text>
                </Box>
            </Box>
        </Center>
    );
};