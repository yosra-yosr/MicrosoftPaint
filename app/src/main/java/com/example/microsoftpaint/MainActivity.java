package com.example.microsoftpaint;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Si l'activité est créée pour la première fois, ajouter le fragment de dessin
        if (savedInstanceState == null) {
            DrawingFragment drawingFragment = new DrawingFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, drawingFragment);
            transaction.commit();
        }
    }
}