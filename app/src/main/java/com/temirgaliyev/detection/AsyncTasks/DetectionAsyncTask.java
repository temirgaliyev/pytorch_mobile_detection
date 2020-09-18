package com.temirgaliyev.detection.AsyncTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.temirgaliyev.detection.Detection.AbstractDetection;
import com.temirgaliyev.detection.Detection.Box;
import com.temirgaliyev.detection.Detection.DetectionModelEnum;

import java.io.OutputStream;
import java.util.ArrayList;

import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_ACTION_DETECTION_DETR;
import static com.temirgaliyev.detection.Utils.drawRectangles;
import static com.temirgaliyev.detection.Utils.getDetector;
import static com.temirgaliyev.detection.Utils.getImageOrientation;
import static com.temirgaliyev.detection.Utils.rotateBitmap;
import static com.temirgaliyev.detection.Utils.saveBitmap;

public class DetectionAsyncTask extends AbstractAsyncTask {
    private static final String TAG = "DETECTION_ASYNC_TASK";

    public DetectionAsyncTask(AppCompatActivity activity, ProgressBar progressBar, TextView statusTextView) {
        super(activity, progressBar, statusTextView);
    }

    @Override
    protected String doInBackground(String[] params) {
        String inputFilename = params[0];
        String outputFilename = params[1];
        if (params[2].equals(EXTRA_ACTION_DETECTION_DETR)){
            detectionDETR(inputFilename, outputFilename);
        }

        return null;
    }

    private void detectionDETR(String inputFilename, String outputFilename){
        setTextUI("Loading image...");
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        int orientation = getImageOrientation(inputFilename);
        Bitmap bitmap = BitmapFactory.decodeFile(inputFilename, bitmapOptions);
        Bitmap rotated = rotateBitmap(bitmap, orientation);

        setTextUI("Loading detector weights...");
        AbstractDetection detr = getDetector(getContext(), DetectionModelEnum.DETR);
        setTextUI("Predicting...");
        ArrayList<Box> boxes = detr.predict(rotated);
        setTextUI("Drawing rectangles...");
        drawRectangles(boxes, rotated);
        setTextUI("Saving...");
        saveBitmap(rotated, outputFilename);
    }

}
