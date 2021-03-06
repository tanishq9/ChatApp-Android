package com.example.tanishqsaluja.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by tanishqsaluja on 18/2/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    ArrayList<Message> arrayList;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Context context;

    MessageAdapter(Context c, ArrayList<Message> list) {
        this.arrayList = list;
        this.context = c;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView imageView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.usermessage);
            imageView = itemView.findViewById(R.id.imagemessage);
        }
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder holder, int position) {
        Message user = arrayList.get(position);
        String current_user = user.getFrom();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("test", firebaseUser.getUid());
        Log.e("test", current_user);
        if (user.getType().equals("text")) {
            holder.imageView.setVisibility(View.INVISIBLE);
            if (current_user.equals(firebaseUser.getUid())) {
                holder.message.setBackgroundResource(R.drawable.message_text_background_other);
                holder.message.setTextColor(Color.BLACK);
            } else {
                holder.message.setBackgroundResource(R.drawable.message_text_background);
                holder.message.setTextColor(Color.WHITE);
            }
            holder.message.setText(user.getMessage());
        } else {

            holder.message.setVisibility(View.INVISIBLE);
            Picasso.with(context).load(user.getMessage()).placeholder(R.drawable.bot).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
