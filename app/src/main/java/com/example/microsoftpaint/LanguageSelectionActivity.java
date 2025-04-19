package com.example.microsoftpaint;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class LanguageSelectionActivity extends AppCompatActivity {

    private MaterialCardView cardEnglish, cardFrench, cardArabic;
    private ImageView checkEnglish, checkFrench, checkArabic;
    private FloatingActionButton fabContinue;
    private TextView textTitle;
    private String selectedLanguage = "fr"; // Par défaut français comme dans votre capture d'écran

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifier si l'utilisateur a déjà choisi une langue
        if (isLanguageAlreadySelected()) {
            // Si oui, passer directement à MainActivity
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_language_selection);

        // Initialiser les vues
        cardEnglish = findViewById(R.id.cardEnglish);
        cardFrench = findViewById(R.id.cardFrench);
        cardArabic = findViewById(R.id.cardArabic);

        checkEnglish = findViewById(R.id.checkEnglish);
        checkFrench = findViewById(R.id.checkFrench);
        checkArabic = findViewById(R.id.checkArabic);

        textTitle = findViewById(R.id.textTitle);

        fabContinue = findViewById(R.id.fabContinue);

        // Définir les écouteurs de clic pour les cartes de langue
        cardEnglish.setOnClickListener(v -> selectLanguage("en"));
        cardFrench.setOnClickListener(v -> selectLanguage("fr"));
        cardArabic.setOnClickListener(v -> selectLanguage("ar"));

        // Définir l'écouteur de clic pour le bouton de continuation
        fabContinue.setOnClickListener(v -> {
            // Sauvegarder la langue sélectionnée
            saveLanguagePreference(selectedLanguage);
            // Passer à l'activité principale
            startMainActivity();
        });

        // Montrer la sélection par défaut (français selon votre capture d'écran)
        // Appliquer immédiatement la langue française pour le titre
        updateCheckmarks();
        updateTitleLanguage();
    }

    private boolean isLanguageAlreadySelected() {
        // Vérifier si la langue a déjà été sélectionnée
        return getSharedPreferences("Settings", MODE_PRIVATE)
                .contains("My_Lang") &&
                !getSharedPreferences("Settings", MODE_PRIVATE)
                        .getBoolean("FirstLaunch", true);
    }

    private void selectLanguage(String languageCode) {
        selectedLanguage = languageCode;
        updateCheckmarks();

        // Mettre à jour immédiatement le titre selon la langue sélectionnée
        updateTitleLanguage();
    }

    private void updateTitleLanguage() {
        // Créer une configuration temporaire avec la langue sélectionnée
        Resources resources = getResources();
        Configuration tempConfig = new Configuration(resources.getConfiguration());
        tempConfig.setLocale(new Locale(selectedLanguage));

        // Créer un contexte temporaire pour cette configuration
        Resources tempResources = createConfigurationContext(tempConfig).getResources();

        // Obtenir la chaîne correcte dans la langue sélectionnée
        String translatedTitle = tempResources.getString(R.string.choose_language);

        // Mettre à jour le titre
        textTitle.setText(translatedTitle);
    }

    private void updateCheckmarks() {
        // Cacher tous les checks
        checkEnglish.setVisibility(View.GONE);
        checkFrench.setVisibility(View.GONE);
        checkArabic.setVisibility(View.GONE);

        // Afficher le check pour la langue sélectionnée
        switch (selectedLanguage) {
            case "en":
                checkEnglish.setVisibility(View.VISIBLE);
                // Changer l'image du check pour l'anglais
                checkEnglish.setImageResource(R.drawable.ic_check_circle);
                break;
            case "fr":
                checkFrench.setVisibility(View.VISIBLE);
                // Changer l'image du check pour le français
                checkFrench.setImageResource(R.drawable.ic_check_circle);
                break;
            case "ar":
                checkArabic.setVisibility(View.VISIBLE);
                // Changer l'image du check pour l'arabe
                checkArabic.setImageResource(R.drawable.ic_check_circle);
                break;
        }
    }

    private void saveLanguagePreference(String languageCode) {
        // Enregistrer la langue préférée dans les SharedPreferences
        getSharedPreferences("Settings", MODE_PRIVATE)
                .edit()
                .putString("My_Lang", languageCode)
                .putBoolean("FirstLaunch", false) // Marquer que l'app a été lancée
                .apply();
    }

    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void startMainActivity() {
        // Appliquer la langue sélectionnée avant de passer à l'activité principale
        setLocale(selectedLanguage);

        Intent intent = new Intent(LanguageSelectionActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}