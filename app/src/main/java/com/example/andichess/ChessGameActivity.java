package com.example.andichess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChessGameActivity extends AppCompatActivity {

    Button button;

    String playerName = "";
    String sessionName = "";
    String role = "";
    String message = "";

    FirebaseDatabase database;
    DatabaseReference chessRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_game);

        button = findViewById(R.id.chessButton);
        button.setEnabled(false);

        database = FirebaseDatabase.getInstance();

        SharedPreferences prefs = getSharedPreferences("PREFS", 0);
        playerName = prefs.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sessionName = extras.getString("sessionName");
            if (sessionName.equals(playerName)) {
                role = "white";
            }
            else {
                role = "black";
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                message = role + ":Poked!";
                chessRef.setValue(message);
            }
        });

        chessRef = database.getReference("sessions/" + sessionName + "/messages");
        message = role + "Poked!";
        chessRef.setValue(message);
        addSessionEventListener();
    }

    private void addSessionEventListener() {
        chessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(role.equals("host")) {
                    if (snapshot.getValue(String.class).contains("guest:")) {
                        button.setEnabled(true);
                        Toast.makeText(ChessGameActivity.this, "" + snapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (snapshot.getValue(String.class).contains("host:")) {
                        button.setEnabled(true);
                        Toast.makeText(ChessGameActivity.this, "" + snapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}