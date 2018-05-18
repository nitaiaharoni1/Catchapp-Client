package com.example.nitai.client_nitai;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class recoListener extends MainActivity implements RecognitionListener {

    @Override
    public void onResults(Bundle bundle) {
        final ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null) {
            try {
                textRecognizedQueue.put(matches.get(0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        } else{
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        //AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
    }

    @Override
    public void onBeginningOfSpeech() {
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
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }
}
