package com.example.andichess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChessLobbyActivity extends AppCompatActivity {

    ListView listView;
    Button buttonCreateLobby;

    List<String> sessionList;
    String playerName = "";
    String sessionName = "";

    FirebaseDatabase database;
    DatabaseReference sessionRef;
    DatabaseReference sessionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chesslobby);

        database = FirebaseDatabase.getInstance();
        SharedPreferences prefs = getSharedPreferences("PREFS", 0);
        playerName = prefs.getString("playerName", "");
        sessionName = playerName;
        listView = findViewById(R.id.sessionList);
        buttonCreateLobby = findViewById(R.id.buttonCreateSession);

        sessionList = new ArrayList<>();

        buttonCreateLobby.setOnClickListener(new View.OnClickListener() {
            // create room and add yourself as player2
            @Override
            public void onClick(View v) {
                buttonCreateLobby.setText("Create Session");
                buttonCreateLobby.setEnabled(false);
                sessionName = playerName;
                sessionRef = database.getReference("chess/" + sessionName + "/player1");
                addSessionEventListener();
                sessionRef.setValue(playerName);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // join room and add yourself as player2
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sessionName = sessionList.get(position);
                sessionRef = database.getReference("chess/" + sessionName + "/player2");
                addSessionEventListener();
                sessionRef.setValue(playerName);
            }
        });
        //show if new session appears
        addSessionsEventListener();
    }

    private void addSessionEventListener() {
        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                buttonCreateLobby.setText("Create Session");
                buttonCreateLobby.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), CheckersGameActivity.class);
                intent.putExtra("sessionName", sessionName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                buttonCreateLobby.setText("Create Session");
                buttonCreateLobby.setEnabled(false);
                Toast.makeText(ChessLobbyActivity.this, "Error Creating Session", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSessionsEventListener() {
        sessionsRef = database.getReference("sessions");
        sessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sessionList.clear();
                Iterable<DataSnapshot> sessions = snapshot.getChildren();
                for(DataSnapshot dataSnapshot : sessions) {
                    sessionList.add(dataSnapshot.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ChessLobbyActivity.this, android.R.layout.simple_list_item_1, sessionList);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //nada, here be errors
            }
        });
    }
}