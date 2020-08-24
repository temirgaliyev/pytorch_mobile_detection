package org.pytorch.helloworld.SSD;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.helloworld.AbstractDetection;
import org.pytorch.helloworld.Box;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.pytorch.helloworld.Utils.assetFilePath;
import static org.pytorch.helloworld.Utils.scaleBitmapImage;

public class SSD extends AbstractDetection {
    Module module = null;

    public SSD(Context context) throws IOException {
        module = Module.load(assetFilePath(context, "model_detection.pt"));
    }

    @Override
    public ArrayList<Box> predict(Bitmap bitmap) {
        int originalImageWidth = bitmap.getWidth();
        int originalImageHeight = bitmap.getHeight();
        final float probabilityThreshold = 0.2f, iouThreshold = 0.45f;

        Bitmap scaledBitmap = scaleBitmapImage(bitmap, SSDConfig.size, SSDConfig.size);
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(scaledBitmap, SSDConfig.mean, SSDConfig.std);

        final IValue[] outputTensor = module.forward(IValue.from(inputTensor)).toTuple();

        float[] confidencesTensor = outputTensor[0].toTensor().getDataAsFloatArray();
        long[] confidencesShape = outputTensor[0].toTensor().shape();
        float[] locationsTensor = outputTensor[1].toTensor().getDataAsFloatArray();
        long[] locationsShape = outputTensor[1].toTensor().shape();

        ArrayList<Box> locations = new ArrayList<>();

        for (int i = 0; i < confidencesShape[1]; i++) {
            int confidencesFrom = (int) (i * confidencesShape[2]);
            int confidencesTo = (int) (confidencesFrom + confidencesShape[2]);

            float[] confidence = Arrays.copyOfRange(confidencesTensor, confidencesFrom, confidencesTo);
            float[] softmaxScores = SSDUtils.softmax(confidence);

            int locationsFrom = (int) (i * locationsShape[2]);
            Box location = Box.fromCentered(
                    locationsTensor[locationsFrom], locationsTensor[locationsFrom + 1],
                    locationsTensor[locationsFrom + 2], locationsTensor[locationsFrom + 3], softmaxScores);
            locations.add(location);
        }

        ArrayList<Box> boxes = SSDUtils.convertLocations2Boxes(locations, SSDConfig.priors, SSDConfig.centerVariance, SSDConfig.sizeVariance);

        ArrayList<Box> pickedBoxes = new ArrayList<>();
        for (int classIndex = 1; classIndex < confidencesShape[2]; classIndex++) {
            LinkedList<Box> boxesWithClassIndex = new LinkedList<>();
            for (Box box : boxes) {
                if (box.getMaxIndex() == classIndex && box.getMaxProbability() >= probabilityThreshold) {
                    boxesWithClassIndex.add(box);
                }
            }

            if (boxesWithClassIndex.isEmpty()) continue;

            ArrayList<Box> notSuppressedBoxes = SSDUtils.nonMaximumSuppression(boxesWithClassIndex, iouThreshold);
            pickedBoxes.addAll(notSuppressedBoxes);
        }

        for (Box box : pickedBoxes) {
            box.scaleBox(originalImageWidth, originalImageHeight);
        }

        return pickedBoxes;
    }

}
