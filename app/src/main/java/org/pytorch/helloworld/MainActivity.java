package org.pytorch.helloworld;

import android.content.Context;
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

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private static final int RESULT_LOAD_IMG = 0xAAA;
  Bitmap bitmap = null;
  Module module = null;
  ImageView imageView;
  TextView textView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try {
      // creating bitmap from packaged into app android asset 'image.jpg',
      // app/src/main/assets/image.jpg
      bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"));
      // loading serialized torchscript module from packaged into app android asset model.pt,
      // app/src/model/assets/model.pt
      Log.e("DetectionFirstTry","before Module.load");
      module = Module.load(assetFilePath(this, "model_detection.pt"));
      Log.e("DetectionFirstTry","after Module.load");
    } catch (IOException e) {
      Log.e("PytorchHelloWorld", "Error reading assets", e);
      finish();
    }

    // showing image on UI
    imageView = findViewById(R.id.image);
    imageView.setImageBitmap(bitmap);
    String className = predictImageClasses(bitmap);

    // showing className on UI
    textView = findViewById(R.id.text);
    textView.setText(className);
  }

  public String predictImageClasses(Bitmap bitmap){
    // preparing input tensor
    final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);


    // running the model
    final IValue[] outputTensor = module.forward(IValue.from(inputTensor)).toTuple();
    Tensor scores = outputTensor[0].toTensor();
    Tensor boxes = outputTensor[1].toTensor();

    Log.d("OutputTensor", String.format("Scores shape: %s", Arrays.toString(scores.shape())));
//    Toast.makeText(this, Arrays.toString(outputTensor.shape()), Toast.LENGTH_SHORT).show();
//    Log.d("OutputTensor", Arrays.toString(outputTensor.getDataAsFloatArray()));

//    // getting tensor content as java array of floats
//    final float[] scores = outputTensor.getDataAsFloatArray();
//
//    // searching for the index with maximum score
//    float maxScore = -Float.MAX_VALUE;
//    int maxScoreIdx = -1;
//    for (int i = 0; i < scores.length; i++) {
//      if (scores[i] > maxScore) {
//        maxScore = scores[i];
//        maxScoreIdx = i;
//      }
//    }

    return "Class";

//    return ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx];
  }

  /**
   * Copies specified asset to the file in /files app directory and returns this file absolute path.
   *
   * @return absolute file path
   */
  public static String assetFilePath(Context context, String assetName) throws IOException {
    File file = new File(context.getFilesDir(), assetName);
    if (file.exists() && file.length() > 0) {
      return file.getAbsolutePath();
    }

    try (InputStream is = context.getAssets().open(assetName)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
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
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        String className = predictImageClasses(selectedImage);
        textView.setText(className);
        imageView.setImageBitmap(selectedImage);
        Toast.makeText(MainActivity.this, "Everything is ok!", Toast.LENGTH_LONG).show();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
      }

    }else {
      Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
    }
  }
}
