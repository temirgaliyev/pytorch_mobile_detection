package com.temirgaliyev.detection.SSD;

public class SSDBoxSizes {
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