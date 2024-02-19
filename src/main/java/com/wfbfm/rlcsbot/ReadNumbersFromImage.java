package com.wfbfm.rlcsbot;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadNumbersFromImage
{
    public static void main(String[] args) throws TesseractException
    {
        final List<String> fileNames = new ArrayList<>();
        fileNames.add("blueTeam");
        fileNames.add("orangeTeam");
        fileNames.add("bluePlayer1");
        fileNames.add("bluePlayer2");
        fileNames.add("bluePlayer3");
        fileNames.add("orangePlayer1");
        fileNames.add("orangePlayer2");
        fileNames.add("orangePlayer3");
        fileNames.add("bestOf");
        fileNames.add("clock");
        fileNames.add("gameNumber");
        fileNames.add("description");

        final List<String> numberFileNames = new ArrayList<>();
        numberFileNames.add("blueGameScore");
        numberFileNames.add("orangeGameScore");

        Tesseract textTesseract = new Tesseract();
        textTesseract.setDatapath("src/main/resources/tessdata");
        textTesseract.setLanguage("eng");
        textTesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SPARSE_TEXT);
        textTesseract.setOcrEngineMode(1);

        for (String fileName : fileNames)
        {
            final String filePath = "src/main/temp/processing/test-screenshot-" + fileName + ".png";
            File image = new File(filePath);
            String result = textTesseract.doOCR(image);
            System.out.println("Parsed data from " + fileName);
            System.out.println(result);
        }

        Tesseract numberTesseract = new Tesseract();
        numberTesseract.setDatapath("src/main/resources/tessdata");
        numberTesseract.setLanguage("eng");
        numberTesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
        numberTesseract.setOcrEngineMode(1);
        numberTesseract.setVariable("tessedit_char_whitelist", "0123456789:");

        for (String fileName : numberFileNames)
        {
            final String filePath = "src/main/temp/processing/test-screenshot-" + fileName + ".png";
            File image = new File(filePath);
            String result = numberTesseract.doOCR(image);
            System.out.println("Parsed data from " + fileName);
            System.out.println(result);
        }
    }
}
