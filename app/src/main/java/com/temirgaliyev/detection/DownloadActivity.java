package com.temirgaliyev.detection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadActivity extends AppCompatActivity {

    private TextView statusTextView;
    private ProgressBar progressBar;

    private boolean cancel = false;
    private String filename, fileurl;

    private static final String TAG = "DOWNLOAD_ACTIVITY";
    public static final String EXTRA_FILENAME = "com.temirgaliyev.detection.filename";
    public static final String EXTRA_FILE_URL = "com.temirgaliyev.detection.fileurl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        filename = getIntent().getStringExtra(EXTRA_FILENAME);
        fileurl = getIntent().getStringExtra(EXTRA_FILE_URL);

        if (filename == null || fileurl == null) {
            finish();
        }

        statusTextView = findViewById(R.id.statusTextView);
        statusTextView.setText("Starting...");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        new CustonAsyncTast().execute(filename, fileurl);
    }

    public void onCancelClick(View view) {
        view.setClickable(false);
        cancel = true;
        statusTextView.setText("Cancelling...");
    }

    public class CustonAsyncTast extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String[] params) {
            String fileName = params[0];
            String fileURL = params[1];
            try {
                setTextUI("Initializing...");
                int lastDotPosition = fileName.lastIndexOf('/');
                if( lastDotPosition > 0 ) {
                    String folder = fileName.substring(0, lastDotPosition);
                    File fDir = new File(folder);
                    fDir.mkdirs();
                }

                URL u = new URL(fileURL);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(30000);
                c.connect();
                double fileSize  = (double) c.getContentLength();
                int counter = 0;
                while ( (fileSize == -1) && (counter <=30)){
                    c.disconnect();
                    u = new URL(fileURL);
                    c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.setReadTimeout(30000);
                    c.connect();
                    fileSize  = (double) c.getContentLength();
                    counter++;
                }

                File fOutput = new File(fileName);
                if (fOutput.exists()){
                    fOutput.delete();
                }

                setTextUI("Downloading...");

                BufferedOutputStream f = new BufferedOutputStream(new FileOutputStream(fOutput));
                InputStream in = c.getInputStream();
                byte[] buffer = new byte[8192];
                int len1 = 0;
                int downloadedData = 0;
                while ((len1 = in.read(buffer)) > 0 && !cancel) {
                    setProgressUI((int) (downloadedData/fileSize*100));
                    downloadedData += len1;
                    f.write(buffer, 0, len1);
                }

                if (cancel && fOutput.exists()){
                    fOutput.delete();
                }
                f.close();

                setTextUI("Finished");

            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            finish();
        }


        public void setProgressUI(final int progress){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                }
            });
        }

        public void setTextUI(final String text){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTextView.setText(text);
                }
            });
        }
    }
}
