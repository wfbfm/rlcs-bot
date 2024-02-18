package com.wfbfm.rlcsbot;

public class SubImageStrategy
{
    private String name;
    private int cropStartX;
    private int cropStartY;
    private int cropEndX;
    private int cropEndY;
    private boolean shouldInvertGreyscale;
    private boolean isWhiteOnBlue;
    private boolean isWhiteOnOrange;
    private int rgbComparisonBuffer;
    private int additionalBorderSize;

    public SubImageStrategy(final String name,
                            final int cropStartX,
                            final int cropStartY,
                            final int cropEndX,
                            final int cropEndY,
                            final boolean shouldInvertGreyscale,
                            final boolean isWhiteOnBlue,
                            final boolean isWhiteOnOrange,
                            final int rgbComparisonBuffer,
                            final int additionalBorderSize)
    {
        this.name = name;
        this.cropStartX = cropStartX;
        this.cropStartY = cropStartY;
        this.cropEndX = cropEndX;
        this.cropEndY = cropEndY;
        this.shouldInvertGreyscale = shouldInvertGreyscale;
        this.isWhiteOnBlue = isWhiteOnBlue;
        this.isWhiteOnOrange = isWhiteOnOrange;
        this.rgbComparisonBuffer = rgbComparisonBuffer;
        this.additionalBorderSize = additionalBorderSize;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public int getCropStartX()
    {
        return cropStartX;
    }

    public void setCropStartX(final int cropStartX)
    {
        this.cropStartX = cropStartX;
    }

    public int getCropStartY()
    {
        return cropStartY;
    }

    public void setCropStartY(final int cropStartY)
    {
        this.cropStartY = cropStartY;
    }

    public int getCropEndX()
    {
        return cropEndX;
    }

    public void setCropEndX(final int cropEndX)
    {
        this.cropEndX = cropEndX;
    }

    public int getCropEndY()
    {
        return cropEndY;
    }

    public void setCropEndY(final int cropEndY)
    {
        this.cropEndY = cropEndY;
    }

    public boolean shouldInvertGreyscale()
    {
        return shouldInvertGreyscale;
    }

    public void setShouldInvertGreyscale(final boolean shouldInvertGreyscale)
    {
        this.shouldInvertGreyscale = shouldInvertGreyscale;
    }

    public boolean isWhiteOnBlue()
    {
        return isWhiteOnBlue;
    }

    public void setWhiteOnBlue(final boolean whiteOnBlue)
    {
        isWhiteOnBlue = whiteOnBlue;
    }

    public boolean isWhiteOnOrange()
    {
        return isWhiteOnOrange;
    }

    public void setWhiteOnOrange(final boolean whiteOnOrange)
    {
        isWhiteOnOrange = whiteOnOrange;
    }

    public int getRgbComparisonBuffer()
    {
        return rgbComparisonBuffer;
    }

    public void setRgbComparisonBuffer(final int rgbComparisonBuffer)
    {
        this.rgbComparisonBuffer = rgbComparisonBuffer;
    }

    public int getAdditionalBorderSize()
    {
        return additionalBorderSize;
    }

    public void setAdditionalBorderSize(final int additionalBorderSize)
    {
        this.additionalBorderSize = additionalBorderSize;
    }
}
