package org.pytorch.helloworld.DETR;

import android.graphics.Bitmap;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.Tensor;
import org.pytorch.helloworld.AbstractDetection;
import org.pytorch.helloworld.Box;
import org.pytorch.helloworld.SSD.SSDUtils;
import org.pytorch.torchvision.TensorImageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.pytorch.helloworld.DETR.DETRConfig.IMAGE_SIZE;
import static org.pytorch.helloworld.DETR.DETRConfig.NORMALIZATION_MEAN;
import static org.pytorch.helloworld.DETR.DETRConfig.NORMALIZATION_STD;
import static org.pytorch.helloworld.DETR.DETRConfig.CLASSES;
import static org.pytorch.helloworld.DETR.DETRConfig.PROBABILITY_THRESHOLD;
import static org.pytorch.helloworld.Utils.resize;

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
        final long NUM_CLASSES = outputTensor[0].toTensor().shape()[2];
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
