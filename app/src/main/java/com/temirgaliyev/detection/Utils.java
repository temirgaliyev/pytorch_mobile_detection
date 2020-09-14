package com.temirgaliyev.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Log;

import com.temirgaliyev.detection.Detection.AbstractDetection;
import com.temirgaliyev.detection.Detection.Box;
import com.temirgaliyev.detection.Detection.DETR.DETR;
import com.temirgaliyev.detection.Detection.DetectionModelEnum;
import com.temirgaliyev.detection.Detection.SSD.SSD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static String TAG = "UTILS";
    private static Paint rectPaint = new Paint();
    private static Paint textPaint = new Paint();

    public static void initUtils(){
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(Color.RED);
        textPaint.setColor(Color.DKGRAY);
//        float scale = getResources().getDisplayMetrics().density;
//        textPaint.setTextSize(20 * scale);
//        textPaint.setTextSize(40);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void drawRectangles(ArrayList<Box> boxes, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        for (Box box : boxes) {
            canvas.drawRect(box.getTopLeftX(), box.getTopLeftY(), box.getBotRightX(), box.getBotRightY(), rectPaint);
            String text = String.format("%s: %.2f", box.getCls(), box.getMaxProbability());
            textPaint.setTextSize(Math.max(bitmap.getWidth()/25, 50));
            Log.d(TAG, "Text size: " + bitmap.getWidth()/25);
            canvas.drawText(text, box.getTopLeftX() + 20, box.getTopLeftY() + 40, textPaint);
        }
    }

    public static Bitmap resize(Bitmap bitmap, int width, int height){
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static Bitmap resize(Bitmap bitmap, int size){
        int width;
        int height;

        if (bitmap.getWidth() < bitmap.getHeight()){
            width = size;
            height = bitmap.getHeight()*width/bitmap.getWidth();
        } else{
            height = size;
            width = bitmap.getWidth()*height/bitmap.getHeight();
        }

        return resize(bitmap, width, height);
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

    public static String millisToShortDHMS(long duration) {
        long seconds    = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        long millis     = TimeUnit.MILLISECONDS.toMillis(duration) -
                TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration));

        return String.format("%02d.%04d seconds", seconds, millis);
    }

    private static AbstractDetection detr, ssd;

    public static AbstractDetection getDetector(Context context,
                                                DetectionModelEnum detectionModelEnum){
        AbstractDetection detector;
        if (detectionModelEnum == DetectionModelEnum.DETR){
            if (detr == null) {
                detr = new DETR(context.getExternalCacheDir());
                detr.loadModule();
            }
            detector = detr;
        } else {
            if (ssd == null) {
                ssd = new SSD(context.getExternalCacheDir());
                ssd.loadModule();
            }
            detector = ssd;
        }

        return detector;
    }


    public static int getImageOrientation(String filename){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
    }

}
