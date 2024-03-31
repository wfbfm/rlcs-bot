import React, { useEffect, useState } from 'react';
import { Badge, Box, Center, Divider, HStack, Image, Icon, Stack, Text, VStack, useColorMode, useColorModeValue } from '@chakra-ui/react';
import Series from './model/series';
import { MinusIcon, TimeIcon } from '@chakra-ui/icons';
import lightModeBlueLogo from './logos/Karmine Corp_lightmode.png'
import darkModeBlueLogo from './logos/Karmine Corp_darkmode.png'
import lightModeOrangeLogo from './logos/Team Falcons_default.png'
import darkModeOrangeLogo from './logos/Team Falcons_darkmode.png'

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

const fileExists = async (filePath: string) =>
{
    try
    {
        const response = await fetch(filePath);
        if (response.ok)
        {
            return true;
        } else
        {
            return false;
        }
    } catch (error)
    {
        return false;
    }
};

const getLogoPath = async (teamName: string | undefined, isLightMode: boolean): Promise<string | undefined> =>
{
    if (!teamName)
    {
        return undefined;
    }
    const modeSuffix = isLightMode ? '_lightmode' : '_darkmode';
    const modeSpecificFilePath = `/logos/${teamName}${modeSuffix}.png`;
    const defaultFilePath = `/logos/${teamName}_default.png`;
    if (await fileExists(modeSpecificFilePath))
    {
        console.log("file exists: ", modeSpecificFilePath)
        return modeSpecificFilePath;
    }
    else if (await fileExists(defaultFilePath))
    {
        console.log("file exists: ", defaultFilePath)
        return defaultFilePath;
    }
    return undefined;
};

export const SeriesHeader: React.FC<{ series: Series | null }> = ({ series }) =>
{
    const backgroundColour = useColorModeValue('gray.100', 'gray.900');
    const { colorMode, toggleColorMode } = useColorMode();
    const isLightMode = colorMode === 'light';

    const [blueLogoPath, setBlueLogoPath] = useState<string | undefined>(undefined);
    const [orangeLogoPath, setOrangeLogoPath] = useState<string | undefined>(undefined);

    useEffect(() => {
        if (series) {
            getLogoPath(series._source.blueTeam.teamName, isLightMode)
                .then(path => setBlueLogoPath(path))
                .catch(error => console.error('Error fetching blue logo:', error));

            getLogoPath(series._source.orangeTeam.teamName, isLightMode)
                .then(path => setOrangeLogoPath(path))
                .catch(error => console.error('Error fetching orange logo:', error));
        }
    }, [series, isLightMode]);

    if (series != null)
    {
        return (

            <Box width='100%' overflow='hidden'>
                <Box>
                    <Center bg={backgroundColour} p={2}>
                        <HStack>
                            {blueLogoPath ? <Image src={blueLogoPath} boxSize={10}></Image> : null}
                            <VStack spacing={0}>
                                <Text as='b' fontSize='sm'>{series._source.blueTeam.teamName}</Text>
                                <HStack>
                                    {seriesScoreIcons(series._source.seriesWinningGameScore, series._source.seriesScore.blueScore, 'blue.500', false)}
                                </HStack>
                            </VStack>
                            <VStack>
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
                                <HStack>
                                </HStack>
                            </VStack>
                            <VStack spacing={0}>
                                <Text as='b' fontSize='sm'>{series._source.orangeTeam.teamName}</Text>
                                <HStack>
                                    {seriesScoreIcons(series._source.seriesWinningGameScore, series._source.seriesScore.orangeScore, 'orange.400', true)}
                                </HStack>
                            </VStack>
                            {orangeLogoPath ? <Image src={orangeLogoPath} boxSize={10}></Image> : null}
                        </HStack>
                    </Center>
                </Box>
            </Box>
        );
    }
    return (<></>);
};