import React, { useEffect, useState } from 'react';
import SeriesEvent from './model/seriesEvent';
import { Box } from '@chakra-ui/react'; 

export const SeriesEventContainer: React.FC<{ seriesEvent: SeriesEvent }> = ({ seriesEvent }) => {
  return (
    <Box>
      {seriesEvent._source.commentary}
    </Box>
  );
};