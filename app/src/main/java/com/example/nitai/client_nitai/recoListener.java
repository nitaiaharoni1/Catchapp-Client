package com.example.nitai.client_nitai;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class recoListener extends MainActivity implements RecognitionListener {

    private int flag = 0;


    @Override
    public void onResults(Bundle bundle) {
        final ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        try {
            textRecognizedQueue.put(matches.get(0));
            Log.i("recoListener", "matches: " + matches.get(0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPartialResults(Bundle bundle) {
//        final ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//        try {
//            if (matches != null && matches.get(0).length() > 100 && flag == 0) {
//                flag = 1;
//                Log.i("matches", matches.get(0));
//                textRecognizedQueue.put(matches.get(0));
//                mSpeechRecognizer.stopListening();
//                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
        if( i == 6 || i == 7){
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
        else if (i!=8){
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }
}
