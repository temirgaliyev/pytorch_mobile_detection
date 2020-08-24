package org.pytorch.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.pytorch.helloworld.SSD.SSDConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Utils {

    private static Paint rectPaint = new Paint();
    private static Paint textPaint = new Paint();

    public static void initUtils(){
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(Color.RED);
        textPaint.setColor(Color.DKGRAY);
//        float scale = getResources().getDisplayMetrics().density;
//        textPaint.setTextSize(20 * scale);
        textPaint.setTextSize(40);
    }

    public static void drawRectangles(ArrayList<Box> boxes, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        for (Box box : boxes) {
            canvas.drawRect(box.getTopLeftX(), box.getTopLeftY(), box.getBotRightX(), box.getBotRightY(), rectPaint);
            String text = String.format("Class: %s; Probability: %.2f", SSDConfig.classes[box.getMaxIndex()], box.getMaxProbability());
            canvas.drawText(text, box.getTopLeftX() + 20, box.getTopLeftY() + 40, textPaint);
        }
    }

    public static Bitmap scaleBitmapImage(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
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
}
