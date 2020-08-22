package org.pytorch.helloworld;

import java.util.ArrayList;

public class Config {
    public static final float[] mean = new float[]{0f, 0f, 0f};
    public static final float[] std = new float[]{1f, 1f, 1f};
    public static final float centerVariance = 0.1f;
    public static final float sizeVariance = 0.2f;

    public static final int size = 300;

    public static final SSDSpec[] specs = new SSDSpec[]{
            new SSDSpec(19, 16, new SSDBoxSizes(60, 105), new int[]{2, 3}),
            new SSDSpec(10, 32, new SSDBoxSizes(105, 150), new int[]{2, 3}),
            new SSDSpec(5, 64, new SSDBoxSizes(150, 195), new int[]{2, 3}),
            new SSDSpec(3, 100, new SSDBoxSizes(195, 240), new int[]{2, 3}),
            new SSDSpec(2, 150, new SSDBoxSizes(240, 285), new int[]{2, 3}),
            new SSDSpec(1, 300, new SSDBoxSizes(285, 330), new int[]{2, 3})
    };

    public static final ArrayList<Prior> priors = TensorUtils.generateSSDPriors(specs, size);

    public static String[] classes = new String[]{
            "BACKGROUND", "aeroplane", "bicycle", "bird", "boat", "bottle", "bus", "car",
            "cat", "chair", "cow", "diningtable", "dog", "horse", "motorbike", "person",
            "pottedplant", "sheep", "sofa", "train", "tvmonitor"
    };
}
