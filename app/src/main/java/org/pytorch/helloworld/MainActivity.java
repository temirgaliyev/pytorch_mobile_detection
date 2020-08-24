package org.pytorch.helloworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.pytorch.helloworld.DETR.DETR;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.pytorch.helloworld.Utils.drawRectangles;
import static org.pytorch.helloworld.Utils.initUtils;
import static org.pytorch.helloworld.Utils.millisToShortDHMS;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MAIN_ACTIVITY";

    private static final int RESULT_LOAD_IMG = 0xAAA;
    ImageView imageView;
    TextView textView;
    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
    AbstractDetection detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        bitmapOptions.inMutable = true;

        initUtils();
//        detector = new SSD();
        detector = new DETR();

        try {
            detector.loadModule(this);
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"), null, bitmapOptions);
            ArrayList<Box> boxes = detector.predict(bitmap);
            drawRectangles(boxes, bitmap);
            imageView.setImageBitmap(bitmap);
            textView.setText(String.format("Inference time: %s", millisToShortDHMS(detector.getInferenceTime())));

        } catch (IOException e) {
            Log.e(TAG, "Error reading assets", e);
            finish();
        }
    }



    public void onGalleryClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

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
                ArrayList<Box> boxes = detector.predict(selectedImage);
                drawRectangles(boxes, selectedImage);
                imageView.setImageBitmap(selectedImage);
                textView.setText(String.format("Inference time: %s", millisToShortDHMS(detector.getInferenceTime())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}
