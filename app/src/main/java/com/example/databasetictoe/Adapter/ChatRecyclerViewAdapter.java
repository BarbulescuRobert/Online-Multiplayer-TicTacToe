package com.example.databasetictoe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databasetictoe.Model.Chat;
import com.example.databasetictoe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    public static final int MSG_RIGHT = 1;
    public static final int MSG_LEFT = 0;

    List<Chat> chat;
    Context context;
    String myname;

    FirebaseUser me;

    public ChatRecyclerViewAdapter(List<Chat> chat,Context context, String myname)
    {
        this.chat = chat;
        this.context = context;
        this.myname = myname;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_sent, parent, false);
            return new ChatRecyclerViewAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_get, parent, false);
            return new ChatRecyclerViewAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chatt = chat.get(position);

        holder.textView.setText(chatt.getMsg());
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    @Override
    public int getItemViewType(int position) {
        me = FirebaseAuth.getInstance().getCurrentUser();
        if(chat.get(position).getSender().equals(myname))
            return MSG_RIGHT;
        else
            return MSG_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.showMessage);
        }
    }
}
