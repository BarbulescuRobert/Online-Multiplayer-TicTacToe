package com.example.databasetictoe.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databasetictoe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestViewAdapter extends RecyclerView.Adapter<FriendRequestViewAdapter.ViewHolder> {

    List<String> names = new ArrayList<>();
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
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendrequestlist, parent, false);
        return new ViewHolder(view);
    }
    String friendName;
    String myname;
    String friendID;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nume.setText(names.get(position));
        holder.accept_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myname = snapshot.child("username").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Friends_Request").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue().toString().equals(names.get(position))) {
                                friendName = names.get(position);
                                friendID = s.getKey().toString();
                                FirebaseDatabase.getInstance().getReference("Users").child(friendID).child("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).setValue(myname);
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Friends").child(friendID).setValue(friendName);
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Friends_Request").child(friendID).removeValue();
                                notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nume;
        Button accept_friend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nume = itemView.findViewById(R.id.friend_request_name);
            accept_friend = itemView.findViewById(R.id.friend_request_button);
        }
    }
}
