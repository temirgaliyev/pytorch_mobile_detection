package com.temirgaliyev.detection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.temirgaliyev.detection.Detection.AbstractDetection;
import com.temirgaliyev.detection.Detection.Box;
import com.temirgaliyev.detection.Detection.DetectionModelEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.temirgaliyev.detection.Utils.drawRectangles;
import static com.temirgaliyev.detection.Utils.getDetector;
import static com.temirgaliyev.detection.Utils.initUtils;
import static com.temirgaliyev.detection.Utils.millisToShortDHMS;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG = 0xAAA;
    private final String TAG = "MAIN_ACTIVITY";
    ImageView imageView;
    TextView inferenceTextView;
    Button changeModelButton;

    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

    DetectionModelEnum detectionModelEnum = DetectionModelEnum.SSD;
    AbstractDetection detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            runTesting(this, imageView);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        changeModelButton = findViewById(R.id.modelButton);
        imageView = findViewById(R.id.imageView);
        inferenceTextView = findViewById(R.id.inferenceTextView);

        bitmapOptions.inMutable = true;

        initUtils();
        detector = getDetector(this, detectionModelEnum);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"), null, bitmapOptions);
            predictAndShow(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error reading assets", e);
            finish();
        }
    }

    private void predictAndShow(Bitmap bitmap) {
        ArrayList<Box> boxes = detector.predict(bitmap);
        drawRectangles(boxes, bitmap);
        imageView.setImageBitmap(bitmap);
        inferenceTextView.setText(String.format("Inference time: %s", millisToShortDHMS(detector.getInferenceTime())));
    }

//    public void changeDetector() {
//        if (detectionModelEnum == DetectionModelEnum.DETR) {
//            if (detr == null) {
//                detr = new DETR();
//                try {
//                    detr.loadModule(this);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            detector = detr;
//            changeModelButton.setText(R.string.detr);
//        } else {
//            if (ssd == null) {
//                ssd = new SSD();
//                try {
//                    ssd.loadModule(this);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            detector = ssd;
//            changeModelButton.setText(R.string.ssd);
//        }
//    }
//

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream, null, bitmapOptions);
                assert selectedImage != null;
                predictAndShow(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }


    public void onGalleryClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }


    public void onModelClick(View view) {
        detectionModelEnum = detectionModelEnum.next();
        detector = getDetector(this, detectionModelEnum);
        Toast.makeText(this, String.format("Changed detector to: %s", detectionModelEnum), Toast.LENGTH_SHORT).show();
    }
}
