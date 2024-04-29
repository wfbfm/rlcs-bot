import React, { useEffect, useState } from 'react';
import { Badge, Box, Center, Divider, HStack, Image, Icon, Stack, Text, VStack, useColorMode, Grid, GridItem, Flex, Spacer, useColorModeValue } from '@chakra-ui/react';
import Series from './model/series';

const getLogoName = (teamName: string | undefined, isLightMode: boolean): string | undefined => {
    if (!teamName) {
        return undefined;
    }
    const modeSuffix = isLightMode ? '_lightmode' : '_darkmode';
    return `${teamName}${modeSuffix}.png`
};


export const SeriesContainer: React.FC<{ series: Series, logos: { [logoName: string]: string } }> = ({ series, logos }) => {

    const { colorMode, toggleColorMode } = useColorMode();
    const isLightMode = colorMode === 'light';
    const defaultColour: string = useColorModeValue('gray.400', 'gray.700');
    const blueColour: string = series._source.complete ? (series._source.seriesScore.teamInLead === 'BLUE' ? 'blue.500' : defaultColour) : defaultColour;
    const orangeColour: string = series._source.complete ? (series._source.seriesScore.teamInLead === 'ORANGE' ? 'orange.400' : defaultColour) : defaultColour;
    const blueLogoName = getLogoName(series?._source.blueTeam.teamName, isLightMode);
    const orangeLogoName = getLogoName(series?._source.orangeTeam.teamName, isLightMode);
    const blueLogo = blueLogoName ? logos[blueLogoName] : undefined;
    const orangeLogo = orangeLogoName ? logos[orangeLogoName] : undefined;

    return (
        <>
            <Box width='100%' overflow='hidden' p={2} borderRadius={'lg'} border={'1px solid gray'}>
                <Flex>
                    <Flex width='40%'>
                        <Center>
                            {blueLogo && (
                                <Image src={`${blueLogo}`} boxSize={6} mr={2}></Image>
                            )}
                        </Center>
                        <Spacer></Spacer>
                        <Center paddingRight={2}>
                            <Center>
                                <Text as='b' fontSize='xs' mr={2} alignItems='center'>
                                    {series._source.blueTeam.teamName}
                                </Text>
                            </Center>
                        </Center>
                    </Flex>

                    <Spacer />

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

                    <Spacer />

                    <Flex width='40%'>
                        <Center paddingLeft={2}>
                            <Text as='b' fontSize='xs' mr={2}>
                                {series._source.orangeTeam.teamName}
                            </Text>
                        </Center>
                        <Spacer>                        </Spacer>
                        <Center>
                            {orangeLogo && (
                                <Image src={`${orangeLogo}`} boxSize={6} mr={2}></Image>
                            )}
                        </Center>
                    </Flex>
                </Flex>
            </Box>
        </>
    );
};