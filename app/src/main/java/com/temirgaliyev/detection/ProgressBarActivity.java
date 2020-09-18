package com.temirgaliyev.detection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.temirgaliyev.detection.AsyncTasks.DetectionAsyncTask;
import com.temirgaliyev.detection.AsyncTasks.DownloadAsyncTask;

public class ProgressBarActivity extends AppCompatActivity {

    // download, detection
    public static final String EXTRA_ACTION_TYPE = "com.temirgaliyev.detection.action";
    public static final String EXTRA_ACTION_DOWNLOAD = "com.temirgaliyev.detection.action.download";
    public static final String EXTRA_ACTION_DETECTION_DETR = "com.temirgaliyev.detection.action.detr";
    public static final String EXTRA_ACTION_DETECTION_SSD = "com.temirgaliyev.detection.action.ssd";
    public static final String EXTRA_INPUT_FILENAME = "com.temirgaliyev.detection.input_filename";
    public static final String EXTRA_OUTPUT_FILENAME = "com.temirgaliyev.detection.output_filename";
    public static final String EXTRA_FILE_URL = "com.temirgaliyev.detection.fileurl";
    private static final String TAG = "DOWNLOAD_ACTIVITY";
    TextView statusTextView;
    ProgressBar progressBar;
    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        statusTextView = findViewById(R.id.statusTextView);
        statusTextView.setText("Starting...");
        progressBar = findViewById(R.id.progressBar);

        String actionType = getIntent().getStringExtra(EXTRA_ACTION_TYPE);
        if (actionType == null) {
            finish();
        } else if (actionType.equals(EXTRA_ACTION_DOWNLOAD)) {
            progressBar.setMax(100);
            progressBar.setIndeterminate(false);
            createAndExecuteDownloadAsyncTask();
        } else if (actionType.equals(EXTRA_ACTION_DETECTION_DETR)) {
            progressBar.setIndeterminate(true);
            createAndExecuteDetectionActivity();
        } else if (actionType.equals(EXTRA_ACTION_DETECTION_SSD)) {

        } else {
            finish();
        }
    }

    private void createAndExecuteDetectionActivity() {
        String inputFilename = getIntent().getStringExtra(EXTRA_OUTPUT_FILENAME);
        String outputFilename = getIntent().getStringExtra(EXTRA_OUTPUT_FILENAME);
        String detectionType = getIntent().getStringExtra(EXTRA_ACTION_TYPE);

        if (inputFilename == null || outputFilename == null || detectionType == null) {
            finish();
        }

        asyncTask = new DetectionAsyncTask(this, progressBar, statusTextView);
        asyncTask.execute(inputFilename, outputFilename, detectionType);
    }

    private void createAndExecuteDownloadAsyncTask() {
        String outputFilename = getIntent().getStringExtra(EXTRA_OUTPUT_FILENAME);
        String fileurl = getIntent().getStringExtra(EXTRA_FILE_URL);

        if (outputFilename == null || fileurl == null) {
            finish();
        }

        asyncTask = new DownloadAsyncTask(this, progressBar, statusTextView);
        asyncTask.execute(outputFilename, fileurl);
    }

    public void onCancelClick(View view) {
        view.setClickable(false);
        asyncTask.cancel(false);
    }
}
