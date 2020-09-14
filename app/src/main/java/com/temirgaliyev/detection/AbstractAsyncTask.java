package com.temirgaliyev.detection;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public abstract class AbstractAsyncTask extends AsyncTask<String, String, String> {
    private WeakReference<AppCompatActivity> activityReference;
    private WeakReference<ProgressBar> progressBarReference;
    private WeakReference<TextView> statusTextViewReference;

    public AbstractAsyncTask(AppCompatActivity activity, ProgressBar progressBar, TextView statusTextView){
        this.activityReference = new WeakReference<>(activity);
        this.progressBarReference = new WeakReference<>(progressBar);
        this.statusTextViewReference = new WeakReference<>(statusTextView);
    }

    @Override
    protected void onPostExecute(String file_url) {
        setTextUI("Finished");
        if (activityReference != null){
            activityReference.get().finish();
        }
    }


    protected void setProgressUI(final int progress){
        if (activityReference != null && progressBarReference != null){
            activityReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarReference.get().setProgress(progress);
                }
            });
        }
    }

    protected void setTextUI(final String text){
        if (activityReference != null && statusTextViewReference != null) {
            activityReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTextViewReference.get().setText(text);
                }
            });
        }
    }


}
