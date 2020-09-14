package com.temirgaliyev.detection.Detection.TestingSSD;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.temirgaliyev.detection.Detection.Box;
import com.temirgaliyev.detection.Detection.SSD.SSDConfig;
import com.temirgaliyev.detection.Detection.SSD.SSDUtils;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.CLASSES;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.IMAGE_SIZE;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.IOU_THRESHOLD;
import static com.temirgaliyev.detection.Detection.SSD.SSDConfig.PROBABILITY_THRESHOLD;
import static com.temirgaliyev.detection.Utils.assetFilePath;
import static com.temirgaliyev.detection.Utils.resize;

public class Main {
    private static String TAG = "TESTING_MAIN";

    public static void runTesting(Context context, ImageView imageView) throws IOException {
        Module module = getModule(context, "trace_model2.pt");
        Bitmap bitmap = getBitmapFromAsset(context, "image.jpg");

        int originalImageWidth = bitmap.getWidth();
        int originalImageHeight = bitmap.getHeight();

////        ==========================================================================
//        final int pixelsCount = originalImageHeight * originalImageWidth ;
//        final int[] pixels = new int[pixelsCount];
//        bitmap.getPixels(pixels, 0, originalImageWidth, 0, 0,
//                originalImageWidth, originalImageHeight);
//
//        StringBuilder firstTen = new StringBuilder();
//        StringBuilder lastTen = new StringBuilder();
//        for (int i = 0; i < 10; i++) {
//            int c = pixels[i];
//            float r = ((c >> 16) & 0xff);
//            float g = ((c >> 8) & 0xff);
//            float b = ((c) & 0xff);
//
//            firstTen.append(String.format("[%s %s %s] ", r, g, b));
//        }
//
//        for (int i = pixelsCount-10; i < pixelsCount; i++) {
//            int c = pixels[i];
//            float r = ((c >> 16) & 0xff);
//            float g = ((c >> 8) & 0xff);
//            float b = ((c) & 0xff);
//
//            lastTen.append(String.format("[%s %s %s] ", r, g, b));
//        }
//
//        Log.d(TAG, String.format("First 10 Pixels: %s", firstTen));
//        Log.d(TAG, String.format("Last 10 Pixels: %s", lastTen));
////        ==========================================================================


        Bitmap scaledBitmap = resize(bitmap, IMAGE_SIZE, IMAGE_SIZE);
//        Bitmap scaledBitmap = bitmap;

//        float[] NORMALIZATION_MEAN = new float[]{0.498f, 0.498f, 0.498f};
//        float[] NORMALIZATION_STD = new float[]{0.50196f, 0.50196f, 0.50196f};
        float[] NORMALIZATION_MEAN = new float[]{128.0f/255.0f, 128.0f/255.0f, 128.0f/255.0f};
        float[] NORMALIZATION_STD  = new float[]{128.0f/255.0f, 128.0f/255.0f, 128.0f/255.0f};
//        float[] NORMALIZATION_MEAN = new float[]{0.0f, 0.0f, 0.0f};
//        float[] NORMALIZATION_STD = new float[]{128.0f, 128.0f, 1.0f};

        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(scaledBitmap,
                NORMALIZATION_MEAN,
                NORMALIZATION_STD);

//        ==========================================================================
        float[] floatArray = inputTensor.getDataAsFloatArray();
        StringBuilder firstTen = new StringBuilder();
        StringBuilder lastTen = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            firstTen.append(floatArray[i]).append(" ");
            lastTen.append(floatArray[floatArray.length - i -1]).append(" ");
        }

        Log.d(TAG, String.format("First 10 Scaled and Normalized Input: %s", firstTen));
        Log.d(TAG, String.format("Last 10 Scaled and Normalized Input: %s", lastTen));
//        ==========================================================================

        final IValue[] outputTensor = module.forward(IValue.from(inputTensor)).toTuple();

//        ==========================================================================
        floatArray = outputTensor[0].toTensor().getDataAsFloatArray();
        firstTen = new StringBuilder();
        lastTen = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            firstTen.append(floatArray[i]).append(" ");
            lastTen.append(floatArray[floatArray .length - i -1]).append(" ");
        }

        Log.d(TAG, String.format("First 10 Confidences: %s", firstTen));
        Log.d(TAG, String.format("Last 10 Confidences: %s", lastTen));
