package com.example.nitai.client_nitai;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends AppCompatActivity {
    public static MessagesListFragmant messagesList;
    public static AudioManager audioManager;
    @SuppressLint("StaticFieldLeak")
    public static ButtonFragment buttonFragment;
    public static ArrayAdapter<CharSequence> adapter;
    public static SpeechRecognizer mSpeechRecognizer;
    public static Intent mSpeechRecognizerIntent;
    public static Map<String, WikiObject> wikiMap;
    public static BlockingQueue<Pair<String, WikiObject>> wikiMapQueue;
    public static Queue<Pair<String, WikiObject>> wikiMapQueue2;
    public static BlockingQueue<String> textRecognizedQueue;
    public static RequestQueue queue;
    public static ObjectPopAsyncTask asyncTask;
    public static Thread phrasesThread;
    public static String userLanguage = "EN";

    public static void setUserLanguage(String userLanguage) {
        MainActivity.userLanguage = userLanguage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        queue = Volley.newRequestQueue(this);
        wikiMap = new HashMap<>();
        wikiMapQueue = new LinkedBlockingQueue<>();
        wikiMapQueue2 = new LinkedList<>();
        textRecognizedQueue = new LinkedBlockingQueue<>();
        buttonFragment = new ButtonFragment();
        messagesList = new MessagesListFragmant();
        phrasesThread = new Thread(new PhrasesThread());
        asyncTask = new ObjectPopAsyncTask();
        adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmantViewHolder, buttonFragment, "buttonFragment");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().findFragmentByTag("buttonFragment").isVisible() == true) {
            phrasesThread.interrupt();
            asyncTask.cancel(true);
            mSpeechRecognizer.destroy();
        } else if (getSupportFragmentManager().findFragmentByTag("messageListFragment").isVisible() == true) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragmantViewHolder, buttonFragment);
//        transaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSpeechRecognizer.destroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
        }
        phrasesThread.interrupt();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    200);
        } else {
            //do something?
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void silence() {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
    }

//    public static void onNewPair(Pair pair) {
//        messagesList.popBubble(pair);
//    }

    public static void setSpeechRecognizer() {
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "he");
        mSpeechRecognizer.setRecognitionListener(new recoListener());
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    public static void startThreads() {
        phrasesThread = new Thread(new PhrasesThread());
        asyncTask = new ObjectPopAsyncTask();
        phrasesThread.start();
        asyncTask.execute(wikiMapQueue);
    }

}
