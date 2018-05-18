package com.example.nitai.client_nitai;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.nitai.client_nitai.Utils.BASE_URL;

public class WikiThread extends MainActivity implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                ArrayList<String> phrases = new ArrayList<>();
                phrases.add(phrasesQueue.take());
                MyPostRequest(phrases);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void MyPostRequest(final ArrayList<String> array) {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL + "wiki", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            addPhrases(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        queue.add(postRequest);

    }

    public static void addPhrases(JSONObject obj) throws JSONException, InterruptedException {
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            WikiObject wikiObject = new WikiObject(obj.getJSONObject(key));
            wikiMap.put(wikiObject.getTitle(), wikiObject);
            Pair wikiPair = new Pair(wikiObject.getTitle(), wikiObject);
            wikiMapQueue.put(wikiPair);
        }
    }
}
