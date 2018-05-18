package com.example.nitai.client_nitai;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.nitai.client_nitai.Utils.BASE_URL;

public class PhrasesThread extends MainActivity implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                String text = textRecognizedQueue.take();
                MyGetRequest(text);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void MyGetRequest(final String text) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL + "phrases", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray phrasesArray = response.getJSONArray("phrases");
                            for (int i = 0; i < phrasesArray.length(); i++) {
                                phrasesQueue.put(phrasesArray.getString(i));
                            }
                        } catch (JSONException | InterruptedException e) {
                            return;
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
                return headers;
            }
        };
        queue.add(getRequest);
    }
}
