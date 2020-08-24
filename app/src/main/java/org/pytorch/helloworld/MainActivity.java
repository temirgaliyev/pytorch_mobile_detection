package org.pytorch.helloworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.helloworld.SSD.SSD;
import org.pytorch.helloworld.SSD.SSDConfig;
import org.pytorch.helloworld.SSD.SSDUtils;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.pytorch.helloworld.Utils.drawRectangles;
import static org.pytorch.helloworld.Utils.initUtils;
import static org.pytorch.helloworld.Utils.scaleBitmapImage;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 0xAAA;
    ImageView imageView;
    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
    AbstractDetection detection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        bitmapOptions.inMutable = true;

        initUtils();

        try {
            detection = new SSD(this);
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"), null, bitmapOptions);
            ArrayList<Box> boxes = detection.predict(bitmap);
            drawRectangles(boxes, bitmap);
            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            Log.e("MainActivity", "Error reading assets", e);
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
                ArrayList<Box> boxes = detection.predict(selectedImage);
                drawRectangles(boxes, selectedImage);
                imageView.setImageBitmap(selectedImage);

//                Toast.makeText(MainActivity.this, "Everything is ok!", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}
