package com.example.databasetictoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.databasetictoe.Adapter.ViewPagerAdapter;
import com.example.databasetictoe.Fragments.ChatFragment;
import com.example.databasetictoe.Fragments.GameFragment;
import com.example.databasetictoe.Model.GameUsers;
import com.example.databasetictoe.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    TextView username;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

    Button back;

    // ------------- Functions for Fragments --------------------------------

    String turn;

    public String turn() {
        return turn;
    }

    String cod;

    public String cod() {
        return cod;
    }

    String cod2;

    public String cod2() {
        return cod2;
    }

    String myname;

    public String myname() {
        return myname;
    }

    //-------------------------------------------------------------


    @Override
    protected void onStart() {
        super.onStart();

        //Preiau datele ce au fost trimise din MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            turn = extras.getString("Turn");
            cod = extras.getString("cod");
        } else {
            cod = "";
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            cod2 = sharedPreferences.getString("cod", "");
            turn = sharedPreferences.getString("name", "");
            myname = sharedPreferences.getString("myname", "");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GameActivity", "onCreate-inceput");
        setContentView(R.layout.activity_game);
        //-------------- TabLayout Settings ---------------------------

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(new GameFragment(), "GAME");
        adapter.addFragments(new ChatFragment(), "CHAT");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //-------------------------------------------------------------

        //-------------- Toolbar settings ----------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        username = findViewById(R.id.username);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //-------------------------------------------------------------

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(GameActivity.this, StartActivity.class));
                finish();
                return true;
        }
        return false;
    }


    @Override
    protected void onStop() {
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("opponent").setValue("");
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("GameCod").setValue("");
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Ready").setValue("False");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseDatabase.getInstance().getReference("Games").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if(snapshot.child(cod).child("Player 2").exists() && !snapshot.child(cod).child("Opponent_left").exists())
                        {
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Opponent_left").setValue("da");
                        }
                        else
                        {
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).removeValue();
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            FirebaseDatabase.getInstance().getReference("Games").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(cod2).child("Player 2").exists() && !snapshot.child(cod2).child("Opponent_left").exists())
                    {
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Opponent_left").setValue("da");
                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        super.onStop();
    }
}