package com.example.andichess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.andichess.checkers.Checkerboard;
import com.example.andichess.chess.Chessboard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckersGameActivity extends AppCompatActivity {

    String playerName = "";
    String sessionName = "";
    String role = "";
    String message = "";

    Checkerboard checkerboard;

    FirebaseDatabase database;
    DatabaseReference checkersRef;

    boolean t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        checkerboard = new Checkerboard(this, sessionName);
        setContentView(checkerboard);


        t = true;
        /*
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

         */
        /*
        while(t) {
            if(checkerboard.blackTurn) {
                if(role.equals("black")) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(CheckersGameActivity.this, "Your turn", Toast.LENGTH_SHORT).show();
                }
                else if(role.equals("red")) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
            // !chessboard.whiteTurn
            else {
                if(role.equals("black")) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                else if(role.equals("red")) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(CheckersGameActivity.this, "Your turn", Toast.LENGTH_SHORT).show();
                }
            }

            if(!checkerboard.gameOn) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
         */
    }
}