package com.upv.pm_2022.iti_27849_u3_equipo_01;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.LinearLayout;

public class OpenGLActivity extends AppCompatActivity {

    private CustomSurfaceView customSurfaceView;
    private LinearLayout primaryLayout;
    private Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_glactivity);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        primaryLayout = findViewById(R.id.primaryLayout);
        btnChange = new Button(this);
        btnChange.setText(R.string.text_change);
//        btnChange.setOnClickListener();
        btnChange.setBackgroundColor(Color.parseColor("#00BCD4"));

        customSurfaceView = new CustomSurfaceView(this, size.x, size.y);

        primaryLayout.addView(btnChange, 0);
        primaryLayout.addView(customSurfaceView, 1);
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