package com.example.databasetictoe.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.databasetictoe.GameActivity;
import com.example.databasetictoe.MainActivity;
import com.example.databasetictoe.Model.GameUsers;
import com.example.databasetictoe.Model.User;
import com.example.databasetictoe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Random;

public class GameFragment extends Fragment {

    Button b1_1,b1_2,b1_3,b2_1,b2_2,b2_3,b3_1,b3_2,b3_3;
    Button RestartGame,Back;

    TextView textView;

    FirebaseUser me;

    String turn,myname,cod,opponent,cod2;

    Boolean left;

    ValueEventListener b,a,al,av;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        textView = view.findViewById(R.id.text_turn);
        b1_1 = view.findViewById(R.id.b1);
        b1_2 = view.findViewById(R.id.b2);
        b1_3 = view.findViewById(R.id.b3);
        b2_1 = view.findViewById(R.id.b4);
        b2_2 = view.findViewById(R.id.b5);
        b2_3 = view.findViewById(R.id.b6);
        b3_1 = view.findViewById(R.id.b7);
        b3_2 = view.findViewById(R.id.b8);
        b3_3 = view.findViewById(R.id.b9);
        RestartGame = view.findViewById(R.id.restart_game);
        Back = view.findViewById(R.id.back_lobby);

        me = FirebaseAuth.getInstance().getCurrentUser();
        opponent="";
        left = false;

        GameActivity activity = (GameActivity) getActivity();
        turn = activity.turn();
        cod = activity.cod();
        cod2 = activity.cod2();
        myname = activity.myname();

        if (!cod.equals("")) // Cel care a creat masa intra aici (hostul - Player 1)
        {

            //myname - numele meu
            //opponent - numele adversarului
            //cod - codul mesei
            //turn - randul celui care trebuie sa mute

            if(myname == null)
                myname = turn;

            //Am creat un event listener astfel incat pana intra adversarul, sa-mi afiseze "Waiting opponent"
            //si in momentul cand intra sa imi preia numele acestuia si sa-mi schimbe statusul
            //acestuia de la "Ready" in "False" pentru a nu mai intra la alta masa
            a = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("Player 2").exists())
                    {
                        if(opponent.equals("")) {
                            textView.setText(turn + "'s turn.");
                            opponent = snapshot.child("Player 2").getValue().toString();
                            FirebaseDatabase.getInstance().getReference("Users").child(me.getUid()).child("Ready").setValue("False");
                        }
                    }
                    else
                        textView.setText("Waiting for opponent");

                    if (snapshot.child("Opponent_left").exists())
                    {
                        setButtonsFalse();
                        textView.setText("Opponent left");
                        textView.setTextSize(32);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).removeEventListener(a);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").removeEventListener(b);
                        AparButoane();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            FirebaseDatabase.getInstance().getReference("Games").child(cod).addValueEventListener(a);

            //Scriu in baza de date cine e la rand sa puna X sau O
            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(turn);

