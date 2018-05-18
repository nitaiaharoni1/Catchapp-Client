package com.example.nitai.client_nitai;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class BubblesFragment extends android.support.v4.app.Fragment {

    private ChatMessage.Type type = ChatMessage.Type.RECEIVED;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bubbles, container, false);
        final ChatView chatView = view.findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {

                chatView.addMessage(new ChatMessage(chatMessage.getMessage(), System.currentTimeMillis(), randomChatMessageType()));
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
}
