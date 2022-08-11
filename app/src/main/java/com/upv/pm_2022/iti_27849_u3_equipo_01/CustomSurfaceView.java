package com.upv.pm_2022.iti_27849_u3_equipo_01;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class CustomSurfaceView extends GLSurfaceView {

    boolean zooming = false;
    private int zoom, width, height;
    private GLRender renderer;
    private long timeOfLastZoom;

    public CustomSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);
        zoom = 4;

        renderer = new GLRender(zoom);
        setRenderer(renderer);
        timeOfLastZoom = System.currentTimeMillis();
    }

    public CustomSurfaceView(Context context, int width, int height) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);
        zoom = 4;

        Point size = new Point();
        this.width = width;
        this.height = height;

        renderer = new GLRender(zoom);
        // Set the renderer to our demo renderer, defined below.
        setRenderer(renderer);
        timeOfLastZoom = System.currentTimeMillis();
    }


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        //return super.onTouchEvent(event);
        if (System.currentTimeMillis() - timeOfLastZoom > 250 && !zooming) {
            //onPause();
            if (event.getY() > height / 2 && zoom <= 1 || event.getY() <= height / 2 && zoom >= 9)
                return true;
            queueEvent(() -> {
                zooming = true;
                if (event.getY() > height / 2)
                    zoom--;
                else
                    zoom++;

//                    if (zoom < 1)
//                        zoom = 1;
//                    else if (zoom > 9)
//                        zoom = 9;
                renderer.zoom(zoom);
                timeOfLastZoom = System.currentTimeMillis();
                zooming = false;
            });

            //onResume();
        }
        return true;
    }
}
