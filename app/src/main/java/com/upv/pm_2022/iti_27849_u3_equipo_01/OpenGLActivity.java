package com.upv.pm_2022.iti_27849_u3_equipo_01;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class OpenGLActivity extends AppCompatActivity {

    private CustomSurfaceView customSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_open_glactivity);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        customSurfaceView = new CustomSurfaceView(this, size.x, size.y);
        setContentView(customSurfaceView);
    }

    @Override
    protected void onResume() {
        customSurfaceView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        customSurfaceView.onPause();
        super.onPause();
    }

}