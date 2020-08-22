package org.pytorch.helloworld;

class SSDSpec {
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

class SSDBoxSizes {
    private int min, max;

    public SSDBoxSizes(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}