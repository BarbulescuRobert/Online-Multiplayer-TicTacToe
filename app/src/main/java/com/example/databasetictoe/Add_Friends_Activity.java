package com.example.databasetictoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.databasetictoe.Adapter.FriendRequestViewAdapter;
import com.example.databasetictoe.Adapter.UserRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.Arrays;

public class Add_Friends_Activity extends AppCompatActivity {

    EditText friend_code;
    Button friend_button;

    String code;
    String myname = "";

    FirebaseUser me;

    RecyclerView recyclerView;
    FriendRequestViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friends);

        friend_code = findViewById(R.id.friend_code);
        friend_button = findViewById(R.id.friend_button);

        me = FirebaseAuth.getInstance().getCurrentUser();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        FirebaseDatabase.getInstance().getReference("Users").child(me.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myname = snapshot.child("username").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!friend_code.getText().equals("")) {
                    code = friend_code.getText().toString();
                    friend_code.setText("");
                    FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                if (s.child("id_for_friends").exists())
                                    if (s.child("id_for_friends").getValue().toString().equals(code)) {
                                        FirebaseDatabase.getInstance().getReference("Users").child(s.child("id").getValue().toString()).child("Friends_Request").child(me.getUid()).setValue(myname);
                                    }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        //-------------- Friend list RecyclerView settings ----------------------
        recyclerView = findViewById(R.id.friend_request);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendRequestViewAdapter();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(me.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Friends_Request").exists()) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(me.getUid()).child("Friends_Request").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            String nume = snapshot.getValue().toString();
                            adapter.addNames(nume);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            String nume = snapshot.getValue().toString();
                            adapter.removeNames(nume);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(adapter);

    }
}