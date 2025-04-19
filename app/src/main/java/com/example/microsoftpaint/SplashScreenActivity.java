package com.example.microsoftpaint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // Durée de l'écran de démarrage en millisecondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Masquer la barre de navigation et la barre de statut
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Masquer la barre de navigation système (pour une immersion complète)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_splash_screen);

        // Configurer l'animation Lottie
        LottieAnimationView animationView = findViewById(R.id.lottie_animation);
        animationView.setAnimation(R.raw.splash);
        animationView.setRepeatCount(0); // Animation une seule fois (plus naturel pour un splash)
        animationView.playAnimation();

        // Passer à l'activité de sélection de langue après la durée spécifiée
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, LanguageSelectionActivity.class);
            startActivity(intent);
            finish(); // Fermer cette activité pour qu'elle ne soit pas accessible via retour arrière
        }, SPLASH_DURATION);
    }
}