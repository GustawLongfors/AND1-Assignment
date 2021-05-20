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
import com.example.andichess.chess.objType;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String sessionName;

    public Checkerboard(Context context, String sn) {
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

        sessionName = sn;
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
                    110 * 2 * x, 110, objTypeCheckers.MAN_BLACK));
        }

        //  Black pieces added in for loop below = [110*1, 110 * 2], [110*3, 110 * 2], [110*5, 110 * 2], [110*7, 110 * 2]

        for (int x = 1; x <= 4; x++) {
            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_black, o),
                    110 * (x * 2 - 1), 110 * 2, objTypeCheckers.MAN_BLACK));
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
                    110 * x * 2, 110 * 7, objTypeCheckers.MAN_RED));
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
        if (!gameOn) return false;

        if (selectedObject != null) {
            // We clicked while having a piece already chosen, check if we can move here
            checkersCharacterSprite newSelectedObject = null;
            for (checkersCharacterSprite sprite : pieces) {
                if (sprite.isColliding((int) event.getX(), (int) event.getY())) {
                    // Newly selected object is another piece
                    newSelectedObject = sprite;
                    break;
                }
            }
            if (newSelectedObject == null) {
                for (checkersCharacterSprite sprite : board) {
                    if (sprite.isColliding((int) event.getX(), (int) event.getY())) {
                        // Newly selected object is a field
                        newSelectedObject = sprite;
                        break;
                    }
                }
            }
            if (newSelectedObject == null) {
                for (checkersCharacterSprite field : selected) {
                    if (field.isColliding(selectedObject.getX(), selectedObject.getY())) {
                        // User touched outside the board, deselecting previous field and quitting
                        field.setVisible(false);
                        selectedObject = null;
                        break;
                    }
                }
                return false;
            }

            int[] position = {selectedObject.getX() / CharacterSprite.size, selectedObject.getY() / CharacterSprite.size};
            int[] destination = {newSelectedObject.getX() / CharacterSprite.size, newSelectedObject.getY() / CharacterSprite.size};
            boolean moved = false; // if true then a correct move was performed
            boolean isSomethingInTheWay = false;

            // board positions, xy
            // 11 21 31 41 51 61 71 81
            // 12 22 32 42 52 62 72 82
            // 13 23 33 43 53 63 73 83
            // 14 24 34 44 54 64 74 84
            // 15 25 35 45 55 65 75 85
            // 16 26 36 46 56 66 76 86
            // 17 27 37 47 57 67 77 87
            // 18 28 38 48 58 68 78 88

            switch (selectedObject.getType()) {
                case MAN_BLACK:
                    if (destination[0] == position[0] && destination[1] == position[1] - 1) {
                        if (!anyObstacles(pieces, position, destination, moveMode.DIAGONAL, false)) {
                            int[] result = moveIfPossible(pieces, selectedObject, blackTurn, destination);
                            moved = result[0] == 1;
                            points[blackTurn ? 0 : 1] += result[1];
                        }
                        if (selectedObject.getY() == CharacterSprite.size * 8) {
                            BitmapFactory.Options o = new BitmapFactory.Options();
                            o.inScaled = false;
                            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.king_black, o), selectedObject.getX(), 880, objTypeCheckers.KING_BLACK, 1));
                            pieces.remove(selectedObject);
                        }
                        break;
                    }
                case MAN_RED:
                    if (destination[0] == position[0] && destination[1] == position[1] + 1) {
                        if (!anyObstacles(pieces, position, destination, moveMode.DIAGONAL, false)) {
                            int[] result = moveIfPossible(pieces, selectedObject, blackTurn, destination);
                            moved = result[0] == 1;
                            points[blackTurn ? 0 : 1] += result[1];
                        }
                        if (selectedObject.getY() == CharacterSprite.size) {
                            BitmapFactory.Options o = new BitmapFactory.Options();
                            o.inScaled = false;
                            pieces.add(new checkersCharacterSprite(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.king_white, o), selectedObject.getX(), 880, objTypeCheckers.KING_RED, 1));
                            pieces.remove(selectedObject);
                        }
                        break;
                    }
                case KING_BLACK:
                case KING_RED: {
                    if (abs(destination[0] - position[0]) == abs(destination[1] - position[1])) {
                        if (!anyObstacles(pieces, position, destination, moveMode.DIAGONAL, false)) {
                            int[] result = moveIfPossible(pieces, selectedObject, blackTurn, destination);
                            moved = result[0] == 1;
                            points[blackTurn ? 0 : 1] += result[1];
                        }
                    }
                    break;
                }
            }
            for (checkersCharacterSprite field : selected) {
                field.setVisible(false);
            }
            // Switch players if the correct move was performed
            if (moved) {
                blackTurn = !blackTurn;
                // end game if mate
                if (isWin(pieces, blackTurn)) {
                    gameOn = false;
                }
            }
            // Deselect object
            selectedObject = null;
        }
        else {
            // We clicked and we DO NOT have a piece already chosen
            for (checkersCharacterSprite sprite : pieces) {
                if (sprite.isBlack() == blackTurn && sprite.isColliding((int) event.getX(), (int) event.getY())) {
                    selectedObject = sprite;
                    for (checkersCharacterSprite field : selected) {
                        if (field.isColliding((int) event.getX(), (int) event.getY())) {
                            field.setVisible(true);
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isWin(ArrayList<checkersCharacterSprite> pieces, boolean blackTurn) {
        for (checkersCharacterSprite sprite : pieces) {
            if (blackTurn && sprite.isBlack()) {
                return false;
            } else if (!blackTurn && !sprite.isBlack()) {
                return false;
            }
        }
        return true;
    }

    private static int[] moveIfPossible(ArrayList<checkersCharacterSprite> pieces, checkersCharacterSprite selectedObject, boolean blackTurn, int[] destination) {
        int[] result = new int[]{0, 0};
        for (checkersCharacterSprite sprite : pieces) {
            if (sprite.isColliding(destination[0] * checkersCharacterSprite.size, destination[1] * checkersCharacterSprite.size)) {
                if (blackTurn) {
                    if (sprite.isBlack()) {
                        return result;
                    }
                } else {
                    if (!sprite.isBlack()) {
                        return result;
                    }
                }
                int[] origin = new int[]{selectedObject.getX(), selectedObject.getY()};
                if(takeIfPossible(pieces, origin, destination)[0] != 0) {
                    result[1] = sprite.getPoints();
                    pieces.remove(sprite);
                    // not clean, but ill take it
                    selectedObject.setX(takeIfPossible(pieces, origin, destination)[0] * checkersCharacterSprite.size);
                    selectedObject.setY(takeIfPossible(pieces, origin, destination)[1] * checkersCharacterSprite.size);
                    selectedObject.setMoved();
                    result[0] = 1;

                    String value = origin[0] + "," + origin[1] + "/" + (takeIfPossible(pieces, origin, destination)[0]) + "," + (takeIfPossible(pieces, origin, destination)[1]);
                    DatabaseReference dataRef = database.getReference("chess/" + sessionName + "/chessMove");
                    dataRef.setValue(value);

                    return result;
                }
                break;
            }
            selectedObject.setX(destination[0] * checkersCharacterSprite.size);
            selectedObject.setY(destination[1] * checkersCharacterSprite.size);
            selectedObject.setMoved();
            result[0] = 1;

            int[] position = new int[]{selectedObject.getX(), selectedObject.getY()};
            String value = position[0] + "," + position[1] + "/" + (destination[0] * CharacterSprite.size) + "," + (destination[1] * CharacterSprite.size);
            DatabaseReference dataRef = database.getReference("chess/" + sessionName + "/chessMove");
            dataRef.setValue(value);

            return result;
        }

        selectedObject.setX(destination[0] * checkersCharacterSprite.size);
        selectedObject.setY(destination[1] * checkersCharacterSprite.size);
        selectedObject.setMoved();
        result[0] = 1;

        int[] position = new int[]{selectedObject.getX(), selectedObject.getY()};
        String value = position[0] + "," + position[1] + "/" + (destination[0] * CharacterSprite.size) + "," + (destination[1] * CharacterSprite.size);
        DatabaseReference dataRef = database.getReference("checkers/" + sessionName + "/checkersMove");
        dataRef.setValue(value);

        return result;
    }
    // check if can tak, i.e is there space behind enemy piece in a diagonal way
    private static int[] takeIfPossible(ArrayList<checkersCharacterSprite> pieces, int[] origin, int[] destination) {
        int deltaX = origin[0] - destination[0];
        int deltaY = origin[1] - destination[1];

        for(checkersCharacterSprite checkers : pieces) {
            if(checkers.getX() != destination[0] + deltaX) {
                if(checkers.getY() != destination[1] + deltaY) {
                    return new int[]{destination[0] + deltaX, destination[1] + deltaY};
                }
                // no room behind piece (X-axis)
                return new int[]{0,0};
            }
            // no room behind piece (Y-axis)
            return new int[]{0,0};
        }
        // this should not ever be called but ok Android Studio
        return new int[]{0,0};
    }

    private static boolean anyObstacles(ArrayList<checkersCharacterSprite> pieces, int[] position, int[] destination, moveMode mode, boolean inclusive) {
        switch (mode) {
            case REVERSE_DIAGONAL:
            case DIAGONAL:
                int i = destination[0], j = destination[1];
                i += i > position[0] ? -1 : 1;
                j += j > position[1] ? -1 : 1;

                while (i != position[0]) {
                    for (checkersCharacterSprite sprite : pieces) {
                        if (sprite.isColliding(i * checkersCharacterSprite.size, j * checkersCharacterSprite.size)) {
                            return true;
                        }
                    }

                    i += i > position[0] ? -1 : 1;
                    j += j > position[1] ? -1 : 1;
                }
                break;

            default:
                return true;
        }

        if (inclusive) {
            for (checkersCharacterSprite sprite : pieces) {
                if (sprite.isColliding(destination[0] * checkersCharacterSprite.size, destination[1] * checkersCharacterSprite.size)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int abs(int i) {
        return i >= 0 ? i : -i;
    }

    private static void updateText(boolean blackTurn, int[] points, Canvas canvas, Paint textPaint, boolean gameOn) {
        if (!gameOn) {
            canvas.drawText((blackTurn ? "Red" : "Black") + " Wins!",
                    3 * CharacterSprite.size, 13 * CharacterSprite.size, textPaint);
            return;
        }

        canvas.drawText("Move: " + (blackTurn ? "Black" : "Red"), 3 * CharacterSprite.size,
                10 * CharacterSprite.size, textPaint);
        canvas.drawText("Points:", CharacterSprite.size, 11 * CharacterSprite.size, textPaint);
        canvas.drawText("Black: " + points[0], CharacterSprite.size, 12 * CharacterSprite.size,
                textPaint);
        canvas.drawText("Red: " + points[1], CharacterSprite.size, 13 * CharacterSprite.size,
                textPaint);
    }

    public void movePieceFromDB(int[] position, int[] destination) {
        checkersCharacterSprite pieceToMove;
        int pointsToAdd;
        for (checkersCharacterSprite sprite : pieces) {
            if ((sprite.getX() * CharacterSprite.size) == position[0]) {
                if ((sprite.getY() * CharacterSprite.size) == position[1]) {
                    pieceToMove = sprite;
                    int deltaX = (position[0] + destination[0])/2; // smol explanation - if were taking, its always a + a + 2 -> origin[3,6], destination[1,4], a = 1,4, middle square = a+1, if its a + a + 1 this will fail ( no pieces in a + 0.5 ever)
                    int deltaY = (position[1] + destination[1])/2; // ^ same here ^
                    for (checkersCharacterSprite sprite1 : pieces) {
                        if ((sprite1.getX()) == deltaX) {
                            if ((sprite1.getY()) == deltaY) { // if there is a sprite in the middle
                                pointsToAdd = sprite1.getPoints();
                                pieces.remove(sprite1);
                                // move piece to new position
                                pieceToMove.setX(destination[0]);
                                pieceToMove.setY(destination[1]);
                                pieceToMove.setMoved();

                                if(pieceToMove.isBlack()) {
                                    points[0] += pointsToAdd;
                                    blackTurn = false;
                                }
                                // its black
                                else {
                                    points[1] += pointsToAdd;
                                    blackTurn = true;
                                }
                            }
                            // no piece found to capture, we move 1
                            else {
                                pieceToMove.setX(destination[0]);
                                pieceToMove.setY(destination[1]);
                                if(pieceToMove.isBlack()) {
                                    blackTurn = false;
                                }
                                else {
                                    blackTurn = true;
                                }
                            }
                        }
                        // no piece found to capture, we move 1
                        else {
                            // move piece to new position
                            pieceToMove.setX(destination[0]);
                            pieceToMove.setY(destination[1]);
                            if(pieceToMove.isBlack()) {
                                blackTurn = false;
                            }
                            else {
                                blackTurn = true;
                            }
                        }
                    }
                }
            }
        }
    }
}

