package com.temirgaliyev.detection.SSD;

import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Tensor;
import com.temirgaliyev.detection.AbstractDetection;
import com.temirgaliyev.detection.Box;
import org.pytorch.torchvision.TensorImageUtils;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.temirgaliyev.detection.SSD.SSDConfig.IMAGE_SIZE;
import static com.temirgaliyev.detection.SSD.SSDConfig.IOU_THRESHOLD;
import static com.temirgaliyev.detection.SSD.SSDConfig.NORMALIZATION_MEAN;
import static com.temirgaliyev.detection.SSD.SSDConfig.NORMALIZATION_STD;
import static com.temirgaliyev.detection.SSD.SSDConfig.PROBABILITY_THRESHOLD;
import static com.temirgaliyev.detection.SSD.SSDConfig.CLASSES;
import static com.temirgaliyev.detection.Utils.resize;

public class SSD extends AbstractDetection {

    public SSD(){
        moduleName = "model_detection_ssd.pt";
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
        final long NUM_CLASSES = outputTensor[0].toTensor().shape()[2];
        ArrayList<Box> locations = postprocess(outputTensor, NUM_CLASSES);

        ArrayList<Box> boxes = SSDUtils.convertLocations2Boxes(locations, SSDConfig.PRIORS, SSDConfig.CENTER_VARIANCE, SSDConfig.SIZE_VARIANCE);

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
