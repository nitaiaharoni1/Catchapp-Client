package com.example.nitai.client_nitai;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {
    public static SpeechRecognizer mSpeechRecognizer;
    public static Intent mSpeechRecognizerIntent;
    public static Map<String, Object> wikiMap;
    public static BlockingQueue<String> textRecognizedQueue;
    public static BlockingQueue<String> phrasesQueue;
    public static RequestQueue queue;

    public Boolean clicked = false;
    public String userLanguage = "EN";
    public Button btnSpeak;
    public TextView recognizedText;
    public TextView termTitle;
    public TextView termSummery;
    public Context context;


    public String getUserLanguage() {
        return userLanguage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        languageSpinner();
        queue = Volley.newRequestQueue(this);
        wikiMap = new HashMap<>();
        textRecognizedQueue = new LinkedBlockingQueue<>();
        phrasesQueue = new LinkedBlockingQueue<>();
        btnSpeak = findViewById(R.id.btnSpeak);
        recognizedText = findViewById(R.id.recognitionText);
        termTitle = findViewById(R.id.title);
        termSummery = findViewById(R.id.summery);
        btnSpeak.setOnClickListener(recognitionButtonListener);
    }

    View.OnClickListener recognitionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRecognitionButtonClicked();
        }
    };

    private void onRecognitionButtonClicked() {
        if (!clicked) {
            clicked = true;
            setSpeechRecognizer();
            Thread phrasesThread = new Thread(new PhrasesThread());
            Thread wikiThread = new Thread(new WikiThread());
            phrasesThread.start();
            wikiThread.start();
            //phrasesThread.join();
            //wikiThread.join();
            TextView recognizedText = findViewById(R.id.recognitionText);
            recognizedText.setText("");
            btnSpeak.setText("Listening...");
        } else {
            mSpeechRecognizer.stopListening();
            btnSpeak.setText("Catchapp");
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

    private void setSpeechRecognizer(){
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new recoListener());
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

        private void languageSpinner() {
        Spinner spinner = findViewById(R.id.userLang);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item.toString() != getUserLanguage()) {
                    userLanguage = item.toString();
                    if (mSpeechRecognizer != null) {
                        mSpeechRecognizer.stopListening();
                    }
                    btnSpeak.setText("Catchapp");
                    clicked = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
