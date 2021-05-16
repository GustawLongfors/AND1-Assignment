package com.example.andichess.chess;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class chessMainThread extends Thread
{
    private final SurfaceHolder surfaceHolder;
    private final Chessboard chessboard;
    private boolean running;
    public static Canvas canvas;

    public chessMainThread(SurfaceHolder sh, Chessboard cb)
    {
        super();
        surfaceHolder = sh;
        chessboard = cb;
    }

    public void setRunning(boolean set) { running = set; }

    @Override
    public void run()
    {
        while (running)
        {
            canvas = null;
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    this.chessboard.draw(canvas);
                }
            }
            catch (Exception ignored){}

            finally
            {
                if (canvas != null)
                {
                    try
                    {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}