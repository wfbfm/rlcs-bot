package com.wfbfm.rlcsbot;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class ReadNumbersFromImage
{
    public static void main(String[] args) throws TesseractException
    {
        final String fileName = "orangeScore";
        final String filePath = "src/main/resources/" + fileName + ".png";
        tryFullColour(filePath);
    }

    private static void tryFullColour(final String filePath) throws TesseractException
    {
        File image = new File(filePath);
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SPARSE_TEXT);
        tesseract.setOcrEngineMode(1);
        // tesseract.setVariable("tessedit_char_whitelist", "0123456789:");
        String result = tesseract.doOCR(image);
        System.out.println(result);
    }
}
