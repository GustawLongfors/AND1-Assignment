package com.example.andichess.chessSP;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread
{
    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder sh, GameView gv)
    {
        super();
        surfaceHolder = sh;
        gameView = gv;
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
                    this.gameView.draw(canvas);
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
