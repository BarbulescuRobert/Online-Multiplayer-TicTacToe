package com.example.databasetictoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.databasetictoe.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    List<String> names = new ArrayList<>();
    int gasit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    for(DataSnapshot s : snapshot.getChildren())
                        if(s.child("username").exists())
                            names.add(s.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                gasit = 0;

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password))
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                else if(txt_password.length() < 6)
                    Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                else if(txt_username.length() > 10)
                    Toast.makeText(RegisterActivity.this, "Username must have maximum 10 characters", Toast.LENGTH_SHORT).show();
                else if(names.contains(txt_username))
                    Toast.makeText(RegisterActivity.this, "Username already used", Toast.LENGTH_SHORT).show();
                else {
                        register(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    String numar;
    private void register (String usernamee, String emaill, String passwordd)
    {
        auth.createUserWithEmailAndPassword(emaill,passwordd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", usernamee);
                            hashMap.put("money", "0");

                            FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.child("nr_conturi").exists()) {
                                        numar = snapshot.child("nr_conturi").getValue().toString();
                                        int nr = Integer.valueOf(numar);
                                        nr = nr + 1;
                                        numar = Integer.toString(nr);
                                        reference.child("id_for_friends").setValue(numar);
                                        FirebaseDatabase.getInstance().getReference("Users").child("nr_conturi").setValue(numar);
                                    }
                                    else {
                                        reference.child("id_for_friends").setValue("1");
                                        FirebaseDatabase.getInstance().getReference("Users").child("nr_conturi").setValue("1");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            reference.setValue(hashMap);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "You can't register with this email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}