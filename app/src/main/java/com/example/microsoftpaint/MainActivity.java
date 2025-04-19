package com.example.microsoftpaint;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        // Masquer la barre de navigation système (pour une immersion complète)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

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