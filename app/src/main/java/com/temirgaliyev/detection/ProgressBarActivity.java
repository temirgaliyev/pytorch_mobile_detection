package com.temirgaliyev.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.temirgaliyev.detection.AsyncTasks.DownloadAsyncTask;

public class ProgressBarActivity extends AppCompatActivity {

    TextView statusTextView;
    ProgressBar progressBar;

    private static final String TAG = "DOWNLOAD_ACTIVITY";
    // download, detection
    public static final String EXTRA_ACTION_TYPE = "com.temirgaliyev.detection.action";

    public static final String EXTRA_FILENAME = "com.temirgaliyev.detection.filename";
    public static final String EXTRA_FILE_URL = "com.temirgaliyev.detection.fileurl";

    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        statusTextView.setText("Starting...");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        String actionType = getIntent().getStringExtra(EXTRA_ACTION_TYPE);
        if (actionType == null){
            finish();
        } else if (actionType.equals("download")){
            createAndExecuteAsyncTask();
        } else {
            finish();
        }
    }

    private void createAndExecuteAsyncTask() {
        String filename = getIntent().getStringExtra(EXTRA_FILENAME);
        String fileurl = getIntent().getStringExtra(EXTRA_FILE_URL);

        if (filename == null || fileurl == null) {
            finish();
        }

        asyncTask = new DownloadAsyncTask(this, progressBar, statusTextView);
        asyncTask.execute(filename, fileurl);
    }

    public void onCancelClick(View view) {
        view.setClickable(false);
        asyncTask.cancel(false);
    }
}
