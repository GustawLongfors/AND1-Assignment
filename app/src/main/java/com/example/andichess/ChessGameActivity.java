package com.example.andichess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.andichess.chess.Chessboard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.Activity;
import android.view.Window;
import android.os.Bundle;
import android.view.WindowManager;

public class ChessGameActivity extends AppCompatActivity {

    Button button;

    String playerName = "";
    String sessionName = "";
    String role = "";
    String message = "";

    Chessboard chessboard;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference chessRef;

    MediaPlayer musicBot;

    boolean t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new Chessboard(this, sessionName));

        // this is where i would put my music, if it worked
        //musicBot = MediaPlayer.create(getApplicationContext(), R.raw.Music1CC0);
        /*
        t = true;

        if (sessionName.equals(playerName)) {
            role = "white";
            Toast.makeText(ChessGameActivity.this, "You are white, move first", Toast.LENGTH_LONG).show();
        }
        else {
            role = "black";
        }

        if(role.equals("black")) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(ChessGameActivity.this, "Waiting For White Move", Toast.LENGTH_LONG).show();
        }
        else if(role.equals("OBS")) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(ChessGameActivity.this, "Haha you are observer", Toast.LENGTH_LONG).show();
        }

         */

        // disgusting piece of code but i cant figure out a different solution because head is hurt
        /*
        while(t) {
            if(chessboard.whiteTurn) {
                if(role.equals("white")) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(ChessGameActivity.this, "Your turn", Toast.LENGTH_SHORT).show();
                }
                else if(role.equals("black")) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
            // !chessboard.whiteTurn
            else {
                if(role.equals("white")) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                else if(role.equals("black")) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(ChessGameActivity.this, "Your turn", Toast.LENGTH_SHORT).show();
                }
            }

            if(!chessboard.gameOn) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
         */
    }



    // probably useless code but i keep it because i like it

    /*

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

        chessRef = database.getReference("chess/" + sessionName + "/messages");
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

     */
}