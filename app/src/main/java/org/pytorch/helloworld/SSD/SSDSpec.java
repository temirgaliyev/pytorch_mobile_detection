package org.pytorch.helloworld.SSD;


public class SSDSpec {
    private int featureMapSize;
    private int shrinkage;
    private SSDBoxSizes boxSizes;
    private int[] aspectRatios;

    public SSDSpec(int featureMapSize, int shrinkage, SSDBoxSizes boxSizes, int[] aspectRatios) {
        this.featureMapSize = featureMapSize;
        this.shrinkage = shrinkage;
        this.boxSizes = boxSizes;
        this.aspectRatios = aspectRatios;
    }

    public int getFeatureMapSize() {
        return featureMapSize;
    }

    public int getShrinkage() {
        return shrinkage;
    }

    public SSDBoxSizes getBoxSizes() {
        return boxSizes;
    }

    public int[] getAspectRatios() {
        return aspectRatios;
    }
}
