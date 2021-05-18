package com.example.andichess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.andichess.checkers.Checkerboard;
import com.example.andichess.chess.Chessboard;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckersGameActivity extends AppCompatActivity {

    String playerName = "";
    String sessionName = "";
    String role = "";
    String message = "";

    FirebaseDatabase database;
    DatabaseReference chessRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new Checkerboard(this));

        if (sessionName.equals(playerName)) {
            role = "black";
            Toast.makeText(CheckersGameActivity.this, "You are black, move first", Toast.LENGTH_LONG).show();
        }
        else {
            role = "red";
        }

        if(role.equals("red")) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(CheckersGameActivity.this, "Waiting For Black Move", Toast.LENGTH_LONG).show();
        }
        else if(role.equals("OBS")) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(CheckersGameActivity.this, "Haha you are observer", Toast.LENGTH_LONG).show();
        }
    }
}