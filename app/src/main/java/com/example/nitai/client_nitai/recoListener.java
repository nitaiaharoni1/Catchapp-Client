package com.example.nitai.client_nitai;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class recoListener extends MainActivity implements RecognitionListener {

    private int flag = 0;


    @Override
    public void onResults(Bundle bundle) {
        final ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        try {
            if (matches != null) {
                textRecognizedQueue.put(matches.get(0));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPartialResults(Bundle bundle) {
        final ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        try {
            if (matches != null && matches.get(0).length() > 100 && flag == 0) {
                flag = 1;
                //mSpeechRecognizer.stopListening();
                mSpeechRecognizer.cancel();
                textRecognizedQueue.put(matches.get(0));
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {
        flag = 0;
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onError(int i) {
        mSpeechRecognizer.cancel();
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }
}
