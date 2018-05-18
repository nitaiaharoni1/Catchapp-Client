package com.example.nitai.client_nitai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    MessagesListFragmant messagesList;

    public static SpeechRecognizer mSpeechRecognizer;
    public static Intent mSpeechRecognizerIntent;
    public static Map<String, WikiObject> wikiMap;
    public static BlockingQueue<Pair<String, WikiObject>> wikiMapQueue;

    public static BlockingQueue<String> textRecognizedQueue;
    public static BlockingQueue<String> phrasesQueue;
    public static RequestQueue queue;

    public Boolean clicked = false;
    public String userLanguage = "EN";
    public ImageButton btnSpeak;

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
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(recognitionButtonListener);
    }

    View.OnClickListener recognitionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRecognitionButtonClicked();
        }
    };

    private void onRecognitionButtonClicked() {
        messagesList = new MessagesListFragmant();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmantViewHolder, messagesList);
        transaction.addToBackStack(null);
        transaction.commit();
        Thread phrasesThread = new Thread(new PhrasesThread());
        Thread wikiThread = new Thread(new WikiThread());
        if (!clicked) {
            clicked = true;
            setSpeechRecognizer();
            phrasesThread.start();
            wikiThread.start();
            ObjectPopAsyncTask asyncTask = new ObjectPopAsyncTask(this);
            asyncTask.execute(wikiMapQueue);


            //phrasesThread.join();
            //wikiThread.join();
        } else {
            mSpeechRecognizer.stopListening();
            clicked = false;
        }
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

    public void onNewPair(Pair pair) {
        Log.i("MainActivity", pair.toString());
        messagesList.popBubble(pair, wikiMap);
        //messagesList.popBubble("check");
    }

    private void setSpeechRecognizer() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new recoListener(0));
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
                    if (mSpeechRecognizer != null) {
                        mSpeechRecognizer.stopListening();
                    }
                    clicked = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


}
