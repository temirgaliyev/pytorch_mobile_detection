package com.temirgaliyev.detection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.temirgaliyev.detection.Detection.AbstractDetection;
import com.temirgaliyev.detection.Detection.Box;
import com.temirgaliyev.detection.Detection.DetectionModelEnum;

import org.pytorch.IValue;

import java.util.ArrayList;

import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_ACTION_DETECTION_DETR;
import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_ACTION_DOWNLOAD;
import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_ACTION_TYPE;
import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_FILE_URL;
import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_INPUT_FILENAME;
import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_OUTPUT_FILENAME;
import static com.temirgaliyev.detection.Utils.CAPTURED_IMAGE_NAME;
import static com.temirgaliyev.detection.Utils.drawRectangles;
import static com.temirgaliyev.detection.Utils.getDetector;
import static com.temirgaliyev.detection.Utils.millisToShortDHMS;
import static com.temirgaliyev.detection.Utils.rotateBitmap;
import static com.temirgaliyev.detection.Utils.getImageOrientation;

public class NonRealTimeDetectionActivity extends AppCompatActivity {

    private static final String TAG = "NON_RT_DETECTION";
    private static final int REQUEST_CODE_DETECTION_DETR = 101;
//    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//    AbstractDetection detr;
//    String filename;

    ImageView imageView;
    TextView timeTextView;
    long inferenceStartTime, inferenceEndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_non_real_time_detection);
        imageView = findViewById(R.id.imageView);
        timeTextView = findViewById(R.id.inferenceTimeTextView);

//        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//        bitmapOptions.inMutable = true;
//        String filename = getExternalCacheDir() + "/" + CAPTURED_IMAGE_NAME;
//        int orientation = getImageOrientation(filename);
//        Bitmap bitmap = BitmapFactory.decodeFile(filename, bitmapOptions);
//        Bitmap rotated = rotateBitmap(bitmap, orientation);
//
//        AbstractDetection detr = getDetector(this, DetectionModelEnum.DETR);
//        ArrayList<Box> boxes = detr.predict(rotated);
//        drawRectangles(boxes, rotated);

        inferenceStartTime = System.currentTimeMillis();

        Intent intent = new Intent(this, ProgressBarActivity.class);
        intent.putExtra(EXTRA_ACTION_TYPE, EXTRA_ACTION_DETECTION_DETR);
        intent.putExtra(EXTRA_INPUT_FILENAME, getExternalCacheDir() + "/" + Utils.CAPTURED_IMAGE_NAME);
        intent.putExtra(EXTRA_OUTPUT_FILENAME, getExternalCacheDir() + "/" + Utils.BBOXED_IMAGE_NAME);
        startActivityForResult(intent, REQUEST_CODE_DETECTION_DETR);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_DETECTION_DETR){
                inferenceEndTime = System.currentTimeMillis();
                Bitmap bitmap = BitmapFactory.decodeFile(getExternalCacheDir() + "/" + Utils.BBOXED_IMAGE_NAME);
                imageView.setImageBitmap(bitmap);
                timeTextView.setText(millisToShortDHMS(inferenceEndTime - inferenceStartTime));
//                timeTextView.setText(String.format("%d ms", inferenceEndTime - inferenceStartTime));
            }
        } else {
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
