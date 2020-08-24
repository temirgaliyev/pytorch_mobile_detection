package org.pytorch.helloworld.SSD;

import java.util.ArrayList;

public class SSDConfig {
    static final float[] NORMALIZATION_MEAN = new float[]{0f, 0f, 0f};
    static final float[] NORMALIZATION_STD = new float[]{1f, 1f, 1f};
    static final float CENTER_VARIANCE = 0.1f;
    static final float SIZE_VARIANCE = 0.2f;
    static final float PROBABILITY_THRESHOLD = 0.2f;
    static final float IOU_THRESHOLD = 0.45f;

    static final int IMAGE_SIZE = 300;

    private static final SSDSpec[] SPECS = new SSDSpec[]{
            new SSDSpec(19, 16, new SSDBoxSizes(60, 105), new int[]{2, 3}),
            new SSDSpec(10, 32, new SSDBoxSizes(105, 150), new int[]{2, 3}),
            new SSDSpec(5, 64, new SSDBoxSizes(150, 195), new int[]{2, 3}),
            new SSDSpec(3, 100, new SSDBoxSizes(195, 240), new int[]{2, 3}),
            new SSDSpec(2, 150, new SSDBoxSizes(240, 285), new int[]{2, 3}),
            new SSDSpec(1, 300, new SSDBoxSizes(285, 330), new int[]{2, 3})
    };

    static final ArrayList<Prior> PRIORS = SSDUtils.generateSSDPriors(SPECS, IMAGE_SIZE);

    static final String[] CLASSES = new String[]{
            "BACKGROUND", "aeroplane", "bicycle", "bird", "boat", "bottle", "bus", "car",
            "cat", "chair", "cow", "diningtable", "dog", "horse", "motorbike", "person",
            "pottedplant", "sheep", "sofa", "train", "tvmonitor"
    };
}
