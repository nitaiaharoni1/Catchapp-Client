package com.example.nitai.client_nitai;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.util.concurrent.BlockingQueue;

import static com.example.nitai.client_nitai.MainActivity.wikiMapQueue;

public class ObjectPopAsyncTask extends AsyncTask<BlockingQueue<Pair<String, WikiObject>>, Pair<String, WikiObject>, Void> {

    @SuppressLint("StaticFieldLeak")
    private MainActivity activity;

    ObjectPopAsyncTask(MainActivity activity) {
        this.activity = activity;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(BlockingQueue<Pair<String, WikiObject>>... blockingQueues) {
        while (!isCancelled()) {
            try {
                Pair pair = wikiMapQueue.take();
                Log.i("bubblePop", "object taken: : " + pair.first.toString());

                this.publishProgress(pair);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    protected void onPostExecute(Void a) {
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Pair<String, WikiObject>... pairs) {
        activity.onNewPair(pairs[0]);
    }


}
