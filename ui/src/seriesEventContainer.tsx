import React, { useEffect, useState } from 'react';
import SeriesEvent from './model/seriesEvent';
import { Box } from '@chakra-ui/react';
import Series from './model/series';

export const SeriesEventContainer: React.FC<{ seriesEvent: SeriesEvent , series: Series}> = ({ seriesEvent, series }) => {
    return (
        <Box maxW='sm' borderWidth='1px' borderRadius='lg' overflow='hidden'>
            <Box bg='yellow.100' p={4}>
                {seriesEvent._source.eventId}
            </Box>
            <Box bg='red.100' p={4}>
                {series._source.blueTeam.teamName} - {series._source.orangeTeam.teamName}
            </Box>
            <Box bg='cyan.100' p={4}>
                {seriesEvent._source.seriesScore.blueScore} - {seriesEvent._source.seriesScore.orangeScore}
            </Box>
            <Box bg='green.100' p={4}>
                {seriesEvent._source.currentGame.score.blueScore} - {seriesEvent._source.currentGame.score.orangeScore}
            </Box>
            <Box bg='black.100' p={4}>
                {seriesEvent._source.commentary}
            </Box>
        </Box>
    );
};