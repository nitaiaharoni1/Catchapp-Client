package com.example.nitai.client_nitai;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public class ObjectPopAsyncTask extends AsyncTask<BlockingQueue<Pair<String, WikiObject>>, Pair<String, WikiObject>, Void> {

    MainActivity activity;

    public ObjectPopAsyncTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(BlockingQueue<Pair<String, WikiObject>>... blockingQueues) {
        while (!isCancelled()) {
            try {
                Pair pair = blockingQueues[0].take();
                this.publishProgress(pair);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    protected void onPostExecute(Void a) {
    }

    @Override
    protected void onProgressUpdate(Pair<String, WikiObject>... pairs) {
        activity.onNewPair(pairs[0]);
    }


}
