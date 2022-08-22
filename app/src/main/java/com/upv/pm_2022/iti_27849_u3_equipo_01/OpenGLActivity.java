package com.upv.pm_2022.iti_27849_u3_equipo_01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
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

    private float[] vertices;
    private Point size;
    private Context context = this;
    static String selected = "Cubic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_glactivity);
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        primaryLayout = findViewById(R.id.primaryLayout);
        btnChange = new Button(this);
        btnChange.setText(R.string.text_change);
        btnChange.setOnClickListener(this::launchDialog);
        btnChange.setBackgroundColor(Color.parseColor("#00BCD4"));

        customSurfaceView = drawCustomBravais(selected);
        customSurfaceView.invalidate();
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

        EditText etA = dialog.findViewById(R.id.et_a);
        EditText etB = dialog.findViewById(R.id.et_b);
        EditText etC = dialog.findViewById(R.id.et_c);
        EditText etAlpha = dialog.findViewById(R.id.et_alpha);
        EditText etBeta = dialog.findViewById(R.id.et_beta);
        EditText etGamma = dialog.findViewById(R.id.et_gamma);

        cancelButton.setOnClickListener( v -> dialog.dismiss());

        save.setOnClickListener( v -> {
            String a = etA.getText().toString();
            String b = etB.getText().toString();
            String c = etC.getText().toString();
            String alpha = etAlpha.getText().toString();
            String beta = etBeta.getText().toString();
            String gamma = etGamma.getText().toString();
            if (!valuresAreEmpty(a, b, c, alpha, beta, gamma)){
//                drawCustomBravais(selected);
            }else{
                Toast.makeText(this, "Please enter valid information!!", Toast.LENGTH_SHORT).show();
            }
            //TODO:
            dialog.dismiss();
        });

        dialog.show();
    }

    private CustomSurfaceView drawCustomBravais(String selected) {
        System.out.println("SELECTED " + selected);
        switch (selected){
            case "Cubic P":
                return  new CustomSurfaceView(this, size.x, size.y, 1);
            case "Cubic I":
                return  new CustomSurfaceView(this, size.x, size.y, 11);

            case "Cubic F":
                return  new CustomSurfaceView(this, size.x, size.y, 12);

            case "Tetragonal P":
                return  new CustomSurfaceView(this, size.x, size.y, 6);

            case "Tetragonal I":
                return  new CustomSurfaceView(this, size.x, size.y, 62);

            case "Ortorrombica P":
                return  new CustomSurfaceView(this, size.x, size.y, 4);

            case "Ortorrombica I":
                return  new CustomSurfaceView(this, size.x, size.y, 42);

            case "Ortorrombica C":
                return  new CustomSurfaceView(this, size.x, size.y, 41);

            case "Ortorrombica F":
                return  new CustomSurfaceView(this, size.x, size.y, 43);

            case "Hexagonal P":
                return  new CustomSurfaceView(this, size.x, size.y, 3);

            case "Trigonal P":
                return  new CustomSurfaceView(this, size.x, size.y, 5);

            case "Monoclinica P":
                return  new CustomSurfaceView(this, size.x, size.y, 2);

            case "Monoclinica C":
                return  new CustomSurfaceView(this, size.x, size.y, 22);

            case "Triclinica":
                return  new CustomSurfaceView(this, size.x, size.y, 7);
            default:
                return  new CustomSurfaceView(this, size.x, size.y, 1);
//                this.customSurfaceView.invalidate();
//                primaryLayout.recomputeViewAttributes(this.customSurfaceView);
        }
    }

    /**
     * Utility function to check if any of the required parameters it's empty!
     * Clean Code Check!
     * @param a
     * @param b
     * @param c
     * @param alpha
     * @param beta
     * @param gamma
     * @return boolean: True if any of the values is empty!
     */
    private boolean valuresAreEmpty(String a, String b, String c, String alpha, String beta, String gamma) {
        return (a.isEmpty() || b.isEmpty() || c.isEmpty() || alpha.isEmpty() || beta.isEmpty() || gamma.isEmpty());
    }

}