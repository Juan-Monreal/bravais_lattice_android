package com.upv.pm_2022.iti_27849_u3_equipo_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //TODO: Do the same for the others systems
    static String[] systems;
    static String[] cubic, tetragonal, ortorrombica, hexagonal, trigonal, monoclinica, triclinica;

    static {
        systems = new String[]{"Cubic", "Tetragonal", "ortorrombica", "hexagonal", "trigonal", "monoclinica", "triclinica"};
        cubic = new String[] {"Cubic P", "Cubic I", "Cubic F"};
        tetragonal = new String[] {"Tetragonal P", "Tetragonal I"};
        ortorrombica = new String[] {"Ortorrombica P", "Ortorrombica I,", "Ortorrombica C", "Ortorrombica F"};
        hexagonal = new String[] {"Hexagonal P"};
        trigonal = new String[] {"Trigonal P"};
        monoclinica = new String[] {"Monoclinica P","Monoclinica C"};
        triclinica = new String[] {"Triclinica"};
    }

    private Toolbar toolbar;
    private Spinner spSystem, spSubsystem;
    private TextView tvProperties;
    private EditText etA, etB, etC, etAlpha, etBeta, etGamma;
    private Button btnDraw;

    private ArrayAdapter<String> systemAdapter, subsystemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        spSystem = findViewById(R.id.sp_system);
        spSubsystem = findViewById(R.id.sp_subsystem);
        tvProperties = findViewById(R.id.tv_properties);
        etA = findViewById(R.id.et_a);
        etB = findViewById(R.id.et_b);
        etC = findViewById(R.id.et_c);
        etAlpha = findViewById(R.id.et_alpha);
        etBeta = findViewById(R.id.et_beta);
        etGamma = findViewById(R.id.et_gamma);
        btnDraw = findViewById(R.id.btn_draw);

        systemAdapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                new ArrayList<>(Arrays.asList(MainActivity.systems))
        );
        subsystemAdapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                new ArrayList<>(Arrays.asList(MainActivity.cubic))
        );

        spSystem.setAdapter(systemAdapter);
        spSubsystem.setAdapter(subsystemAdapter);

        spSystem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //TODO: Update the case according the names in the system String []
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        subsystemAdapter = new ArrayAdapter<>(
                                parent.getContext(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                new ArrayList<>(Arrays.asList(MainActivity.cubic))
                        );
                        break;
                    case 1:
                        subsystemAdapter = new ArrayAdapter<>(
                                parent.getContext(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                new ArrayList<>(Arrays.asList(MainActivity.tetragonal))
                        );
                        break;
                }
                spSubsystem.setAdapter(subsystemAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO:
            }
        });

        spSubsystem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvProperties.setText(showProperties(parent.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDraw.setOnClickListener(this::draw);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void draw(View view){
        //TODO: Launch OpenGL View
        Intent intent = new Intent(this, OpenGLActivity.class);
        startActivity(intent);
    }

    private String showProperties(String name){
        switch (name){
            case "Cubic P":
                tvProperties.setText("a = b ≠ c, a = b = y = 90°u");
                break;
            case "Cubic I":
                tvProperties.setText("a = b ≠ c,  a = b = y = 90°u");
                break;
            case "Cubic F":
                tvProperties.setText("a = b = c,  a = b = y = 90°u");
                break;
            case "Tetragonal P":
                tvProperties.setText("a = b ≠ c,  a = b = y = 90°");
                break;
            case "Tetragonal I":
                tvProperties.setText("a = b ≠ c,  a = b = y = 90°");
                break;
            case "Ortorrombica P":
                tvProperties.setText("a ≠ b ≠ c,  a = b = y = 90°");
                break;
            case "Ortorrombica I":
                tvProperties.setText("a = b ≠ c,  a = b = y = 90°");
                break;
            case "Ortorrombica C":
                tvProperties.setText("a ≠ b ≠ c,  a = b = y = 90°");
                break;
            case "Ortorrombica F":
                tvProperties.setText("a ≠ b ≠ c,  a = b = y = 90°");
                break;
            case "Hexagonal P":
                tvProperties.setText("a = b ≠ c,  a = b = 90°, y = 120°");
                break;
            case "Trigonal P":
                tvProperties.setText("a = b = c,  a = b = y ≠ 90°");
                break;
            case "Monoclinica P":
                tvProperties.setText("a ≠ b ≠ c,  a = b = 90°, y ≠ 120°");
                break;
            case "Monoclinica C":
                tvProperties.setText("a ≠ b ≠ c,  a = b = 90°, y ≠ 120°");
                break;
            case "Triclinica":
                tvProperties.setText("a ≠ b ≠ c,  a ≠ b ≠ 90°, y ≠ 120°");
                break;
            default:
                return "";
        }
        return "";
    }
}