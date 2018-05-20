package com.example.nitai.client_nitai;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

@SuppressLint("Registered")
public class recoListener extends MainActivity implements RecognitionListener {


    @Override
    public void onResults(Bundle bundle) {
        final ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        try {
            if (matches != null) {
                textRecognizedQueue.put(matches.get(0));
                Log.i("recoListener", "matches: " + matches.get(0));

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPartialResults(Bundle bundle) {
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
    }

    @Override
    public void onBeginningOfSpeech() {
        //flag = 0;
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int i) {
        Log.i("recoListener", "error: " + i);
        mSpeechRecognizer.destroy();
        mSpeechRecognizer.setRecognitionListener(new recoListener());
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }
}
