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
import java.util.LinkedList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private static final int RESULT_LOAD_IMG = 0xAAA;
  Module module = null;
  ImageView imageView;
  TextView textView;

  Paint paint = new Paint();
  BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    imageView = findViewById(R.id.image);
    textView = findViewById(R.id.text);

    paint.setStyle(Paint.Style.STROKE);
    paint.setColor(Color.RED);
    bitmapOptions.inMutable = true;

    try {
      Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"), null, bitmapOptions);

      Log.d("MainActivity","Module loading...");
      module = Module.load(assetFilePath(this, "model_detection.pt"));
      Log.d("MainActivity","Module loaded");

      ArrayList<Box> boxes = predictImageClasses(bitmap);
      drawRectangles(boxes, bitmap);

    } catch (IOException e) {
      Log.e("MainActivity", "Error reading assets", e);
      finish();
    }

//    Log.d("MainActivity", String.format("Boxes size: %d", boxes.size()));
//    textView.setText(className);
  }

  public void drawRectangles(ArrayList<Box> boxes, Bitmap bitmap){
    Canvas canvas = new Canvas(bitmap);
    for (Box box : boxes) {
      canvas.drawRect(box.getTopLeftX(), box.getTopLeftY(), box.getBotRightX(), box.getBotRightY(), paint);
      Log.d("MainActivit", String.format("Class: %s, Probability: %f", Config.classes[box.getMaxIndex()], box.getMaxProbability()));
    }
    imageView.setImageBitmap(bitmap);
  }

  public Bitmap scaleBitmapImage(Bitmap bitmap){
    return Bitmap.createScaledBitmap(bitmap, Config.size, Config.size, true);
  }

  public ArrayList<Box> predictImageClasses(Bitmap bitmap){
    int originalImageWidth = bitmap.getWidth();
    int originalImageHeight = bitmap.getHeight();
    final float probabilityThreshold = 0.0f, iouThreshold = 0.0f;

    Bitmap scaledBitmap = scaleBitmapImage(bitmap);
//    imageView.setImageBitmap(scaledBitmap);
//    Log.d("MainActivity", scaledBitmap.);

//    final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(scaledBitmap, Config.mean, Config.std);
    final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(scaledBitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
    Log.d("MainActivity", String.format("Input tensor: %s", Arrays.toString(inputTensor.getDataAsFloatArray())));

//    Log.d("MainActivity", String.format("inputTensor.shape(): %s", Arrays.toString(inputTensor.shape())));

    final IValue[] outputTensor = module.forward(IValue.from(inputTensor)).toTuple();

    float[] confidencesTensor = outputTensor[0].toTensor().getDataAsFloatArray();
    long[] confidencesShape = outputTensor[0].toTensor().shape();
    float[] locationsTensor = outputTensor[1].toTensor().getDataAsFloatArray();
    long[] locationsShape = outputTensor[1].toTensor().shape();

//    Log.d("MainActivity", String.format("Confidences shape: %s", Arrays.toString(confidencesShape)));
//    Log.d("MainActivity", String.format("Confidences: %s", Arrays.toString(confidencesTensor)));
//    Log.d("MainActivity", String.format("Locations shape: %s", Arrays.toString(locationsShape)));
//    Log.d("MainActivity", String.format("Locations: %s", Arrays.toString(locationsTensor)));
//    Log.d("MainActivity", String.format("Priors size: %d", Config.priors.size()));

    ArrayList<Box> locations = new ArrayList<>();

    for (int i = 0; i < confidencesShape[1]; i++) {
      int confidencesFrom = (int) (i*confidencesShape[2]);
      int confidencesTo = (int) (confidencesFrom + confidencesShape[2]);

      float[] confidence = Arrays.copyOfRange(confidencesTensor, confidencesFrom, confidencesTo);
      float[] softmaxScores = TensorUtils.softmax(confidence);


      int locationsFrom = (int) (i*locationsShape[2]);
      Box location = Box.fromCentered(
              locationsTensor[locationsFrom], locationsTensor[locationsFrom+1],
              locationsTensor[locationsFrom+2], locationsTensor[locationsFrom+3], softmaxScores);
      locations.add(location);

      if (location.getMaxIndex() != 0){
        Log.d("MainActivity", String.format("Index: %d; Softmax scores: %s", i, Arrays.toString(softmaxScores)));
      }
    }


    ArrayList<Box> boxes = TensorUtils.convertLocations2Boxes(locations, Config.priors, Config.centerVariance, Config.sizeVariance);
//    Log.d("MainActivity", String.format("Locations2Boxes: %s", boxes.get(0).));

    ArrayList<Box> pickedBoxes = new ArrayList<>();
    for (int classIndex = 1; classIndex < confidencesShape[2]; classIndex++) {
      LinkedList<Box> boxesWithClassIndex = new LinkedList<>();
      for (Box box : boxes) {
        if (box.getMaxIndex() == classIndex && box.getMaxProbability() >= probabilityThreshold){
          boxesWithClassIndex.add(box);
        }
      }

      if (boxesWithClassIndex.isEmpty()) continue;

      ArrayList<Box> notSuppressedBoxes = TensorUtils.nonMaximumSuppression(boxesWithClassIndex, iouThreshold);
      pickedBoxes.addAll(notSuppressedBoxes);
    }

    for (Box box : pickedBoxes) {
      box.scaleBox(originalImageWidth, originalImageHeight);
    }

    return pickedBoxes;
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
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream, null, bitmapOptions);
        ArrayList<Box> boxes = predictImageClasses(selectedImage);
        drawRectangles(boxes, selectedImage);

//        textView.setText(className);
//        imageView.setImageBitmap(selectedImage);
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
