package com.example.databasetictoe.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databasetictoe.Adapter.ChatRecyclerViewAdapter;
import com.example.databasetictoe.Adapter.UserRecyclerViewAdapter;
import com.example.databasetictoe.GameActivity;
import com.example.databasetictoe.Model.Chat;
import com.example.databasetictoe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView chatview;
    ChatRecyclerViewAdapter adapter;
    List<Chat> chat;

    EditText typetext;

    Button sendbutton;

    String codd,opponent,myname;

    ValueEventListener a;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);

        typetext = view.findViewById(R.id.TextMessage);
        sendbutton = view.findViewById(R.id.SendButton);
        chatview = view.findViewById(R.id.ChatView);

        GameActivity activity = (GameActivity) getActivity();
        String cod = activity.cod();
        String cod2 = activity.cod2();
        myname = activity.myname();
        opponent = "";

        if(!cod.equals(""))
        {
            codd = cod;
            myname = activity.turn();
            a = new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("Player 2").exists())
                    {
                        opponent = snapshot.child("Player 2").getValue().toString();
                        FirebaseDatabase.getInstance().getReference("Games").child(codd).removeEventListener(a);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            FirebaseDatabase.getInstance().getReference("Games").child(codd).addValueEventListener(a);
        }
        else
        {
            codd = cod2;
            opponent = activity.turn();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        chatview.setLayoutManager(layoutManager);

        FirebaseDatabase.getInstance().getReference("Games").child(codd).child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typetext.getText().equals("") && !opponent.equals(""))
                {
                    sendMessage(myname,opponent,typetext.getText().toString());
                    typetext.setText("");
                }
                else
                {
                    typetext.setText("");
                }
            }
        });

        return view;
    }

    private void sendMessage(String sender, String receiver, String msg)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Games").child(codd);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("msg",msg);

        reference.child("Chat").push().setValue(hashMap);
    }

    private void readMessages()
    {
        chat = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Games").child(codd).child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chat.clear();
                for(DataSnapshot s : snapshot.getChildren())
                {
                    Chat chatt = s.getValue(Chat.class);
                    if((chatt.getReceiver().equals(myname) && chatt.getSender().equals(opponent)) || (chatt.getReceiver().equals(opponent) && chatt.getSender().equals(myname)))
                        chat.add(chatt);
                    adapter = new ChatRecyclerViewAdapter(chat,getContext(),myname);
                    chatview.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
