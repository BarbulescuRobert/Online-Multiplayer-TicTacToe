package com.example.databasetictoe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databasetictoe.Adapter.UserRecyclerViewAdapter;
import com.example.databasetictoe.Model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Objects;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    RecyclerView recyclerView;
    UserRecyclerViewAdapter adapter;

    Button findopponent;

    String myname, mycode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------- Toolbar settings ----------------------
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerlayout);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.tictactoe);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.navigation_myname);
        TextView navMoney = headerView.findViewById(R.id.coins);
        TextView navID = headerView.findViewById(R.id.id_for_friends);

        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                myname = user.getUsername();
                navUsername.setText(myname);
                if(snapshot.child("id_for_friends").exists())
                    navID.setText("id:  " + snapshot.child("id_for_friends").getValue().toString());
                if(snapshot.child("money").exists())
                    navMoney.setText(snapshot.child("money").getValue().toString());
                if(snapshot.child("GameCod").exists())
                    mycode = snapshot.child("GameCod").getValue().toString();
                if(snapshot.child("Friends").exists())
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //-------------------------------------------------------------

        //-------------- Friend list RecyclerView settings ----------------------
        recyclerView = findViewById(R.id.usersrecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserRecyclerViewAdapter();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Friends").exists()) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("Friends").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            String nume = snapshot.getValue().toString();
                            adapter.addNames(nume);
                            adapter.addID(snapshot.getKey().toString());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            String nume = snapshot.getValue().toString();
                            adapter.removeNames(nume);
                            adapter.removeID(snapshot.getKey().toString());
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
        //-------------------------------------------------------------

        //-------------- Find Opponent Button Settings ----------------------
        findopponent = findViewById(R.id.findopponentbutton);
        findopponent.setEnabled(false);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                findopponent.setEnabled(true);
            }

        }, 4000); //Delay de 4 secunde
        FindOpponent();
        //---------------------------------------------------------


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
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;
        }
        return false;
    }

    ValueEventListener as;
    String opponent_id, opponent_cod, finalCod, opponent_name;
    boolean Gasit;
    FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();

    private void FindOpponent() {
        findopponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                //FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference().child("Users").child(me.getUid()).child("Ready").setValue("True");
                findopponent.setEnabled(false);

                //Creez un numar random de 6 cifre care nu mai exista in baza de date
                final int min = 100000;
                final int max = 999999;
                String cod;
                do {
                    int random = new Random().nextInt((max - min) + 1) + min;
                    cod = String.valueOf(random);
                } while (FirebaseDatabase.getInstance().getReference("Games").child(cod).equals(null));
                finalCod = cod;

                //Creez un event listener pentru a vedea daca este creata deja o masa goala in care player-ul asteapta
                as = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            if(s.child("id").exists() && s.child("Ready").exists() && s.child("GameCod").exists())
                            if (!s.child("id").getValue().toString().equals(me.getUid()) && s.child("Ready").getValue().toString().equals("True") && !s.child("GameCod").getValue().toString().equals("")) {
                                Gasit = true;
                                opponent_id = s.child("id").getValue().toString();
                                opponent_cod = s.child("GameCod").getValue().toString();
                                opponent_name = s.child("username").getValue().toString();
                                FirebaseDatabase.getInstance().getReference().child("Users").removeEventListener(as);
                                IntraInMeci();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                Gasit = false;
                FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(as);

                //Daca nu gaseste nicio masa in 4 secunde, creez o masa goala
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {

                        Log.d("MainActivity", "Handler runnable delay");
                        if (!Gasit) {
                            FirebaseDatabase.getInstance().getReference().child("Users").removeEventListener(as);
                            CreeazaUnMeci();
                        }
                    }

                }, 4000); //Delay de 4 secunde

            }
        });
    }

    private void IntraInMeci() {
        FirebaseDatabase.getInstance().getReference("Users").child(me.getUid()).child("opponent").setValue(opponent_id);
        FirebaseDatabase.getInstance().getReference("Users").child(me.getUid()).child("GameCod").setValue(opponent_cod);
        FirebaseDatabase.getInstance().getReference("Users").child(opponent_id).child("opponent").setValue(me.getUid());
        FirebaseDatabase.getInstance().getReference("Games").child(opponent_cod).child("Player 2").setValue(myname);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cod", opponent_cod);
        editor.putString("name", opponent_name);
        editor.putString("myname", myname);
        editor.apply();
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    private void CreeazaUnMeci() {
        FirebaseDatabase.getInstance().getReference("Users").child(me.getUid()).child("GameCod").setValue(finalCod);
        FirebaseDatabase.getInstance().getReference("Games").child(finalCod).child("Player 1").setValue(myname);
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("Turn", myname);
        intent.putExtra("cod", finalCod);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                break;
            case R.id.addfriends:
                startActivity(new Intent(MainActivity.this, Add_Friends_Activity.class));
                break;
            case R.id.market:
                break;
            case R.id.buy_coins:
                break;
            case R.id.tictactoe:
                break;
            case R.id.snake:
                break;
            case R.id.chess:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        navigationView.setCheckedItem(R.id.tictactoe);
        super.onResume();
    }
}