package org.pytorch.helloworld;

import android.content.Context;
import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.helloworld.SSD.SSDUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.pytorch.helloworld.Utils.assetFilePath;

public abstract class AbstractDetection {
    private Module module;
    private long inferenceTime;
    protected String moduleName;

    public void loadModule(Context context) throws IOException {
        module = Module.load(assetFilePath(context, moduleName));
    }

    public IValue forward(Tensor inputTensor){
        long startTime = System.currentTimeMillis();
        IValue output = module.forward(IValue.from(inputTensor));
        long endTime = System.currentTimeMillis();
        inferenceTime = endTime - startTime;
        return output;
    }

    public ArrayList<Box> postprocess(IValue[] outputTensor, long NUM_CLASSES){
        float[] confidencesTensor = outputTensor[0].toTensor().getDataAsFloatArray();
        long[] confidencesShape = outputTensor[0].toTensor().shape();
        float[] locationsTensor = outputTensor[1].toTensor().getDataAsFloatArray();
        long[] locationsShape = outputTensor[1].toTensor().shape();

        ArrayList<Box> locations = new ArrayList<>();

        for (int i = 0; i < confidencesShape[1]; i++) {
            int confidencesFrom = (int) (i * NUM_CLASSES);
            int confidencesTo = (int) (confidencesFrom + NUM_CLASSES);

            float[] confidence = Arrays.copyOfRange(confidencesTensor, confidencesFrom, confidencesTo);
            float[] softmaxScores = SSDUtils.softmax(confidence);

            int locationsFrom = (int) (i * locationsShape[2]);
            Box location = Box.fromCentered(
                    locationsTensor[locationsFrom], locationsTensor[locationsFrom + 1],
                    locationsTensor[locationsFrom + 2], locationsTensor[locationsFrom + 3], softmaxScores);
            locations.add(location);
        }

        return locations;

    }


    public abstract Tensor transform(Bitmap bitmap);

    public abstract ArrayList<Box> predict(Bitmap bitmap);

    public long getInferenceTime() {
        return inferenceTime;
    }
}
