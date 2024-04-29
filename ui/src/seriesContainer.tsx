import React, { useEffect, useState } from 'react';
import { Badge, Box, Center, Divider, HStack, Image, Icon, Stack, Text, VStack, useColorMode, Grid, GridItem, Flex, Spacer } from '@chakra-ui/react';
import Series from './model/series';

const getLogoName = (teamName: string | undefined, isLightMode: boolean): string | undefined => {
    if (!teamName) {
        return undefined;
    }
    const modeSuffix = isLightMode ? '_lightmode' : '_darkmode';
    return `${teamName}${modeSuffix}.png`
};


export const SeriesContainer: React.FC<{ series: Series, logos: { [logoName: string]: string } }> = ({ series, logos }) => {

    const blueColour: string = series._source.complete ? (series._source.seriesScore.teamInLead === 'BLUE' ? 'blue.500' : 'gray.400') : 'teal.300'
    const orangeColour: string = series._source.complete ? (series._source.seriesScore.teamInLead === 'ORANGE' ? 'orange.400' : 'gray.400') : 'teal.300'
    const { colorMode, toggleColorMode } = useColorMode();
    const isLightMode = colorMode === 'light';
    const blueLogoName = getLogoName(series?._source.blueTeam.teamName, isLightMode);
    const orangeLogoName = getLogoName(series?._source.orangeTeam.teamName, isLightMode);
    const blueLogo = blueLogoName ? logos[blueLogoName] : undefined;
    const orangeLogo = orangeLogoName ? logos[orangeLogoName] : undefined;

    return (
            <>
        <Box width='100%' borderWidth='1px' borderRadius='lg' overflow='hidden'>
            <HStack p={2} width='100%' justify="space-between">
                <Flex width='40%'>
                    <Center paddingRight={2}>
                        {blueLogo && (
                            <Image src={`${blueLogo}`} boxSize={6} mr={2}></Image>
                        )}
                        <Text as='b' fontSize='xs' mr={2}>
                            {series._source.blueTeam.teamName}
                        </Text>
                    </Center>
                </Flex>

                <HStack width='20%' justify="center">
                    <Box p={1.5} bg={blueColour} borderRadius='lg' mr={2}>
                        <Text as='b' fontSize='m'>
                            {series._source.seriesScore.blueScore}
                        </Text>
                    </Box>
                    <Box p={1.5} bg={orangeColour} borderRadius='lg' mr={2}>
                        <Text as='b' fontSize='m'>
                            {series._source.seriesScore.orangeScore}
                        </Text>
                    </Box>
                </HStack>

                <Flex width='40%'>
                    <Center>
                        <Text as='b' fontSize='xs' mr={2}>
                            {series._source.orangeTeam.teamName}
                        </Text>
                        {orangeLogo && (
                            <Image src={`${orangeLogo}`} boxSize={6} mr={2}></Image>
                        )}
                    </Center>
                </Flex>
            </HStack>
        </Box>
    </>
    );
};