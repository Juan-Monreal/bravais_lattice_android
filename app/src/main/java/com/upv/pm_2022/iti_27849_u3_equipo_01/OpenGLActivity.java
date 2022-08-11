package com.upv.pm_2022.iti_27849_u3_equipo_01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        btnChange.setOnClickListener(this::launchDialog);
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

    public void launchDialog(View view){
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.custom_dialog_input);
        dialog.setTitle("Update Length");

        Button cancelButton = dialog.findViewById(R.id.customCancel);
        Button save = dialog.findViewById(R.id.customOk);

        EditText etA = findViewById(R.id.et_a);
        EditText etB = findViewById(R.id.et_b);
        EditText etC = findViewById(R.id.et_c);
        EditText etAlpha = findViewById(R.id.et_alpha);
        EditText etBeta = findViewById(R.id.et_beta);
        EditText etGamma = findViewById(R.id.et_gamma);

        cancelButton.setOnClickListener( v -> dialog.dismiss());

        save.setOnClickListener( v -> {
            String a = etA.getText().toString();
            String b = etB.getText().toString();
            String c = etC.getText().toString();
            String alpha = etAlpha.getText().toString();
            String beta = etBeta.getText().toString();
            String gamma = etA.getText().toString();
            if (!valuresAreEmpty(a, b, c, alpha, beta, gamma)){
                
            }else{
                Toast.makeText(this, "Please enter valid information!!", Toast.LENGTH_SHORT).show();
            }
            //TODO:
            dialog.dismiss();
        });

        dialog.show();
    }

    private boolean valuresAreEmpty(String a, String b, String c, String alpha, String beta, String gamma) {
        return (a.isEmpty() || b.isEmpty() || c.isEmpty() || alpha.isEmpty() || beta.isEmpty() || gamma.isEmpty());
    }

}