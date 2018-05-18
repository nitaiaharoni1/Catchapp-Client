package com.example.nitai.client_nitai;

import org.json.JSONException;
import org.json.JSONObject;

public class WikiObject {
    String title;
    String summery;
    String url;
    String image;

    public WikiObject(String title, String summery, String url, String image) {
        this.title = title;
        this.summery = summery;
        this.url = url;
        this.image = image;
    }

    public WikiObject(JSONObject jsonObject) throws JSONException {
        this.title = jsonObject.getString("title");
        this.summery = jsonObject.getString("summery");
        this.url = jsonObject.getString("url");
        this.image = jsonObject.getString("image");
    }

    public String getTitle() {
        return title;
    }

    public String getSummery() {
        return summery;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }
}
