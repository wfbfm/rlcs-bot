import React, { useEffect, useState } from 'react';
import { Badge, Box, Center, Divider, HStack, Icon, Stack, Text, VStack } from '@chakra-ui/react';
import Series from './model/series';


export const SeriesHeader: React.FC<{ series: Series | null }> = ({ series }) =>
{
    if (series != null)
    {
        const blueColour: string = series._source.complete ? (series._source.seriesScore.teamInLead === 'BLUE' ? 'blue.500' : 'gray.400') : 'teal.300'
        const orangeColour: string = series._source.complete ? (series._source.seriesScore.teamInLead === 'ORANGE' ? 'orange.400' : 'gray.400') : 'teal.300'

        return (
            <Box width='100%' borderWidth='1px' borderRadius='lg' overflow='hidden'>
                <Box>
                    <Center p={2}>
                        <HStack>
                            <Text as='b' fontSize='sm'>{series._source.blueTeam.teamName}</Text>
                            <HStack>
                                <Box p={1.5} bg={blueColour} borderRadius='lg'>
                                    <Text as='b' fontSize='m'>
                                        {series._source.seriesScore.blueScore}
                                    </Text>
                                </Box>
                                <Box p={1.5} bg={orangeColour} borderRadius='lg'>
                                    <Text as='b' fontSize='m'>
                                        {series._source.seriesScore.orangeScore}
                                    </Text>
                                </Box>
                            </HStack>
                            <Text as='b' fontSize='sm'>{series._source.orangeTeam.teamName}</Text>
                        </HStack>
                    </Center>
                </Box>
            </Box>
        );
    }
    return (<></>);
};