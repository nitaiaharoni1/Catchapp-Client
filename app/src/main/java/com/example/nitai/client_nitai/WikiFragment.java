package com.example.nitai.client_nitai;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class WikiFragment extends android.support.v4.app.Fragment{

    private WikiObject wikiObject;
    private TextView title;
    private TextView summery;
    private ImageView image;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wiki,container,false);
        Bundle bundle = getArguments();
        wikiObject = (WikiObject) bundle.getSerializable("wikiObject");
        title = view.findViewById(R.id.titleView);
        summery = view.findViewById(R.id.summery);
        image = view.findViewById(R.id.imageView);

//        try {
//            URL url = new URL(wikiObject.getUrl());
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            image.setImageBitmap(bmp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        title.setText(wikiObject.getTitle());
        summery.setText(wikiObject.getSummery());
    }
}
