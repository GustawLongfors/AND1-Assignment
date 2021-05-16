package com.example.andichess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText edittext;
    Button buttonChessLobby;
    Button buttonTickTacToeLobby;
    Button buttonCheckersLobby;

    String playerName = "";

    FirebaseDatabase database;
    DatabaseReference playerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edittext = findViewById(R.id.edittext);
        buttonChessLobby = findViewById(R.id.buttonChess);
        buttonTickTacToeLobby = findViewById(R.id.buttonTickTacToe);

        database = FirebaseDatabase.getInstance();

        // check for player existance
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        if (!playerName.equals("")) {
            playerRef = database.getReference("players/" + playerName);
            addEventListener("chess");
            playerRef.setValue("");
        }
        buttonTickTacToeLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = edittext.getText().toString();
                edittext.setText("");
                if(!playerName.equals("")) {
                    buttonTickTacToeLobby.setText("Joining Lobby Room");
                    buttonTickTacToeLobby.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    addEventListener("ticktactoe");
                    playerRef.setValue("");
                }
            }
        });
        buttonChessLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logging in
                playerName = edittext.getText().toString();
                edittext.setText("");
                if(!playerName.equals("")) {
                    buttonChessLobby.setText("Joining Lobby Room");
                    buttonChessLobby.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    addEventListener("chess");
                    playerRef.setValue("");
                }
            }
        });
        buttonCheckersLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = edittext.getText().toString();
                edittext.setText("");
                if (!playerName.equals("")) {
                    buttonCheckersLobby.setText("Joining Lobby Room");
                    buttonCheckersLobby.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    addEventListener("checkers");
                    playerRef.setValue("");
                }
            }
        });
    }

    private void addEventListener(String activity) {
        playerRef.addValueEventListener(new ValueEventListener() {
            //success - to the next screen (Chess)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!playerName.equals("")) {
                    SharedPreferences prefs = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();
                    if(activity.equals("chess")) {
                        startActivity(new Intent(getApplicationContext(), ChessLobbyActivity.class));
                        finish();
                    }
                    else if (activity.equals("ticktactoe")) {
                        startActivity(new Intent(getApplicationContext(), TickTacToeLobbyActivity.class));
                        finish();
                    }
                    else if (activity.equals("checkers")) {
                        startActivity(new Intent(getApplicationContext(), CheckersLobbyActivity.class));
                        finish();
                    }
                }
            }
            // error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(activity.equals("chess")) {
                    buttonChessLobby.setText("Join Chess Lobby");
                    buttonChessLobby.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
                else if (activity.equals("ticktactoe")) {
                    buttonTickTacToeLobby.setText("Join Tick Tac Toe Lobby");
                    buttonTickTacToeLobby.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
                else if (activity.equals("checkers")) {
                    buttonCheckersLobby.setText("Join Checkers Lobby");
                    buttonCheckersLobby.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}