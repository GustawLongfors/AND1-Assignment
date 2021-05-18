package com.example.andichess.checkers;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.example.andichess.chess.Chessboard;

public class checkersMainThread extends Thread {

    private final SurfaceHolder surfaceHolder;
    private final Checkerboard checkerboard;
    private boolean running;
    public static Canvas canvas;

    public checkersMainThread(SurfaceHolder sh, Checkerboard cb)
    {
        super();
        surfaceHolder = sh;
        checkerboard = cb;
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
                    this.checkerboard.draw(canvas);
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
