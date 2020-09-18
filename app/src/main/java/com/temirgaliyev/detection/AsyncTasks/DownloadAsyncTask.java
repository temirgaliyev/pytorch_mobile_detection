package com.temirgaliyev.detection.AsyncTasks;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAsyncTask extends AbstractAsyncTask {
    private File fOutput;

    public DownloadAsyncTask(AppCompatActivity activity, ProgressBar progressBar, TextView statusTextView) {
        super(activity, progressBar, statusTextView);
    }


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

            fOutput = new File(fileName);
            if (fOutput.exists()){
                fOutput.delete();
            }

            setTextUI("Downloading...");

            BufferedOutputStream f = new BufferedOutputStream(new FileOutputStream(fOutput));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[8192];
            int len1;
            int downloadedData = 0;
            while ((len1 = in.read(buffer)) > 0 ) {
                setProgressUI((int) (downloadedData/fileSize*100));
                downloadedData += len1;
                f.write(buffer, 0, len1);
                if (isCancelled()){
                    setTextUI("Cancelling");
                }
            }

            f.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (fOutput.exists()){
            setTextUI("Deleting files...");
            fOutput.delete();
        }
    }

}