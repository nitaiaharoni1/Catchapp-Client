package com.example.nitai.client_nitai;

import android.os.AsyncTask;

import java.net.URL;

public class ObjectPopAsyncTask extends AsyncTask {

    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        showDialog("Downloaded " + result + " bytes");
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
