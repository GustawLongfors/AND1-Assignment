package com.example.andichess.chess;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class CharacterSprite
{
    private final Bitmap image;
    private int x;
    private int y;
    private final objType type;
    public static final int size = 110;
    private boolean moved;
    private boolean visible;
    private final int points;

    public CharacterSprite(Bitmap bmp, int x, int y, objType type)
    {
        image = bmp;
        this.x = x;
        this.y = y;
        this.type = type;
        moved = false;
        visible = true;
        switch (type)
        {
            case PAWN_BLACK:
            case PAWN_WHITE:
                points = 1;
                break;
            case KNIGHT_BLACK:
            case KNIGHT_WHITE:
            case BISHOP_WHITE:
            case BISHOP_BLACK:
                points = 3;
                break;
            case ROOK_BLACK:
            case ROOK_WHITE:
                points = 5;
                break;
            case QUEEN_BLACK:
            case QUEEN_WHITE:
                points = 9;
                break;
            default:
                points = 0;
                break;
        }
    }

    public CharacterSprite(Bitmap bmp, int x, int y, objType type, int points)
    {
        image = bmp;
        this.x = x;
        this.y = y;
        this.type = type;
        moved = false;
        visible = true;
        this.points = points;
    }

    public void draw(Canvas canvas)
    {
        if (visible)
        {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public boolean isColliding(int x, int y)
    {
        return x >= this.x && x < this.x + size && y >= this.y && y < this.y + size;
    }

    public boolean isWhite()
    {
        return type == objType.PAWN_WHITE || type == objType.QUEEN_WHITE || type == objType.BISHOP_WHITE
                || type == objType.KING_WHITE || type == objType.KNIGHT_WHITE || type == objType.ROOK_WHITE;
    }

    public objType getType() { return type; }

    public void setVisible(boolean visible) { this.visible = visible; }

    public void setMoved() { moved = true; }

    public boolean didMove() { return moved; }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getPoints() { return points; }
}