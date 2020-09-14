package com.temirgaliyev.detection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.temirgaliyev.detection.Detection.DETR.DETR;

import java.io.File;
import java.io.StringReader;

import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_ACTION_TYPE;
import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_FILENAME;
import static com.temirgaliyev.detection.ProgressBarActivity.EXTRA_FILE_URL;

public class StartingActivity extends AppCompatActivity {

    private static final String TAG = "STARTING_ACTIVITY";
    private static final int REQUEST_CODE_DETR = 100;
    private static final int REQUEST_CODE_SSD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
    }

    public void onRTButtonClick(View view) {
    }

    public void onNonRTButtonClick(View view) {
        String filePath = getExternalCacheDir() + "/" + DETR.WEIGHTS_PATH_NAME;
        String fileURL = DETR.WEIGHTS_URL;

        File file = new File(filePath);
        if (!file.exists()){
            Intent intent = new Intent(this, ProgressBarActivity.class);
            intent.putExtra(EXTRA_ACTION_TYPE, "download");
            intent.putExtra(EXTRA_FILE_URL, fileURL);
            intent.putExtra(EXTRA_FILENAME, filePath);
            startActivityForResult(intent, REQUEST_CODE_DETR);
        } else{
            Intent intent = new Intent(StartingActivity.this, ImageCapturingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DETR) {
            Intent intent = new Intent(StartingActivity.this, ImageCapturingActivity.class);
            startActivity(intent);
        } else if (requestCode == REQUEST_CODE_SSD){

        }

    }
}
