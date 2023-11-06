package com.example.laboratorio6_20201696;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button verPuzzle = findViewById(R.id.verVersionSimple);
        Button verMemory= findViewById(R.id.verVersionClasica);

        verPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para abrir la nueva actividad
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);

                // Inicia la nueva actividad
                startActivity(intent);
            }
        });
        verMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para abrir la nueva actividad
                Intent intent = new Intent(MainActivity.this, MemoryActivity.class);

                // Inicia la nueva actividad
                startActivity(intent);
            }
        });
    }
}