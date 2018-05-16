package com.example.nitai.client_nitai;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.nitai.client_nitai.MainActivity.addPhrases;
import static com.example.nitai.client_nitai.MainActivity.phrases;
import static com.example.nitai.client_nitai.Utils.*;

public class recoListener implements RecognitionListener {
    private Context context;
    private RequestQueue queue;
    public String userLanguage;

    public recoListener(Context context, String userLanguage) {
        this.context = context;
        this.userLanguage = userLanguage;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void onResults(Bundle bundle) {
        final ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        //final ArrayList<String> matches = new ArrayList<String>();
        //matches.add("hello today we're going to talk about Robotics and then we're going to talk about AI");
        if (matches != null) {
            //recognizedText.setText(matches.get(0));
            queue.add(MyGetRequest(matches));
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
    }

    @Override
    public void onError(int i) {
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }

    public JsonObjectRequest MyGetRequest(final ArrayList<String> matches) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL + "phrases", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray resArray = response.getJSONArray("phrases");
                            final ArrayList<String> toServer = new ArrayList<>();
                            for (int i = 0; i < resArray.length(); i++) {
                                if (!phrases.containsKey(resArray.getString(i))) {
                                    toServer.add(resArray.getString(i));
                                }
                            }
                            if (toServer.size() > 0) {
                                queue.add(MyPostRequest(toServer));
                            } else{
                                //if toServer == ???
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, String.valueOf(error), Toast.LENGTH_LONG).show();
                        Log.d("Error.Response", String.valueOf(error));
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("text", matches.get(0));
                return headers;
            }
        };
        return getRequest;
    }

        public JsonObjectRequest MyPostRequest(final ArrayList<String> array) {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL + "wiki", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            addPhrases(response);
                            MainActivity.getmSpeechRecognizer().startListening(MainActivity.getmSpeechRecognizerIntent());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("lang", userLanguage);
                StringBuilder terms = new StringBuilder();
                for (int i = 0; i < array.size(); i++) {
                    terms.append(array.get(i));
                    if (i != array.size() - 1) {
                        terms.append(",");
                    }
                }
                headers.put("terms", terms.toString());
                return headers;
            }
        };
        return postRequest;
    }

}
