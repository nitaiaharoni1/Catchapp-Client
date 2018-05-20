package com.example.nitai.client_nitai;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Date;


public class MessagesListFragmant extends android.support.v4.app.Fragment implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    private MessagesListAdapter<Message> adapter;
    private String flip = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messages_list, container, false);
        adapter = new MessagesListAdapter<>("0", null);
        MessagesList messagesList = view.findViewById(R.id.messagesList);
        messagesList.setAdapter(adapter);
        adapter.setOnMessageClickListener(onMessageClickListener);
        adapter.setOnMessageLongClickListener(onMessageLongClickListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flippingInt();
        popAllBubbles();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.d("check", "onLoadMore: ");
        Log.d("", "onResume: ");


    }

    MessagesListAdapter.OnMessageLongClickListener onMessageLongClickListener = new MessagesListAdapter.OnMessageLongClickListener() {

        @Override
        public void onMessageLongClick(IMessage message) {
            WikiObject wikiObject = MainActivity.wikiMap.get(message.getText());
            Bundle bundle = new Bundle();
            bundle.putSerializable("wikiObject", wikiObject);
            WikiFragment wikiFragment = new WikiFragment();
            wikiFragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmantViewHolder, wikiFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };


    MessagesListAdapter.OnMessageClickListener onMessageClickListener = new MessagesListAdapter.OnMessageClickListener() {
        @Override
        public void onMessageClick(IMessage message) {

        }
    };

    public void popAllBubbles() {
        Pair pair;
        int size = MainActivity.wikiMapQueue2.size();
        for (int i = 0; i < size; i++) {
            pair = MainActivity.wikiMapQueue2.poll();
            MainActivity.wikiMapQueue2.add(pair);
            popBubble(pair);
            Log.i("bubblePop", "new bubble: : " + pair.first.toString());
        }
    }

    public void popBubble(Pair pair) {
        Author author = new Author(flippingInt(), "", null);
        Message message = new Message("", pair.first.toString(), author, new Date());
        adapter.addToStart(message, true);
        Log.i("bubblePop", "new bubble: : " + pair.first.toString());
    }

    public String flippingInt() {
        if (flip.equals("0")) {
            flip = "1";
            return flip;
        } else {
            flip = "0";
            return flip;
        }
    }

    @Override
    public void onSelectionChanged(int count) {

    }

    public class Message implements IMessage {
        private String id;
        private String text;
        private Author author;
        private Date createdAt;


        Message(String id, String text, Author author, Date createdAt) {
            this.id = id;
            this.text = text;
            this.author = author;
            this.createdAt = createdAt;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public Author getUser() {
            return author;
        }

        @Override
        public Date getCreatedAt() {
            return createdAt;
        }
    }

    public class Author implements IUser {
        private String id;
        private String name;
        private String avatar;

        Author(String id, String name, String avatar) {
            this.id = id;
            this.name = name;
            this.avatar = avatar;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getAvatar() {
            return avatar;
        }
    }

}
