package com.example.nitai.client_nitai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public TextView recognizedText;
    public TextView termTitle;
    public TextView termSummery;
    public Button btnSpeak;
    public Boolean clicked = false;
    public ArrayList<String> matches;
    public ArrayList<String> partialMatches;
    public String userLang;
    public RequestQueue queue;
    public String url = "https://still-depths-64048.herokuapp.com/";
    public Map<String, String> phrases = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        this.recognizedText = findViewById(R.id.recognitionText);
        this.termTitle = findViewById(R.id.title);
        this.termSummery = findViewById(R.id.summery);
        this.btnSpeak = findViewById(R.id.btnSpeak);
        Spinner spinner = findViewById(R.id.userLang);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                userLang = item.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        queue = Volley.newRequestQueue(this);
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (!clicked) {
                    clicked = true;
                    mSpeechRecognizer.setRecognitionListener(new listener());
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    recognizedText.setText("");
                    btnSpeak.setText("Listening...");
                } else {
                    mSpeechRecognizer.stopListening();
                    btnSpeak.setText("Catchapp");
                    clicked = false;
                }
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    200);
        } else {
        }
    }

    private String[] toArray(JSONArray JSONArray) throws JSONException {
        String[] array = new String[JSONArray.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = JSONArray.getString(i);
        }
        return array;
    }


    class listener implements RecognitionListener {
        @Override
        public void onResults(Bundle bundle) {
            matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null) {
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "phrases", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    final Map<String, String> toServer = new HashMap();
                                    String[] resArray = toArray(response.getJSONArray("phrases"));
                                    for (int i = 0; i < resArray.length; i++) {
                                        if (!phrases.containsKey(resArray[i])) {
                                            phrases.put(resArray[i], "");
                                        }
                                    }
                                    termTitle.setText(phrases.keySet().toString());
                                    for (Map.Entry<String, String> entry : phrases.entrySet()) {
                                        if (entry.getValue() == "") {
                                            toServer.put(entry.getKey(), "");
                                        }
                                    }
                                    if (toServer.size() > 0) {
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, url + "wiki", new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObj = new JSONObject(response);
                                                    Toast.makeText(getApplicationContext(), jsonObj.toString(), Toast.LENGTH_LONG).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_LONG).show();
                                            }
                                        }) {
                                            @Override
                                            public Map<String, String> getHeaders() {
                                                Map<String, String> headers = new HashMap<>();
                                                headers.put("lang", userLang);
                                                return headers;
                                            }

                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<>();
                                                for (Map.Entry<String, String> entry : toServer.entrySet()) {
                                                    params.put(entry.getKey(), "");
                                                }
                                                return params;
                                            }
                                        };
                                        queue.add(postRequest);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_LONG).show();
                                Log.d("Error.Response", String.valueOf(error));
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("text", matches.get(0));
                        return headers;
                    }
                };
                recognizedText.setText(matches.get(0));
                queue.add(getRequest);
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            partialMatches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.i("onPartialResults", partialMatches.toString());
            //Toast.makeText(getApplicationContext(), partialMatches.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReadyForSpeech(Bundle bundle) {
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
        }

        @Override
        public void onError(int i) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    }

}
