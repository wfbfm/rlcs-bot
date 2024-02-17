package com.wfbfm.rlcsbot;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ImageMagickTextParser
{
    public static void main(String[] args) throws IM4JavaException
    {
        // Replace with the path to your image file
        final String imagePath = "src/main/resources/test-crop-just-score-grey-numbers.png";

        // Get the text from the image using ImageMagick's "convert" command
        String text = extractTextWithImageMagick(imagePath);

        System.out.println("Extracted Text:");
        System.out.println(text);
    }

    private static String extractTextWithImageMagick(String imagePath) throws IM4JavaException
    {
        // Path to the ImageMagick "convert" command
        String convertCommand = "convert";

        // Create a ConvertCmd object
        ConvertCmd cmd = new ConvertCmd();

        // Set the path to the "convert" command (if it's not in the system's PATH)
        cmd.setSearchPath(convertCommand);

        // Create an IMOperation
        IMOperation op = new IMOperation();

        // Specify the input image file
        op.addImage(imagePath);

        // Specify the output format and type
//        op.setOption("density", "300"); // Set the resolution (DPI) if needed
//        op.setOption("depth", "8"); // Set the color depth
//        op.setOption("type", "TrueColor"); // Set the color type
//        op.setOption("trim", null); // Trim whitespace from the image
//        op.setOption("enc", "UTF-8"); // Set the encoding

        // Add the OCR option to recognize text
        // op.addImage("txt:-"); // Output text to standard output

        try
        {
            // Execute the "convert" command
            cmd.run(op);

            // Read the text from the standard output
            Process process = Runtime.getRuntime().exec(convertCommand + " - -density 300 -depth 8 -type TrueColor -trim -enc UTF-8 " + imagePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                textBuilder.append(line).append("\n");
            }
            reader.close();

            return textBuilder.toString().trim();
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            return "";
        }
    }
}