//        ==========================================================================

//        ==========================================================================
        floatArray = outputTensor[1].toTensor().getDataAsFloatArray();
        firstTen = new StringBuilder();
        lastTen = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            firstTen.append(floatArray[i]).append(" ");
            lastTen.append(floatArray[floatArray .length - i -1]).append(" ");
        }

        Log.d(TAG, String.format("First 10 Locations: %s", firstTen));
        Log.d(TAG, String.format("Last 10 Locations: %s", lastTen));
//        ==========================================================================

        ArrayList<Box> locations = postprocess(outputTensor);
        int nonzero = 0;
        for(Box location: locations){
            if (location.getMaxIndex() != 0){
                nonzero ++;
            }
        }
        Log.d(TAG, String.format("Nonzer: %d", nonzero));

        ArrayList<Box> boxes = SSDUtils.convertLocations2Boxes(locations, SSDConfig.PRIORS, SSDConfig.CENTER_VARIANCE, SSDConfig.SIZE_VARIANCE);

        ArrayList<Box> pickedBoxes = new ArrayList<>();
        for (int classIndex = 1; classIndex < outputTensor[0].toTensor().shape()[2]; classIndex++) {
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

    }

    public static ArrayList<Box> postprocess(IValue[] outputTensor){
        float[] confidencesTensor = outputTensor[0].toTensor().getDataAsFloatArray();
        long[] confidencesShape = outputTensor[0].toTensor().shape();
        float[] locationsTensor = outputTensor[1].toTensor().getDataAsFloatArray();
        long[] locationsShape = outputTensor[1].toTensor().shape();

        int NUM_CLASSES = (int)confidencesShape[2];
        ArrayList<Box> locations = new ArrayList<>();

        for (int i = 0; i < confidencesShape[1]; i++) {
//            int confidencesFrom = (int) (i * NUM_CLASSES);
//            int confidencesTo = (int) (confidencesFrom + NUM_CLASSES+1);
//            float[] confidence = Arrays.copyOfRange(confidencesTensor, confidencesFrom, confidencesTo);

            float[] softmaxScores = SSDUtils.softmax(confidencesTensor,
                    i * NUM_CLASSES, (i+1)*NUM_CLASSES);

            int locationsFrom = (int) (i * locationsShape[2]);
            Box location = Box.fromCentered(
                    locationsTensor[locationsFrom], locationsTensor[locationsFrom + 1],
                    locationsTensor[locationsFrom + 2], locationsTensor[locationsFrom + 3], softmaxScores);
            locations.add(location);
        }

        return locations;
    }

    public static Bitmap getBitmapFromAsset(Context context, String assetFileName) throws IOException {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        return BitmapFactory.decodeStream(context.getAssets().open(assetFileName), null, bitmapOptions);
    }

    public static Module getModule(Context context, String moduleName) throws IOException {
        return Module.load(assetFilePath(context, moduleName));
    }

    public static void bitmapToFloatBuffer(
            final Bitmap bitmap,
            final int x,
            final int y,
            final int width,
            final int height,
            final float[] normMeanRGB,
            final float[] normStdRGB,
            final FloatBuffer outBuffer,
            final int outBufferOffset) {

        final int pixelsCount = height * width;
        final int[] pixels = new int[pixelsCount];
        bitmap.getPixels(pixels, 0, width, x, y, width, height);
        final int offset_g = pixelsCount;
        final int offset_b = 2 * pixelsCount;
        for (int i = 0; i < pixelsCount; i++) {
            final int c = pixels[i];
            float r = ((c >> 16) & 0xff);
            float g = ((c >> 8) & 0xff) ;
            float b = ((c) & 0xff);
            float rF = (r - normMeanRGB[0]) / normStdRGB[0];
            float gF = (g - normMeanRGB[1]) / normStdRGB[1];
            float bF = (b - normMeanRGB[2]) / normStdRGB[2];
            outBuffer.put(outBufferOffset + i, rF);
            outBuffer.put(outBufferOffset + offset_g + i, gF);
            outBuffer.put(outBufferOffset + offset_b + i, bF);
        }
    }



}
