package com.temirgaliyev.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.temirgaliyev.detection.Detection.AbstractDetection;
import com.temirgaliyev.detection.Detection.Box;
import com.temirgaliyev.detection.Detection.DetectionModelEnum;

import java.util.ArrayList;

import static com.temirgaliyev.detection.Utils.drawRectangles;
import static com.temirgaliyev.detection.Utils.getDetector;
import static com.temirgaliyev.detection.Utils.rotateBitmap;
import static com.temirgaliyev.detection.Utils.getImageOrientation;

public class NonRealTimeDetectionActivity extends AppCompatActivity {

    private static final String TAG = "NON_RT_DETECTION";
//    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//    AbstractDetection detr;
//    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_non_real_time_detection);
        ImageView imageView = findViewById(R.id.imageView);
        TextView timeTextView = findViewById(R.id.inferenceTimeTextView);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        String filename = getExternalCacheDir() + "/pic.jpg";
        int orientation = getImageOrientation(filename);
        Bitmap bitmap = BitmapFactory.decodeFile(filename, bitmapOptions);
        Bitmap rotated = rotateBitmap(bitmap, orientation);

        AbstractDetection detr = getDetector(this, DetectionModelEnum.DETR);
        ArrayList<Box> boxes = detr.predict(rotated);
        drawRectangles(boxes, rotated);
        imageView.setImageBitmap(rotated);
        timeTextView.setText(String.format("%d ms", detr.getInferenceTime()));
    }

}
