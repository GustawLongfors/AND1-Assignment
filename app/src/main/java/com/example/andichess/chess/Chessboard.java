package com.example.andichess.chess;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

import com.example.andichess.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

enum moveMode
{
    DIAGONAL,
    X_AXIS,
    Y_AXIS
}

public class Chessboard extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private static String sessionName;
    private static FirebaseDatabase database;
    private final chessMainThread thread; // thread responsible for updating our screen
    private final ArrayList<CharacterSprite> board; // fields on a chessboard
    private final ArrayList<CharacterSprite> pieces;
    private final ArrayList<CharacterSprite> selected; // yellow tiles visible if field is selected
    private CharacterSprite selectedObject; // currently selected piece
    public boolean whiteTurn; // if true it's white player's turn
    private final int[] points; // [0] = score white [1] = score black
    public boolean gameOn; // if true chessboard responds to clicks
    private final Paint textPaint; // text display style (size and colour)


    public Chessboard(Context context, String sn) {
        super(context);
        setOnTouchListener(this);
        getHolder().addCallback(this);
        board = new ArrayList<>();
        pieces = new ArrayList<>();
        selected = new ArrayList<>();
        points = new int[]{0, 0};
        thread = new chessMainThread(getHolder(), this);
        setFocusable(true);
        whiteTurn = true;
        gameOn = true;
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);

        sessionName = sn;

        database = FirebaseDatabase.getInstance();
        DatabaseReference chessRef = database.getReference("chess/" + sessionName + "/chessMove");
        chessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // format - xOrigin,yOrigin/xDest,yDest
                String value = snapshot.getValue(String.class);
                if(!(value == null)){
                    String[] values = value.split("/"); // 5,4/3,2/true -> [5,4][3,2][true]
                    String[] originVal = values[0].split(",");
                    String[] destVal = values[1].split(",");
                    int[] origin = new int[]{Integer.parseInt(originVal[0]), Integer.parseInt(originVal[1])};
                    int[] destination = new int[]{Integer.parseInt(destVal[0]), Integer.parseInt(destVal[1])};
                    movePieceFromDB(origin, destination);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //something something error
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;


        // create board
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
                    board.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.field_green,
                            o), 110 * x, 110 * y, objType.FIELD_WHITE));
                } else {
                    board.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.field_purple,
                            o), 110 * x, 110 * y, objType.FIELD_BLACK));
                }
            }
        }

        // create pieces
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.rook_white, o),
                110, 110 * 8, objType.ROOK_WHITE));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.rook_white, o),
                110 * 8, 110 * 8, objType.ROOK_WHITE));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.knight_white, o),
                110 * 2, 110 * 8, objType.KNIGHT_WHITE));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.knight_white, o),
                110 * 7, 110 * 8, objType.KNIGHT_WHITE));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.bishop_white, o),
                110 * 3, 110 * 8, objType.BISHOP_WHITE));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.bishop_white, o),
                110 * 6, 110 * 8, objType.BISHOP_WHITE));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.king_white, o),
                110 * 5, 110 * 8, objType.KING_WHITE));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.queen_white, o),
                110 * 4, 110 * 8, objType.QUEEN_WHITE));

        for (int x = 1; x <= 8; x++) {
            pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_white, o),
                    110 * x, 110 * 7, objType.PAWN_WHITE));
        }

        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.rook_black, o),
                110, 110, objType.ROOK_BLACK));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.rook_black, o),
                110 * 8, 110, objType.ROOK_BLACK));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.knight_black, o),
                110 * 2, 110, objType.KNIGHT_BLACK));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.knight_black, o),
                110 * 7, 110, objType.KNIGHT_BLACK));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.bishop_black, o),
                110 * 3, 110, objType.BISHOP_BLACK));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.bishop_black, o),
                110 * 6, 110, objType.BISHOP_BLACK));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.king_black, o),
                110 * 5, 110, objType.KING_BLACK));
        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.queen_black, o),
                110 * 4, 110, objType.QUEEN_BLACK));

        for (int x = 1; x <= 8; x++) {
            pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.pawn_black, o),
                    110 * x, 110 * 2, objType.PAWN_BLACK));
        }

        // create and hide selected tiles
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                selected.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(), R.drawable.field_selected,
                        o), 110 * x, 110 * y, objType.FIELD_SELECTED));
                selected.get(selected.size() - 1).setVisible(false);
            }
        }

        thread.setRunning(true);
        thread.start();
    }

    public void sendMoveToDB(int[] position, int[] destination) {
        String value = position[0] + "," + position[1] + "/" + destination[0] + "," + destination[1] + "/" + String.valueOf(whiteTurn);
        DatabaseReference dataRef = database.getReference("chess/" + sessionName + "/chessMove");
        dataRef.setValue(value);
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
            for (CharacterSprite sprite : board) {
                sprite.draw(canvas);
            }
            for (CharacterSprite sprite : selected) {
                sprite.draw(canvas);
            }
            for (CharacterSprite sprite : pieces) {
                sprite.draw(canvas);
            }
            updateText(whiteTurn, points, canvas, textPaint, gameOn);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // if the game is over, there is no point in doing anything!
        if (!gameOn) return false;

        if (selectedObject != null) {
            // We clicked while having a piece already chosen, check if we can move here
            CharacterSprite newSelectedObject = null;
            for (CharacterSprite sprite : pieces) {
                if (sprite.isColliding((int) event.getX(), (int) event.getY())) {
                    // Newly selected object is another piece
                    newSelectedObject = sprite;
                    break;
                }
            }
            if (newSelectedObject == null) {
                for (CharacterSprite sprite : board) {
                    if (sprite.isColliding((int) event.getX(), (int) event.getY())) {
                        // Newly selected object is a field
                        newSelectedObject = sprite;
                        break;
                    }
                }
            }
            if (newSelectedObject == null) {
                for (CharacterSprite field : selected) {
                    if (field.isColliding(selectedObject.getX(), selectedObject.getY())) {
                        // User touched outside the board, deselecting previous field and quitting
                        field.setVisible(false);
                        selectedObject = null;
                        break;
                    }
                }
                return false;
            }

            // position && destination == [x,y] coordinates list in range <1-8>
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

            // pieces logic
            switch (selectedObject.getType()) {
                case PAWN_BLACK: {
                    // move forward
                    if (destination[0] == position[0] && destination[1] == position[1] + 1) {
                        if (!anyObstacles(pieces, position, destination, moveMode.Y_AXIS, true)) {
                            selectedObject.setY(selectedObject.getY() + CharacterSprite.size);
                            sendMoveToDB(position, destination);

                            // user can move black pawn one field
                            moved = true;
                            selectedObject.setMoved();
                        }
                    }

                    // move forward two spaces
                    else if (!selectedObject.didMove() && destination[0] == position[0]
                            && destination[1] == position[1] + 2) {
                        if (!anyObstacles(pieces, position, destination, moveMode.Y_AXIS, true)) {
                            selectedObject.setY(selectedObject.getY() + CharacterSprite.size * 2);
                            sendMoveToDB(position, destination);
                            // user can move black pawn two fields
                            moved = true;
                            selectedObject.setMoved();
                        }
                    }

                    // capture
                    else if ((destination[0] == position[0] - 1 && destination[1] == position[1] + 1)
                            || (destination[0] == position[0] + 1 && destination[1] == position[1] + 1)) {
                        int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                        moved = result[0] == 1;
                        points[whiteTurn ? 0 : 1] += result[1];
                        sendMoveToDB(position, destination);
                    }

                    // promote to queen if reached the end of the board
                    if (selectedObject.getY() == CharacterSprite.size * 8) {
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inScaled = false;
                        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(),
                                R.drawable.queen_black, o), selectedObject.getX(), 880, objType.QUEEN_BLACK, 1));
                        pieces.remove(selectedObject);
                    }
                    break;
                }

                case PAWN_WHITE: {
                    // move forward
                    if (destination[0] == position[0] && destination[1] == position[1] - 1) {
                        if (!anyObstacles(pieces, position, destination, moveMode.Y_AXIS, true)) {
                            selectedObject.setY(selectedObject.getY() - CharacterSprite.size);
                            //int[] origin = new int[]{selectedObject.getX(), selectedObject.getY()};
                            //int[] destinationDB= new int[]{selectedObject.getX() / CharacterSprite.size, selectedObject.getY() / CharacterSprite.size};
                            sendMoveToDB(position, destination);

                            // user can move white pawn one field
                            moved = true;
                            selectedObject.setMoved();
                        }
                    }

                    // move forward two spaces
                    else if (!selectedObject.didMove() && destination[0] == position[0]
                            && destination[1] == position[1] - 2) {
                        if (!anyObstacles(pieces, position, destination, moveMode.Y_AXIS, true)) {
                            selectedObject.setY(selectedObject.getY() - CharacterSprite.size * 2);
                            sendMoveToDB(position, destination);
                            // user can move white pawn two fields
                            moved = true;
                            selectedObject.setMoved();
                        }
                    }

                    // capture
                    else if ((destination[0] == position[0] - 1 && destination[1] == position[1] - 1)
                            || (destination[0] == position[0] + 1 && destination[1] == position[1] - 1)) {
                        int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                        moved = result[0] == 1;
                        points[whiteTurn ? 0 : 1] += result[1];
                        sendMoveToDB(position, destination);
                    }

                    // promote to queen if reached the end of the board
                    if (selectedObject.getY() == CharacterSprite.size) {
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inScaled = false;
                        pieces.add(new CharacterSprite(BitmapFactory.decodeResource(getResources(),
                                R.drawable.queen_white, o), selectedObject.getX(), 110, objType.QUEEN_WHITE, 1));
                        pieces.remove(selectedObject);
                    }
                    break;
                }

                case ROOK_WHITE:
                case ROOK_BLACK: {
                    // move on Y axis (up/down)
                    if (destination[0] == position[0] && destination[1] != position[1]) {
                        if (!anyObstacles(pieces, position, destination, moveMode.Y_AXIS, false)) {
                            int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                            moved = result[0] == 1;
                            points[whiteTurn ? 0 : 1] += result[1];
                            sendMoveToDB(position, destination);
                        }

                    }
                    // move on X axis (left/right)
                    else if (destination[1] == position[1] && destination[0] != position[0]) {
                        if (!anyObstacles(pieces, position, destination, moveMode.X_AXIS, false)) {
                            int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                            moved = result[0] == 1;
                            points[whiteTurn ? 0 : 1] += result[1];
                            sendMoveToDB(position, destination);
                        }
                    }
                    break;
                }

                case KNIGHT_BLACK:
                case KNIGHT_WHITE: {
                    if ((destination[0] == position[0] - 2 && destination[1] == position[1] - 1)
                            || (destination[0] == position[0] - 2 && destination[1] == position[1] + 1)
                            || (destination[0] == position[0] - 1 && destination[1] == position[1] + 2)
                            || (destination[0] == position[0] - 1 && destination[1] == position[1] - 2)
                            || (destination[0] == position[0] + 1 && destination[1] == position[1] - 2)
                            || (destination[0] == position[0] + 1 && destination[1] == position[1] + 2)
                            || (destination[0] == position[0] + 2 && destination[1] == position[1] - 1)
                            || (destination[0] == position[0] + 2 && destination[1] == position[1] + 1)) {
                        int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                        moved = result[0] == 1;
                        points[whiteTurn ? 0 : 1] += result[1];
                        sendMoveToDB(position, destination);
                    }
                    break;
                }

                case BISHOP_BLACK:
                case BISHOP_WHITE: {
                    if (abs(destination[0] - position[0]) == abs(destination[1] - position[1])) {
                        if (!anyObstacles(pieces, position, destination, moveMode.DIAGONAL, false)) {
                            int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                            moved = result[0] == 1;
                            points[whiteTurn ? 0 : 1] += result[1];
                            sendMoveToDB(position, destination);
                        }
                    }
                    break;
                }

                case KING_BLACK:
                case KING_WHITE: {
                    // castle
                    if (!selectedObject.didMove() && (destination[0] == position[0] - 4 && destination[1] == position[1])
                            || (destination[0] == position[0] + 3 && destination[1] == position[1])) {
                        int o;
                        // o-o-o
                        if (destination[0] == position[0] - 4) {
                            o = -1;
                        }
                        // o-o
                        else {
                            o = 1;
                        }

                        for (int i = position[0] + o; i > destination[0]; i += o) {
                            for (CharacterSprite sprite : pieces) {
                                if (sprite.isColliding(i * CharacterSprite.size, destination[1] * CharacterSprite.size)) {
                                    isSomethingInTheWay = true;
                                    break;
                                }
                            }
                            if (isSomethingInTheWay) {
                                break;
                            }
                        }

                        if (!isSomethingInTheWay) {
                            for (CharacterSprite sprite : pieces) {
                                if (sprite.isColliding(destination[0] * CharacterSprite.size,
                                        destination[1] * CharacterSprite.size)) {
                                    if (whiteTurn) {
                                        if (sprite.getType() != objType.ROOK_WHITE || sprite.didMove()) {
                                            break;
                                        }
                                    } else {
                                        if (sprite.getType() != objType.ROOK_BLACK || sprite.didMove()) {
                                            break;
                                        }
                                    }
                                    selectedObject.setMoved();
                                    moved = true;
                                    sprite.setMoved();
                                    if (o == -1) {
                                        selectedObject.setX(3 * CharacterSprite.size);
                                        sprite.setX(4 * CharacterSprite.size);
                                    } else {
                                        selectedObject.setX(7 * CharacterSprite.size);
                                        sprite.setX(6 * CharacterSprite.size);
                                    }

                                }
                            }
                        }
                    } else if (abs(destination[0] - position[0]) == 1 || abs(destination[1] - position[1]) == 1) {
                        int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                        moved = result[0] == 1;
                        points[whiteTurn ? 0 : 1] += result[1];
                        sendMoveToDB(position, destination);
                    }
                    break;
                }

                case QUEEN_BLACK:
                case QUEEN_WHITE: {
                    // move on Y axis
                    if (destination[0] == position[0] && destination[1] != position[1]) {
                        isSomethingInTheWay = anyObstacles(pieces, position, destination, moveMode.Y_AXIS,
                                false);
                    }
                    // move on X axis
                    else if (destination[0] != position[0] && destination[1] == position[1]) {
                        isSomethingInTheWay = anyObstacles(pieces, position, destination, moveMode.X_AXIS,
                                false);
                    }
                    // move diagonally
                    else if (abs(destination[0] - position[0]) == abs(destination[1] - position[1])) {
                        isSomethingInTheWay = anyObstacles(pieces, position, destination, moveMode.DIAGONAL,
                                false);
                    }

                    if (!isSomethingInTheWay) {
                        int[] result = moveIfPossible(pieces, selectedObject, whiteTurn, destination);
                        moved = result[0] == 1;
                        points[whiteTurn ? 0 : 1] += result[1];
                        sendMoveToDB(position, destination);
                    }
                    break;
                }
            }

            // Uncheck previous field
            for (CharacterSprite field : selected) {
                field.setVisible(false);
            }
            // Switch players if the correct move was performed
            if (moved) {
                whiteTurn = !whiteTurn;
                // end game if mate
                if (isMate(pieces, whiteTurn)) {
                    gameOn = false;
                }
            }
            // Deselect object
            selectedObject = null;
        } else {
            // We clicked and we DO NOT have a piece already chosen
            for (CharacterSprite sprite : pieces) {
                if (sprite.isWhite() == whiteTurn && sprite.isColliding((int) event.getX(), (int) event.getY())) {
                    selectedObject = sprite;
                    for (CharacterSprite field : selected) {
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

    private int abs(int i) {
        return i >= 0 ? i : -i;
    }


    private static boolean isMate(ArrayList<CharacterSprite> pieces, boolean whiteTurn) {
        for (CharacterSprite sprite : pieces) {
            if (whiteTurn && sprite.getType() == objType.KING_WHITE) {
                return false;
            } else if (!whiteTurn && sprite.getType() == objType.KING_BLACK) {
                return false;
            }
        }
        return true;
    }

    // checks if any pieces are on the route, if inclusive is true it also checks for the destination field
    private static boolean anyObstacles(ArrayList<CharacterSprite> pieces, int[] position, int[] destination,
                                        moveMode mode, boolean inclusive) {
        switch (mode) {
            case X_AXIS:
                int h = destination[0] < position[0] ? -1 : 1;
                for (int i = position[0] + h; i != destination[0]; i += h) {
                    for (CharacterSprite sprite : pieces) {
                        if (sprite.isColliding(i * CharacterSprite.size, position[1] * CharacterSprite.size)) {
                            return true;
                        }
                    }
                }
                break;

            case Y_AXIS:
                h = destination[1] < position[1] ? -1 : 1;
                for (int i = position[1] + h; i != destination[1]; i += h) {
                    for (CharacterSprite sprite : pieces) {
                        if (sprite.isColliding(position[0] * CharacterSprite.size, i * CharacterSprite.size)) {
                            return true;
                        }
                    }
                }
                break;

            case DIAGONAL:
                int i = destination[0], j = destination[1];
                i += i > position[0] ? -1 : 1;
                j += j > position[1] ? -1 : 1;

                while (i != position[0]) {
                    for (CharacterSprite sprite : pieces) {
                        if (sprite.isColliding(i * CharacterSprite.size, j * CharacterSprite.size)) {
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
            for (CharacterSprite sprite : pieces) {
                if (sprite.isColliding(destination[0] * CharacterSprite.size, destination[1] * CharacterSprite.size)) {
                    return true;
                }
            }
        }

        return false;
    }

    // moves piece if possible, if impossible returns [0, 0], else returns [1, points_taken]
    private static int[] moveIfPossible(ArrayList<CharacterSprite> pieces, CharacterSprite selectedObject,
                                        boolean whiteTurn, int[] destination) {
        int[] result = new int[]{0, 0};
        for (CharacterSprite sprite : pieces) {
            if (sprite.isColliding(destination[0] * CharacterSprite.size, destination[1] * CharacterSprite.size)) {
                if (whiteTurn) {
                    if (sprite.isWhite()) {
                        return result;
                    }
                } else {
                    if (!sprite.isWhite()) {
                        return result;
                    }
                }
                result[1] = sprite.getPoints();
                pieces.remove(sprite);
                break;
            }
        }

        selectedObject.setX(destination[0] * CharacterSprite.size);
        selectedObject.setY(destination[1] * CharacterSprite.size);
        selectedObject.setMoved();
        result[0] = 1;
        int[] position = new int[]{selectedObject.getX(), selectedObject.getY()};
        String value = position[0] + "," + position[1] + "/" + (destination[0] * CharacterSprite.size) + "," + (destination[1] * CharacterSprite.size);
        DatabaseReference dataRef = database.getReference("chess/" + sessionName + "/chessMove");
        dataRef.setValue(value);
        return result;
    }

    private static void updateText(boolean whiteTurn, int[] points, Canvas canvas, Paint textPaint, boolean gameOn) {
        if (!gameOn) {
            canvas.drawText((whiteTurn ? "Black" : "White") + " Wins!",
                    3 * CharacterSprite.size, 13 * CharacterSprite.size, textPaint);
            return;
        }

        canvas.drawText("Move: " + (whiteTurn ? "White" : "Black"), 3 * CharacterSprite.size,
                10 * CharacterSprite.size, textPaint);
        canvas.drawText("Points:", CharacterSprite.size, 11 * CharacterSprite.size, textPaint);
        canvas.drawText("White: " + points[0], CharacterSprite.size, 12 * CharacterSprite.size,
                textPaint);
        canvas.drawText("Black: " + points[1], CharacterSprite.size, 13 * CharacterSprite.size,
                textPaint);
    }

    // int[] position = {selectedObject.getX() / CharacterSprite.size, selectedObject.getY() / CharacterSprite.size};
    // int[] destination = {newSelectedObject.getX() / CharacterSprite.size, newSelectedObject.getY() / CharacterSprite.size};
    // Called from database move, will execute move passed - move should always be legal

    public void movePieceFromDB(int[] position, int[] destination) {
        CharacterSprite pieceToMove;
        int pointsToAdd;
        for (CharacterSprite sprite : pieces) {
            if ((sprite.getX()) == position[0]) {
                if ((sprite.getY()) == position[1]) {
                    pieceToMove = sprite;
                        if(pieceToMove.isWhite() && whiteTurn || pieceToMove.isWhite() && !whiteTurn) {
                            for (CharacterSprite sprite1 : pieces) {
                                if ((sprite1.getX()) == destination[0]) {
                                    if ((sprite1.getY()) == destination[1]) {
                                        pointsToAdd = sprite1.getPoints();
                                        pieces.remove(sprite1);
                                        // move piece to new position
                                        pieceToMove.setX(destination[0] * CharacterSprite.size);
                                        pieceToMove.setY(destination[1] * CharacterSprite.size);
                                        pieceToMove.setMoved();

                                        if (pieceToMove.isWhite()) {
                                            points[0] += pointsToAdd;
                                            whiteTurn = false;
                                        }
                                        // its black
                                        else {
                                            points[1] += pointsToAdd;
                                            whiteTurn = true;
                                        }
                                    }
                                    // no piece found to capture
                                    else {
                                        pieceToMove.setX(destination[0]);
                                        pieceToMove.setY(destination[1]);
                                        if (pieceToMove.isWhite()) {
                                            whiteTurn = false;
                                        } else {
                                            whiteTurn = true;
                                        }
                                    }
                                }
                                // no piece found to capture
                                else {
                                    // move piece to new position
                                    pieceToMove.setX(destination[0]);
                                    pieceToMove.setY(destination[1]);
                                    if (pieceToMove.isWhite()) {
                                        whiteTurn = false;
                                    } else {
                                        whiteTurn = true;
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}
