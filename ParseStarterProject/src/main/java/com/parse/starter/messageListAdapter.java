package com.parse.starter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.parse.ParseObject;

import java.util.ArrayList;

public class messageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ArrayList<DataSnapshot> mMessageList;
    private String sender;
    private Boolean isGroup;

    public messageListAdapter(ArrayList<DataSnapshot> messageList, String sender, Boolean isGroup) {
        this.mMessageList = messageList;
        this.sender = sender;
        this.isGroup = isGroup;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        String user = ((mMessageList.get(position)).getValue(ChatData.class)).getName();
        //Log.i("info", position + " u "+user+" s "+sender + mMessageList.size());
        if (user.equals(sender)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            if(isGroup){
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grp_recevier, parent, false);
                return new GrpReceivedMessageHolder(view);
            }else{
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.receiver, parent, false);
                return new ReceivedMessageHolder(view);
            }

        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DataSnapshot message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                if(isGroup){
                    ((GrpReceivedMessageHolder) holder).bind(message);
                }else{
                    ((ReceivedMessageHolder) holder).bind(message);
                }

        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.sender_message_body);
        }

        void bind(DataSnapshot message) {
            messageText.setText(message.getValue(ChatData.class).getMessage());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.receiver_message_body);
        }

        void bind(DataSnapshot message) {
            messageText.setText(message.getValue(ChatData.class).getMessage());
        }
    }

    private class GrpReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView nameReciver;

        GrpReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.grpReceiver_message_body);
            nameReciver = (TextView) itemView.findViewById(R.id.grpReciverName);
        }

        void bind(DataSnapshot message) {
            messageText.setText(message.getValue(ChatData.class).getMessage());
            nameReciver.setText(message.getValue(ChatData.class).getName());
        }
    }
}