package com.example.nitai.client_nitai;

import android.Manifest;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends AppCompatActivity {
    static MessagesListFragmant messagesList;
    ButtonFragment buttonFragment;
    public ImageButton btnSpeak;

    public static SpeechRecognizer mSpeechRecognizer;
    public static Intent mSpeechRecognizerIntent;
    public static Map<String, WikiObject> wikiMap;
    public static BlockingQueue<Pair<String, WikiObject>> wikiMapQueue;

    public static BlockingQueue<String> textRecognizedQueue;
    public static BlockingQueue<String> phrasesQueue;
    public static RequestQueue queue;

    Thread phrasesThread;

    public String userLanguage = "EN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        languageSpinner();
        queue = Volley.newRequestQueue(this);
        wikiMap = new HashMap<>();
        wikiMapQueue = new LinkedBlockingQueue<>();
        textRecognizedQueue = new LinkedBlockingQueue<>();
        phrasesQueue = new LinkedBlockingQueue<>();
        buttonFragment = new ButtonFragment();
        messagesList = new MessagesListFragmant();
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(recognitionButtonListener);
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
        mSpeechRecognizer.stopListening();
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

    View.OnClickListener recognitionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRecognitionButtonClicked();
        }
    };

    public void onRecognitionButtonClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmantViewHolder, messagesList);
        transaction.addToBackStack(null);
        transaction.commit();
        setSpeechRecognizer();
        phrasesThread = new Thread(new PhrasesThread());
        phrasesThread.start();
        ObjectPopAsyncTask asyncTask = new ObjectPopAsyncTask(this);
        asyncTask.execute(wikiMapQueue);
        //silence();
        //phrasesThread.join();
        //wikiThread.join();
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
    public void silence() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
    }

    public void onNewPair(Pair pair) {
        messagesList.popBubble(pair, wikiMap);
    }

    private void setSpeechRecognizer() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new recoListener());
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    private void languageSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.userLang);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (!item.toString().equals(userLanguage)) {
                    userLanguage = item.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


}
