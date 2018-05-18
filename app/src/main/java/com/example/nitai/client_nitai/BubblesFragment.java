package com.example.nitai.client_nitai;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class BubblesFragment extends android.support.v4.app.Fragment {

    View view;
    ChatView chatView;

    private ChatMessage.Type type = ChatMessage.Type.RECEIVED;

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

    public void popBubble(Pair pair) {
        chatView.addMessage(new ChatMessage(pair.first.toString(), System.currentTimeMillis(), randomChatMessageType()));
    }
}
