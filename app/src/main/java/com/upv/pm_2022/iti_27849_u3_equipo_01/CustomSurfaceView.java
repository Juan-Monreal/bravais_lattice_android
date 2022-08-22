package com.upv.pm_2022.iti_27849_u3_equipo_01;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class CustomSurfaceView extends GLSurfaceView {

    boolean zooming = false;
    private int zoom, width, height;
    public GLRender renderer;
    private long timeOfLastZoom;

    private float sizeCoef = 1;

    public CustomSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);
        zoom = 4;

        renderer = new GLRender(zoom);
        setRenderer(renderer);
        timeOfLastZoom = System.currentTimeMillis();
    }

    public CustomSurfaceView(Context context, int width, int height, int options) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);
        zoom = 1;

        Point size = new Point();
        this.width = width;
        this.height = height;

        renderer = new GLRender(options);
        // Set the renderer to our demo renderer, defined below.
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
        return super.onTouchEvent(event);
    }

    private class ScaleDetectorListener implements ScaleGestureDetector.OnScaleGestureListener{

        float scaleFocusX = 0;
        float scaleFocusY = 0;

        public boolean onScale(ScaleGestureDetector arg0) {
            float scale = arg0.getScaleFactor() * sizeCoef;

            sizeCoef = scale;

            requestRender();

            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector arg0) {
            invalidate();

            scaleFocusX = arg0.getFocusX();
            scaleFocusY = arg0.getFocusY();

            return true;
        }

        public void onScaleEnd(ScaleGestureDetector arg0) {
            scaleFocusX = 0;
            scaleFocusY = 0;
        }
    }
}

