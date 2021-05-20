package com.example.andichess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andichess.chessSP.ChessSPActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText edittext;
    Button buttonChessLobby;
    Button buttonChessSP;
    Button buttonCheckersLobby;
    Button buttonConfirmName;

    String playerName = "";

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    MediaPlayer musicBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();

        edittext = findViewById(R.id.edittext);
        buttonChessLobby = findViewById(R.id.buttonChess);
        buttonChessLobby.setEnabled(false);
        buttonCheckersLobby = findViewById(R.id.buttonCheckers);
        buttonCheckersLobby.setEnabled(false);
        buttonChessSP = findViewById(R.id.buttonChessSP);
        buttonChessSP.setEnabled(false);
        buttonConfirmName = findViewById(R.id.buttonConfirmName);

        //this is where i would put my music, if it worked
        //musicBot = MediaPlayer.create(getApplicationContext(), R.raw.ambientElectronicCC0);

        buttonConfirmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edittext.equals("")) {
                    playerName = edittext.getText().toString();
                    DatabaseReference playerRef = database.getReference();
                    playerRef.child("players").child(playerName).setValue(playerName);
                    buttonChessLobby.setEnabled(true);
                    buttonCheckersLobby.setEnabled(true);
                    buttonChessSP.setEnabled(true);
                    buttonConfirmName.setEnabled(false);
                    edittext.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonChessLobby.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // logging in
                playerName = edittext.getText().toString();
                edittext.setText("");
                if (!playerName.equals("")) {
                    buttonChessLobby.setText("Joining Lobby Room");
                    buttonChessLobby.setEnabled(false);
                    databaseRef = database.getReference("players/" + playerName);
                    addEventListener("chess");
                    databaseRef.setValue("");
                }
            }
        });

        buttonCheckersLobby.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                playerName = edittext.getText().toString();
                edittext.setText("");
                if (!playerName.equals("")) {
                    buttonCheckersLobby.setText("Joining Lobby Room");
                    buttonCheckersLobby.setEnabled(false);
                    databaseRef = database.getReference("players/" + playerName);
                    addEventListener("checkers");
                    databaseRef.setValue("");
                }
            }
        });

        buttonChessSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = edittext.getText().toString();
                edittext.setText("");
                if (!playerName.equals("")) {
                    buttonCheckersLobby.setText("Joining ChessSP");
                    buttonCheckersLobby.setEnabled(false);
                    databaseRef = database.getReference("players/" + playerName);
                    addEventListener("chessSP");
                    databaseRef.setValue("");
                }
            }
        });
    }
    private void addEventListener(String activity){
        databaseRef.addValueEventListener(new ValueEventListener() {
            //success - to the next screen (Chess)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!playerName.equals("")) {
                    SharedPreferences prefs = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(), ChessLobbyActivity.class));
                    finish();
                    switch (activity) {
                        case "chess":
                            startActivity(new Intent(getApplicationContext(), ChessLobbyActivity.class));
                            finish();
                            break;
                        case "checkers":
                            startActivity(new Intent(getApplicationContext(), CheckersLobbyActivity.class));
                            finish();
                            break;
                        case "chessSP":
                            startActivity(new Intent(getApplicationContext(), ChessSPActivity.class));
                            finish();
                            break;
                        case "null":
                            break;
                    }
                }
            }
            // error
            @SuppressLint("SetTextI18n")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                buttonChessLobby.setText("Join Chess Lobby");
                buttonChessLobby.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                /*
                switch () {
                    case "chess":
                        buttonChessLobby.setText("Join Chess Lobby");
                        buttonChessLobby.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        break;
                    case "ticktactoe":
                        buttonTickTacToeLobby.setText("Join Tick Tac Toe Lobby");
                        buttonTickTacToeLobby.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        break;
                    case "checkers":
                        buttonCheckersLobby.setText("Join Checkers Lobby");
                        buttonCheckersLobby.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        break;
                }
                 */
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //musicBot.start();
    }
}