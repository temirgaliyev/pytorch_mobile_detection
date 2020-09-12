package com.temirgaliyev.detection.DETR;

import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Tensor;
import com.temirgaliyev.detection.AbstractDetection;
import com.temirgaliyev.detection.Box;

import org.pytorch.torchvision.TensorImageUtils;

import java.util.ArrayList;

import static com.temirgaliyev.detection.DETR.DETRConfig.IMAGE_SIZE;
import static com.temirgaliyev.detection.DETR.DETRConfig.NORMALIZATION_MEAN;
import static com.temirgaliyev.detection.DETR.DETRConfig.NORMALIZATION_STD;
import static com.temirgaliyev.detection.DETR.DETRConfig.CLASSES;
import static com.temirgaliyev.detection.DETR.DETRConfig.PROBABILITY_THRESHOLD;
import static com.temirgaliyev.detection.Utils.resize;

public class DETR extends AbstractDetection {
    private final String TAG = "DETR_CLASS";


    public DETR(){
        moduleName = "model_detection_detr.pt";
    }

    @Override
    public Tensor transform(Bitmap bitmap) {
        Bitmap scaledBitmap = resize(bitmap, IMAGE_SIZE);
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
        ArrayList<Box> boxes = postprocess(outputTensor, NUM_CLASSES);

        ArrayList<Box> pickedBoxes = new ArrayList<>();

        for (Box box : boxes) {
            if (box.getMaxProbability() >= PROBABILITY_THRESHOLD && box.getMaxIndex() != 91){
                pickedBoxes.add(box);
                box.setCls(CLASSES[box.getMaxIndex()]);
                box.scaleBox(originalImageWidth, originalImageHeight);
            }
        }

        return pickedBoxes;
    }

}
