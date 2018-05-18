package com.example.nitai.client_nitai;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class BubblesFragment extends android.support.v4.app.Fragment {

    View view;
    ChatView chatView;
    ListView listView;

    private ChatMessage.Type type = ChatMessage.Type.RECEIVED;
    private Map<String, WikiObject> wikiMap;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bubbles, container, false);
        chatView = view.findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {

                return false;
            }
        });
        listView = chatView.findViewById(R.id.chat_list);
        listView.setOnItemClickListener(onBubbleClickListener);
        return view;
    }

    public ChatMessage.Type randomChatMessageType() {
        if (type == ChatMessage.Type.RECEIVED){
            type = ChatMessage.Type.SENT;
            return type;
        } else{
            type = ChatMessage.Type.RECEIVED;
            return type;
        }
    }

    private void onBubbleClicked() {
        TextView title = view.findViewById(co.intentservice.chatui.R.id.message_text_view);
        TextView timeStamp = view.findViewById(co.intentservice.chatui.R.id.timestamp_text_view);
        WikiObject wikiObject = wikiMap.get(title.getText().toString());
        String summery = wikiObject.getSummery();
        Log.i("BubbleFragment", title.getText().toString());
        timeStamp.setText(summery);
    }

    public void popBubble(Pair pair, Map<String, WikiObject> wikiMap) {
        ChatMessage bubble = new ChatMessage(pair.first.toString(), System.currentTimeMillis(), randomChatMessageType());
        chatView.addMessage(bubble);
        listView = chatView.findViewById(R.id.chat_list);
        this.wikiMap = wikiMap;

    }

    ListView.OnItemClickListener onBubbleClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onBubbleClicked();
        }
    };


}

