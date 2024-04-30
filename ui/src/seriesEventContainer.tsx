import React, { useEffect, useState } from 'react';
import SeriesEvent from './model/seriesEvent';
import { Badge, Box, Center, Divider, Flex, HStack, Icon, Image, Spacer, Stack, Text, VStack, useColorMode, useColorModeValue } from '@chakra-ui/react';
import Series from './model/series';
import { MinusIcon, TimeIcon } from '@chakra-ui/icons';
import { MdOutlineSportsScore } from "react-icons/md";
import { IoFootball } from "react-icons/io5";


const seriesScoreIcons = (seriesWinningGameScore: number, seriesScore: number, colour: string, filledFirst: boolean, isMobile: boolean | undefined) =>
{
    const icons = [];

    const emptyIconCount = seriesWinningGameScore - seriesScore;

    for (let i = 1; i < seriesWinningGameScore + 1; i++)
    {
        const isFilled = filledFirst ? i <= seriesScore : i > emptyIconCount;
        const iconName = isFilled ? 'filledIcon' : 'emptyIcon';
        const iconColour = isFilled ? colour : 'grey';

        icons.push(
            <MinusIcon boxSize={isMobile ? 4 : 6} key={`${iconName}-${colour}-${i}`} name={iconName} color={iconColour} />
        );
    }
    return icons;
};

const gameScoreDisplay = (seriesEvent: SeriesEvent) => {
    if (seriesEvent._source.currentGame && seriesEvent._source.currentGame.score != null) {
        return (

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
        )
    }
    return (<></>);
}

const getLogoName = (teamName: string | undefined, isLightMode: boolean): string | undefined => {
    if (!teamName) {
        return undefined;
    }
    const modeSuffix = isLightMode ? '_lightmode' : '_darkmode';
    return `${teamName}${modeSuffix}.png`
};


export const SeriesEventContainer: React.FC<{
    seriesEvent: SeriesEvent, series: Series, logos: { [logoName: string]: string },
    isMobile: boolean | undefined
}> = ({ seriesEvent, series, logos, isMobile }) => {
    const evaluationResult: string = seriesEvent._source.evaluation;
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
        <HStack height='100%'>
            <Center width={isMobile ? '0%' : '10%'} height='100%'>
                {evaluationResult === 'BLUE_GAME' && blueLogo && !isMobile &&
                    <Flex direction='column' height='100%' alignItems={'center'} bg={`${blueBannerColour}`} borderWidth='1px' borderRadius='lg' overflow='hidden'>
                        <Spacer height='30%'></Spacer>
                        <Image src={`${blueLogo}`} boxSize={10}></Image>
                        <Badge variant='solid' colorScheme='blue' p={1} marginTop={4}>GAME WIN</Badge>
                        <Spacer height='30%'></Spacer>
                    </Flex>
                }
            </Center>
            <Center width={isMobile ? '100%' : '80%'} borderWidth='1px' borderRadius='lg' overflow='hidden'>
                <Box width='100%'>
                    <Center width='100%' bg={headerColour} p={2}>
                        <HStack>
                            <Center minW='20px'>
                                {evaluationResult === 'BLUE_GOAL' && <Icon boxSize={isMobile ? 4 : 6} as={IoFootball} color={'blue.500'}></Icon>}
                                {evaluationResult === 'BLUE_GAME' && <Icon boxSize={isMobile ? 4 : 6} as={MdOutlineSportsScore} color={'blue.500'}></Icon>}
                            </Center>
                            <VStack spacing={0}>
                                <Text as='b' fontSize={isMobile ? 'xs' : 'sm'}>{series._source.blueTeam.teamName}</Text>
                                <HStack>
                                    {seriesScoreIcons(series._source.seriesWinningGameScore, seriesEvent._source.seriesScore.blueScore, 'blue.500', false, isMobile)}
                                </HStack>
                            </VStack>
                            {gameScoreDisplay(seriesEvent)}
                            <VStack spacing={0}>
                                <Text as='b' fontSize={isMobile ? 'xs' : 'sm'}>{series._source.orangeTeam.teamName}</Text>
                                <HStack>
                                    {seriesScoreIcons(series._source.seriesWinningGameScore, seriesEvent._source.seriesScore.orangeScore, 'orange.400', true, isMobile)}
                                </HStack>
                            </VStack>
                            <Center minW='20px'>
                                {evaluationResult === 'ORANGE_GOAL' && <Icon boxSize={isMobile ? 4 : 6} as={IoFootball} color={'orange.400'}></Icon>}
                                {evaluationResult === 'ORANGE_GAME' && <Icon boxSize={isMobile ? 4 : 6} as={MdOutlineSportsScore} color={'orange.400'}></Icon>}
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
            <Center width={isMobile ? '0%' : '10%'} height='100%'>
                {evaluationResult === 'ORANGE_GAME' && orangeLogo && !isMobile &&
                    <Flex direction='column' height='100%' alignItems={'center'} bg={`${orangeBannerColour}`} borderWidth='1px' borderRadius='lg' overflow='hidden'>
                        <Spacer height='30%'></Spacer>
                        <Image src={`${orangeLogo}`} boxSize={10}></Image>
                        <Badge variant='solid' colorScheme='orange' p={1} marginTop={4}>GAME WIN</Badge>
                        <Spacer height='30%'></Spacer>
                    </Flex>
                }
            </Center>
        </HStack>
    );
};