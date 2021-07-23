package com.example.databasetictoe.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databasetictoe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    List<String> names = new ArrayList<>();
    List<String> idd = new ArrayList<>();
    public void addNames(String name)
    {
        if(!names.contains(name))
            names.add(name);
    }
    public void removeNames(String name)
    {

        if(names.contains(name))
            names.remove(name);
    }
    public void addID(String id)
    {
        if(!idd.contains(id))
            idd.add(id);
    }
    public void removeID(String id)
    {

        if(idd.contains(id))
            idd.remove(id);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nume.setText(names.get(position));

        holder.accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.invite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nume;
        Button accept_button, invite_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nume = itemView.findViewById(R.id.user_name);
            accept_button = itemView.findViewById(R.id.send_invite);
            invite_button = itemView.findViewById(R.id.accept_invite);
        }
    }
}
