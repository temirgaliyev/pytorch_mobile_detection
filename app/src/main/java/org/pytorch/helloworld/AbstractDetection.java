package org.pytorch.helloworld;

import android.graphics.Bitmap;

import java.util.ArrayList;

public abstract class AbstractDetection {

    public abstract ArrayList<Box> predict(Bitmap bitmap);
}
