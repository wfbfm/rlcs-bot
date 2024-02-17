package com.wfbfm.rlcsbot;

import com.opencsv.exceptions.CsvValidationException;

public class ImageUtilsImpl
{
    public static void main(String[] args) throws CsvValidationException
    {
        ImageProcessorUtils.processImages("src/main/resources/test-screenshot.png", "src/main/resources/crops.csv");
    }
}
