package com.example.andichess.checkers;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.andichess.R;
import com.example.andichess.chess.CharacterSprite;

import java.util.ArrayList;

enum moveMode {
    DIAGONAL,
    REVERSE_DIAGONAL,
}

public class Checkerboard extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private checkersMainThread thread;
    private ArrayList<checkersCharacterSprite> board; // fields on a chessboard
    private ArrayList<checkersCharacterSprite> pieces;
    private ArrayList<checkersCharacterSprite> selected; // yellow tiles visible if field is selected
    private checkersCharacterSprite selectedObject; // currently selected piece
    private boolean blackTurn; // if true it's black player's turn (black moves first)
    private int[] points; // [0] = score white [1] = score black
    private boolean gameOn; // if true chessboard responds to clicks
    private Paint textPaint; // text display style (size and colour)

    public Checkerboard(Context context) {
        super(context);
        setOnTouchListener(this);
        getHolder().addCallback(this);
        board = new ArrayList<>();
        pieces = new ArrayList<>();
        selected = new ArrayList<>();
        points = new int[]{0, 0};
        thread = new checkersMainThread(getHolder(), this);
        setFocusable(true);
        blackTurn = true;
        gameOn = true;
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;


        // create board
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
                    board.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.field_green,
                            o), 110 * x, 110 * y, objTypeCheckers.FIELD_RED));
                } else {
                    board.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.field_purple,
                            o), 110 * x, 110 * y, objTypeCheckers.FIELD_BLACK));
                }
            }
        }

        // ----------------- BLACK PIECES ----------------- //

        //  Black pieces added in for loop below = [110*2, 110], [110*4, 110], [110*6, 110], [110*8, 110]

        for (int x = 1; x <= 4; x++) {
            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_black, o),
                    110 * 2 *x, 110, objTypeCheckers.MAN_BLACK));
        }

        //  Black pieces added in for loop below = [110*1, 110 * 2], [110*3, 110 * 2], [110*5, 110 * 2], [110*7, 110 * 2]

        for (int x = 1; x <= 4; x++) {
            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_black, o),
                    110 * (x * 2 - 1) , 110 * 2, objTypeCheckers.MAN_BLACK));
        }

        //  Black pieces added in for loop below = [110*2, 110 * 3], [110*4, 110 * 3], [110*6, 110 * 3], [110*8, 110 * 3]

        for (int x = 1; x <= 4; x++) {
            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_black, o),
                    110 * 2 * x, 110 * 3, objTypeCheckers.MAN_BLACK));
        }

        // ----------------- RED PIECES ----------------- //

        //  Red pieces added in for loop below = [110*1, 110 * 6], [110*3, 110 * 6], [110*5, 110 * 6], [110*7, 110 * 6]

        for (int x = 1; x <= 4; x++) {
            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_white, o),
                    110 * (x * 2 - 1), 110 * 6, objTypeCheckers.MAN_RED));
        }

        //  Red pieces added in for loop below = [110*2, 110 * 7], [110*4, 110 * 7], [110*6, 110 * 7], [110*8, 110 * 7]

        for (int x = 1; x <= 4; x++) {
            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_white, o),
                    110 * x * 2 , 110 * 7, objTypeCheckers.MAN_RED));
        }

        //  Red pieces added in for loop below = [110*1, 110 * 8], [110*3, 110 * 8], [110*5, 110 * 8], [110*7, 110 * 8]

        for (int x = 1; x <= 4; x++) {
            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_white, o),
                    110 * (x * 2 - 1), 110 * 8, objTypeCheckers.MAN_RED));
        }

        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                selected.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.field_selected,
                        o), 110 * x, 110 * y, objTypeCheckers.FIELD_SELECTED));
                selected.get(selected.size() - 1).setVisible(false);
            }
        }

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            for (checkersCharacterSprite sprite : board) {
                sprite.draw(canvas);
            }
            for (checkersCharacterSprite sprite : selected) {
                sprite.draw(canvas);
            }
            for (checkersCharacterSprite sprite : pieces) {
                sprite.draw(canvas);
            }
            updateText(blackTurn, points, canvas, textPaint, gameOn);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return  false;
    }

    private static void updateText(boolean blackTurn, int[] points, Canvas canvas, Paint textPaint, boolean gameOn) {
        if (!gameOn) {
            canvas.drawText((blackTurn ? "Red" : "Black") + " Wins!",
                    3* CharacterSprite.size, 13*CharacterSprite.size, textPaint);
            return;
        }

        canvas.drawText("Move: " + ( blackTurn ? "Black" : "Red"), 3*CharacterSprite.size,
                10*CharacterSprite.size, textPaint);
        canvas.drawText("Points:", CharacterSprite.size, 11*CharacterSprite.size, textPaint);
        canvas.drawText("Black: " + points[0], CharacterSprite.size, 12*CharacterSprite.size,
                textPaint);
        canvas.drawText("Red: " + points[1], CharacterSprite.size, 13*CharacterSprite.size,
                textPaint);
    }
}

