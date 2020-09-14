package com.temirgaliyev.detection.Detection.SSD;

import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Tensor;
import com.temirgaliyev.detection.Detection.AbstractDetection;
import com.temirgaliyev.detection.Detection.Box;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.IMAGE_SIZE;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.IOU_THRESHOLD;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.NORMALIZATION_MEAN;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.NORMALIZATION_STD;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.PROBABILITY_THRESHOLD;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.CLASSES;
import static com.temirgaliyev.detection.Utils.resize;

public class SSD extends AbstractDetection {
    private static final String TAG = "SSD_CLASS";
    public final static String WEIGHTS_PATH_NAME  = "model_detection_ssd.pt";
    public final static String WEIGHTS_URL = "https://s3.eu-central-1.amazonaws.com/yela.static/model_detection_ssd.pt";

    public SSD(File externalCacheDir){
        modulePath = externalCacheDir + "/" + WEIGHTS_PATH_NAME;
    }

    @Override
    public Tensor transform(Bitmap bitmap) {
        Bitmap scaledBitmap = resize(bitmap, IMAGE_SIZE, IMAGE_SIZE);
        return TensorImageUtils.bitmapToFloat32Tensor(scaledBitmap,
                NORMALIZATION_MEAN,
                NORMALIZATION_STD);
    }

    @Override
    public ArrayList<Box> predict(Bitmap bitmap) {
        int originalImageWidth = bitmap.getWidth();
        int originalImageHeight = bitmap.getHeight();

        Tensor inputTensor = transform(bitmap);

        final IValue[] outputTensor = forward(inputTensor).toTuple();
        final int NUM_CLASSES = (int) outputTensor[0].toTensor().shape()[2];
        ArrayList<Box> locations = postprocess(outputTensor, NUM_CLASSES);

        ArrayList<Box> boxes = SSDUtils.convertLocations2Boxes(locations,
                SSDConfig.PRIORS, SSDConfig.CENTER_VARIANCE, SSDConfig.SIZE_VARIANCE);

        ArrayList<Box> pickedBoxes = new ArrayList<>();
        for (int classIndex = 1; classIndex < NUM_CLASSES; classIndex++) {
            LinkedList<Box> boxesWithClassIndex = new LinkedList<>();
            for (Box box : boxes) {
                if (box.getMaxIndex() == classIndex && box.getMaxProbability() >= PROBABILITY_THRESHOLD) {
                    boxesWithClassIndex.add(box);
                }
            }

            if (boxesWithClassIndex.isEmpty()) continue;

            ArrayList<Box> notSuppressedBoxes = SSDUtils.nonMaximumSuppression(boxesWithClassIndex, IOU_THRESHOLD);
            pickedBoxes.addAll(notSuppressedBoxes);
        }

        for (Box box : pickedBoxes) {
            box.setCls(CLASSES[box.getMaxIndex()]);
            box.scaleBox(originalImageWidth, originalImageHeight);
        }

        return pickedBoxes;
    }

}
