package com.example.nitai.client_nitai;


import android.util.Log;
import android.util.Pair;

public class ObjectsThread extends MainActivity implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                Pair pair = wikiMapQueue.take();
                Log.e("------------", pair.toString());

            } catch (InterruptedException e) {
                return;
            }
        }
    }


}
