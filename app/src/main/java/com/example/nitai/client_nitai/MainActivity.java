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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static SpeechRecognizer mSpeechRecognizer;
    public static Intent mSpeechRecognizerIntent;
    public Boolean clicked = false;
    public static Map<String, Object> phrases;
    public String userLanguage = "EN";
    public Button btnSpeak;
    public TextView recognizedText;
    public TextView termTitle;
    public TextView termSummery;
    public Context context;

    public static Intent getmSpeechRecognizerIntent() {
        return mSpeechRecognizerIntent;
    }

    public static SpeechRecognizer getmSpeechRecognizer() {
        return mSpeechRecognizer;
    }

    public static void addPhrases(JSONObject obj) throws JSONException {
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            phrases.put(key, obj.getJSONObject(key));
        }
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        checkPermission();
        languageSpinner();
        phrases = new HashMap<>();
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
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        if (!clicked) {
            clicked = true;
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            mSpeechRecognizer.setRecognitionListener(new recoListener(this, userLanguage));
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            TextView recognizedText = findViewById(R.id.recognitionText);
            recognizedText.setText("");
            btnSpeak.setText("Listening...");
            Log.i("i", "onRecognitionButtonClicked: ");
        } else {
            mSpeechRecognizer.stopListening();
            btnSpeak.setText("Catchapp");
            clicked = false;
        }
    }

    public Context recoListener() {
        return this.context;
    }

    private Context checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    200);
        } else {
        }
        return null;
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
                    setUserLanguage(item.toString());
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
