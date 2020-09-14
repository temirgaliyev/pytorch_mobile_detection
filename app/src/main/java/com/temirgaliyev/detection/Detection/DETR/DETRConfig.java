package com.temirgaliyev.detection.Detection.DETR;

public class DETRConfig {

    static final float[] NORMALIZATION_MEAN = new float[]{0.485f, 0.456f, 0.406f};
    static final float[] NORMALIZATION_STD = new float[]{0.229f, 0.224f, 0.225f};
    static final int IMAGE_SIZE = 256;
    static final float PROBABILITY_THRESHOLD = 0.7f;

    static final String[] CLASSES = new String[]{
            "N/A", "person", "bicycle", "car", "motorcycle", "airplane", "bus",
            "train", "truck", "boat", "traffic light", "fire hydrant", "N/A",
            "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse",
            "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "N/A", "backpack",
            "umbrella", "N/A", "N/A", "handbag", "tie", "suitcase", "frisbee", "skis",
            "snowboard", "sports ball", "kite", "baseball bat", "baseball glove",
            "skateboard", "surfboard", "tennis racket", "bottle", "N/A", "wine glass",
            "cup", "fork", "knife", "spoon", "bowl", "banana", "apple", "sandwich",
            "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake",
            "chair", "couch", "potted plant", "bed", "N/A", "dining table", "N/A",
            "N/A", "toilet", "N/A", "tv", "laptop", "mouse", "remote", "keyboard",
            "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "N/A",
            "book", "clock", "vase", "scissors", "teddy bear", "hair drier", "toothbrush",
            "BACKGROUND"
    };

}
