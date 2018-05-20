package com.example.nitai.client_nitai;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.nitai.client_nitai.Utils.BASE_URL;

@SuppressLint("Registered")
public class PhrasesThread extends MainActivity implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                String text = textRecognizedQueue.take();
                MyGetRequest(text);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void MyGetRequest(final String text) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL + "phrases", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            addPhrases(response);
                        } catch (JSONException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("text", text);
                headers.put("lang", userLanguage);
                return headers;
            }
        };
        queue.add(getRequest);
    }

    public static void addPhrases(JSONObject obj) throws JSONException, InterruptedException {
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            WikiObject wikiObject = new WikiObject(obj.getJSONObject(key));
            wikiMap.put(wikiObject.getTitle(), wikiObject);
            Pair<String, WikiObject> wikiPair = new Pair<>(wikiObject.getTitle(), wikiObject);
            wikiMapQueue.put(wikiPair);
        }
    }
}
