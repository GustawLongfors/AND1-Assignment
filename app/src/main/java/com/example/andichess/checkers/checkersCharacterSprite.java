package com.example.andichess.checkers;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.andichess.chess.CharacterSprite;
import com.example.andichess.chess.objType;

public class checkersCharacterSprite {

    private final Bitmap image;
    private int x;
    private int y;
    private final objTypeCheckers type;
    public static final int size = 110;
    private boolean moved;
    private boolean visible;
    private int points;

    public checkersCharacterSprite(Bitmap bmp, int x, int y, objTypeCheckers type) {
        image = bmp;
        this.x = x;
        this.y = y;
        this.type = type;
        moved = false;
        visible = true;
        switch (type) {
            case MAN_BLACK:
            case MAN_RED:
                points = 1;
                break;
            case KING_BLACK:
            case KING_RED:
                points = 3;
                break;
        }
    }

    public checkersCharacterSprite(Bitmap bmp, int x, int y, objTypeCheckers type, int points) {
        image = bmp;
        this.x = x;
        this.y = y;
        this.type = type;
        moved = false;
        visible = true;
        this.points = points;
    }

    public void draw(Canvas canvas) {
        if (visible) {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public boolean isColliding(int x, int y) {
        return x >= this.x && x < this.x + size && y >= this.y && y < this.y + size;
    }

    public boolean isBlack() {
        return type == objTypeCheckers.MAN_BLACK || type == objTypeCheckers.KING_BLACK;
    }

    public objTypeCheckers getType() {return type; }

    public void setVisible(boolean visible) { this.visible = visible; }

    public void setMoved() { moved = true; }

    public boolean didMove() { return moved; }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getPoints() { return points; }
}