            //Am creat al doilea event listener astfel incat sa preiau mutarile adversarului si sa blochez butoanele care au fost apasate,
            //iar cand exista vreun castigator, distrug event listener-ul si afisez butoanele de intoarcere si mesajul corespunzator
            //Totodata, imi verific mutarile astfel incat sa depistez daca am castigat si actualizez textul pentru a-mi afisa
            //cine e la rand
            b = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    VerificaMutarile();
                    if (!snapshot.child("Castigator").exists()) {
                   // VerificaMutarile();

                    if (snapshot.child("1_1").exists() && b1_1.isEnabled())
                    {
                        b1_1.setEnabled(false);
                        if(snapshot.child("1_1").getValue().equals(myname))
                            b1_1.setText("X");
                        else b1_1.setText("O");
                    }
                    if (snapshot.child("1_2").exists() && b1_2.isEnabled())
                    {
                        b1_2.setEnabled(false);
                        if(snapshot.child("1_2").getValue().equals(myname))
                            b1_2.setText("X");
                        else b1_2.setText("O");
                    }
                    if (snapshot.child("1_3").exists() && b1_3.isEnabled())
                    {
                        b1_3.setEnabled(false);
                        if(snapshot.child("1_3").getValue().equals(myname))
                            b1_3.setText("X");
                        else b1_3.setText("O");
                    }
                    if (snapshot.child("2_1").exists() && b2_1.isEnabled())
                    {
                        b2_1.setEnabled(false);
                        if(snapshot.child("2_1").getValue().equals(myname))
                            b2_1.setText("X");
                        else b2_1.setText("O");
                    }
                    if (snapshot.child("2_2").exists() && b2_2.isEnabled())
                    {
                        b2_2.setEnabled(false);
                        if(snapshot.child("2_2").getValue().equals(myname))
                            b2_2.setText("X");
                        else b2_2.setText("O");
                    }
                    if (snapshot.child("2_3").exists() && b2_3.isEnabled())
                    {
                        b2_3.setEnabled(false);
                        if(snapshot.child("2_3").getValue().equals(myname))
                            b2_3.setText("X");
                        else b2_3.setText("O");
                    }
                    if (snapshot.child("3_1").exists() && b3_1.isEnabled())
                    {
                        b3_1.setEnabled(false);
                        if(snapshot.child("3_1").getValue().equals(myname))
                            b3_1.setText("X");
                        else b3_1.setText("O");
                    }
                    if (snapshot.child("3_2").exists() && b3_2.isEnabled())
                    {
                        b3_2.setEnabled(false);
                        if(snapshot.child("3_2").getValue().equals(myname))
                            b3_2.setText("X");
                        else b3_2.setText("O");
                    }
                    if (snapshot.child("3_3").exists() && b3_3.isEnabled())
                    {
                        b3_3.setEnabled(false);
                        if(snapshot.child("3_3").getValue().equals(myname))
                            b3_3.setText("X");
                        else b3_3.setText("O");
                    }
                      if(!snapshot.child("Turn").equals(turn)) {
                        turn = snapshot.child("Turn").getValue().toString();
                        textView.setText(turn + "'s turn.");
                        joaca1(turn);
                        }
                    } else {
                        if (snapshot.child("Castigator").getValue().equals(opponent))
                        {
                            lose();
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).removeEventListener(a);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").removeEventListener(b);
                        }
                        else if (snapshot.child("Castigator").getValue().equals(myname))
                        {
                            textView.setText("AI CASTIGAT");
                            textView.setTextSize(32);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).removeEventListener(a);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").removeEventListener(b);
                        }
                        else if(snapshot.child("Castigator").getValue().equals("Egal"))
                        {
                            Egal();
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).removeEventListener(a);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").removeEventListener(b);
                        }
                        else
                        {
                            left=true;
                            setButtonsFalse();
                            textView.setText("Opponent left the game");
                            textView.setTextSize(23);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).removeEventListener(a);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").removeEventListener(b);
                            AparButoane();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").addValueEventListener(b);

        }
        else // Cel care a gasit masa gata facuta intra aici (guest - Player 2)
        {
            //myname - numele meu
            //opponent - numele adversarului
            //cod2 - codul de la masa de joc
            //turn - randul celui care trebuie sa mute

            //Schimb statusul de la "Ready" in "False" pentru a nu mai intra la alta masa
            FirebaseDatabase.getInstance().getReference("Users").child(me.getUid()).child("Ready").setValue("False");
            if(opponent.equals(""))
                opponent = turn;

            //Setez textul astfel incat sa-mi afiseze cine e la rand
            textView.setText(turn + "'s turn.");

             al = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Opponent_left").exists())
                {
                    setButtonsFalse();
                    textView.setText("Opponent left");
                    textView.setTextSize(32);
                    FirebaseDatabase.getInstance().getReference("Games").child(cod2).removeEventListener(al);
                    FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").removeEventListener(b);
                    AparButoane();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
            FirebaseDatabase.getInstance().getReference("Games").child(cod2).addValueEventListener(al);

            //Am creat un event listener astfel incat sa preiau mutarile adversarului si sa blochez butoanele care au fost apasate,
            //iar cand exista vreun castigator, distrug event listener-ul si afisez butoanele de intoarcere si mesajul corespunzator
            //Totodata, imi verific mutarile astfel incat sa depistez daca am castigat si actualizez textul pentru a-mi afisa
            //cine e la rand
            b = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child("Castigator").exists()) {
                        VerificaMutarile();

                        if (snapshot.child("1_1").exists() && b1_1.isEnabled()) {
                            b1_1.setEnabled(false);
                            if (snapshot.child("1_1").getValue().equals(myname))
                                b1_1.setText("O");
                            else b1_1.setText("X");
                        }
                        if (snapshot.child("1_2").exists() && b1_2.isEnabled()) {
                            b1_2.setEnabled(false);
                            if (snapshot.child("1_2").getValue().equals(myname))
                                b1_2.setText("O");
                            else b1_2.setText("X");
                        }
                        if (snapshot.child("1_3").exists() && b1_3.isEnabled()) {
                            b1_3.setEnabled(false);
                            if (snapshot.child("1_3").getValue().equals(myname))
                                b1_3.setText("O");
                            else b1_3.setText("X");
                        }
                        if (snapshot.child("2_1").exists() && b2_1.isEnabled()) {
                            b2_1.setEnabled(false);
                            if (snapshot.child("2_1").getValue().equals(myname))
                                b2_1.setText("O");
                            else b2_1.setText("X");
                        }
                        if (snapshot.child("2_2").exists() && b2_2.isEnabled()) {
                            b2_2.setEnabled(false);
                            if (snapshot.child("2_2").getValue().equals(myname))
                                b2_2.setText("O");
                            else b2_2.setText("X");
                        }
                        if (snapshot.child("2_3").exists() && b2_3.isEnabled()) {
                            b2_3.setEnabled(false);
                            if (snapshot.child("2_3").getValue().equals(myname))
                                b2_3.setText("O");
                            else b2_3.setText("X");
                        }
                        if (snapshot.child("3_1").exists() && b3_1.isEnabled()) {
                            b3_1.setEnabled(false);
                            if (snapshot.child("3_1").getValue().equals(myname))
                                b3_1.setText("O");
                            else b3_1.setText("X");
                        }
                        if (snapshot.child("3_2").exists() && b3_2.isEnabled()) {
                            b3_2.setEnabled(false);
                            if (snapshot.child("3_2").getValue().equals(myname))
                                b3_2.setText("O");
                            else b3_2.setText("X");
                        }
                        if (snapshot.child("3_3").exists() && b3_3.isEnabled()) {

                            b3_3.setEnabled(false);
                            if (snapshot.child("3_3").getValue().equals(myname))
                                b3_3.setText("O");
                            else b3_3.setText("X");
                        }
                        if(snapshot.child("Turn").exists())
                           if (!snapshot.child("Turn").equals(turn)) {
                                turn = snapshot.child("Turn").getValue().toString();
                                textView.setText(turn + "'s turn.");
                                joaca2(turn);
                            }
                        } else {
                        if (snapshot.child("Castigator").getValue().equals(opponent))
                        {
                            lose();
                            FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").removeEventListener(b);
                        }
                        else if (snapshot.child("Castigator").getValue().equals("Egal"))
                        {
                            Egal();
                            FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").removeEventListener(b);
                        }
                        else if (snapshot.child("Castigator").getValue().equals(myname))
                        {
                            textView.setText("AI CASTIGAT");
                            textView.setTextSize(32);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").removeEventListener(b);
                        }
                        else
                        {
                            left=true;
                            setButtonsFalse();
                            textView.setText("Opponent left the game");
                            textView.setTextSize(23);
                            FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").removeEventListener(b);
                            AparButoane();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").addValueEventListener(b);
        }

        return view;
    }

    List<String> mutari = new ArrayList<>(); //aici imi tin evidenta mutarilor mele

    //Functia este pentru host - Player 1.
    //In momentul cand apasa un buton care nu a fost apasat, blocheaza butonul si afiseaza "X" pe buton
    //adauga mutarea acestuia in baza de date cu numele lui si actualizeaza cine e la rand
    private void joaca1(String tur)
    {
            b1_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b1_1.getText().equals("") && tur.equals(myname)) {
                        b1_1.setEnabled(false);
                        mutari.add("b1_1");
                        b1_1.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("1_1").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });

            b1_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b1_2.getText().equals("") && tur.equals(myname)) {
                        b1_2.setEnabled(false);
                        mutari.add("b1_2");
                        b1_2.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("1_2").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });

            b1_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b1_3.getText().equals("") && tur.equals(myname)) {
                        b1_3.setEnabled(false);
                        mutari.add("b1_3");
                        b1_3.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("1_3").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });

            b2_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b2_1.getText().equals("") && tur.equals(myname)) {
                        b2_1.setEnabled(false);
                        mutari.add("b2_1");
                        b2_1.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("2_1").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b2_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b2_2.getText().equals("") && tur.equals(myname)) {
                        b2_2.setEnabled(false);
                        mutari.add("b2_2");
                        b2_2.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("2_2").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b2_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (b2_3.getText().equals("") && tur.equals(myname)) {
                        b2_3.setEnabled(false);
                        mutari.add("b2_3");
                        b2_3.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("2_3").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b3_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (b3_1.getText().equals("") && tur.equals(myname)) {
                        b3_1.setEnabled(false);
                        mutari.add("b3_1");
                        b3_1.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("3_1").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b3_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b3_2.getText().equals("") && tur.equals(myname)) {
                        b3_2.setEnabled(false);
                        mutari.add("b3_2");
                        b3_2.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("3_2").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b3_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (b3_3.getText().equals("") && tur.equals(myname)) {
                        b3_3.setEnabled(false);
                        mutari.add("b3_3");
                        b3_3.setText("X");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("3_3").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
    }

    //Functia este pentru guest - Player 2.
    //In momentul cand apasa un buton care nu a fost apasat, blocheaza butonul si afiseaza "O" pe buton
    //adauga mutarea acestuia in baza de date cu numele lui si actualizeaza cine e la rand
    private void joaca2(String tur)
    {
            b1_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b1_1.getText().equals("") && tur.equals(myname)) {
                        b1_1.setEnabled(false);
                        mutari.add("b1_1");
                        b1_1.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("1_1").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b1_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b1_2.getText().equals("") && tur.equals(myname)) {
                        b1_2.setEnabled(false);
                        mutari.add("b1_2");
                        b1_2.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("1_2").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b1_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b1_3.getText().equals("") && tur.equals(myname)) {
                        b1_3.setEnabled(false);
                        mutari.add("b1_3");
                        b1_3.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("1_3").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b2_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b2_1.getText().equals("") && tur.equals(myname)) {
                        b2_1.setEnabled(false);
                        mutari.add("b2_1");
                        b2_1.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("2_1").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b2_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b2_2.getText().equals("") && tur.equals(myname)) {
                        b2_2.setEnabled(false);
                        mutari.add("b2_2");
                        b2_2.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("2_2").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b2_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b2_3.getText().equals("") && tur.equals(myname)) {
                        b2_3.setEnabled(false);
                        mutari.add("b2_3");
                        b2_3.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("2_3").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b3_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b3_1.getText().equals("") && tur.equals(myname)) {
                        b3_1.setEnabled(false);
                        mutari.add("b3_1");
                        b3_1.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("3_1").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b3_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b3_2.getText().equals("") && tur.equals(myname)) {
                        b3_2.setEnabled(false);
                        mutari.add("b3_2");
                        b3_2.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("3_2").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
            b3_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (b3_3.getText().equals("") && tur.equals(myname)) {
                        b3_3.setEnabled(false);
                        mutari.add("b3_3");
                        b3_3.setText("O");
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("3_3").setValue(myname);
                        FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Turn").setValue(opponent);
                        return;
                    }
                }
            });
    }

    //Functia blocheaza toate butoanele
    private void setButtonsFalse()
    {
        b1_1.setEnabled(false);
        b1_2.setEnabled(false);
        b1_3.setEnabled(false);
        b2_1.setEnabled(false);
        b2_2.setEnabled(false);
        b2_3.setEnabled(false);
        b3_1.setEnabled(false);
        b3_2.setEnabled(false);
        b3_3.setEnabled(false);
    }

    //Functia afiseaza mesajul "AI PIERDUT"
    private void lose()
    {
        setButtonsFalse();
        textView.setText("AI PIERDUT");
        textView.setTextSize(32);
        AparButoane();
    }

    //Functia afiseaza mesajul "AI CASTIGAT" si adauga in baza de date castigatorul
    private void AmCastigat()
    {
        if(cod.equals("")) {

            FirebaseDatabase.getInstance().getReference("Games").child(cod2).child("Game").child("Castigator").setValue(myname);
            setButtonsFalse();
            textView.setText("AI CASTIGAT");
            textView.setTextSize(32);
            AparButoane();
        }
        else {

            FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Castigator").setValue(myname);
            setButtonsFalse();
            textView.setText("AI CASTIGAT");
            textView.setTextSize(32);
            AparButoane();
        }
    }

    //Functia verifica daca este egalitate doar pentru host - Player 1
    //iar pentru ambii jucatori verifica mutarile acestora, dandu-si seama daca cineva a castigat
    private void VerificaMutarile()
    {
        if(mutari.size() > 2)
        {
            if(mutari.contains("b1_1") && mutari.contains("b1_2") && mutari.contains("b1_3"))
                AmCastigat();
            else if(mutari.contains("b2_1") && mutari.contains("b2_2") && mutari.contains("b2_3"))
                AmCastigat();
            else if(mutari.contains("b3_1") && mutari.contains("b3_2") && mutari.contains("b3_3"))
                AmCastigat();
            else if(mutari.contains("b1_1") && mutari.contains("b2_1") && mutari.contains("b3_1"))
                AmCastigat();
            else if(mutari.contains("b1_2") && mutari.contains("b2_2") && mutari.contains("b3_2"))
                AmCastigat();
            else if(mutari.contains("b1_3") && mutari.contains("b2_3") && mutari.contains("b3_3"))
                AmCastigat();
            else if(mutari.contains("b1_1") && mutari.contains("b2_2") && mutari.contains("b3_3"))
                AmCastigat();
            else if(mutari.contains("b1_3") && mutari.contains("b2_2") && mutari.contains("b3_1"))
                AmCastigat();
            else if (mutari.size() > 4) {
                FirebaseDatabase.getInstance().getReference("Games").child(cod).child("Game").child("Castigator").setValue("Egal");
            }
        }
    }

    //Functia afiseaza butoanele de "Restart" si "Back to lobby"
    private void AparButoane()
    {
        RestartGame.setEnabled(true);
        RestartGame.setVisibility(View.VISIBLE);
        Back.setEnabled(true);
        Back.setVisibility(View.VISIBLE);
        /*RestartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }); */
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MainActivity.class);
                if(cod.equals(""))
                {
                   // intent.putExtra("cod",cod2);
                    startActivity(intent);
                    getActivity().finish();
                }
                else {
                   // intent.putExtra("cod", cod);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    //Afiseaza mesajul "Egalitate"
    private void Egal()
    {
        setButtonsFalse();
        textView.setText("Egalitate");
        textView.setTextSize(32);
        AparButoane();
    }


    //Daca ies din aplicatie, adauga in baza de date mesajul "Opponent left"
}
