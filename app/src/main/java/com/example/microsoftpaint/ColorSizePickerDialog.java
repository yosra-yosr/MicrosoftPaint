// Créez un nouveau fichier appelé ColorSizePickerDialog.java
package com.example.microsoftpaint;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorSizePickerDialog extends Dialog {

    private OnColorSizeSelectedListener listener;
    private int selectedColor = Color.RED;
    private int strokeSize = 10;

    public interface OnColorSizeSelectedListener {
        void onColorSizeSelected(int color, int size);
    }

    public ColorSizePickerDialog(Context context, OnColorSizeSelectedListener listener, int initialColor, int initialSize) {
        super(context);
        this.listener = listener;
        this.selectedColor = initialColor;
        this.strokeSize = initialSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_size_picker, null);
        setContentView(view);
        setTitle("Choisir la couleur et la taille");

        // Configuration du slider de taille
        SeekBar sizeSeekBar = view.findViewById(R.id.size_seekbar);
        final TextView sizeValueText = view.findViewById(R.id.size_value);

        sizeSeekBar.setProgress(strokeSize);
        sizeValueText.setText(String.valueOf(strokeSize));

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                strokeSize = progress + 1; // Pour éviter le 0
                sizeValueText.setText(String.valueOf(strokeSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Configuration des boutons de couleur
        setupColorButton(view.findViewById(R.id.color_black), Color.BLACK);
        setupColorButton(view.findViewById(R.id.color_dark_gray), Color.DKGRAY);
        setupColorButton(view.findViewById(R.id.color_gray), Color.GRAY);
        setupColorButton(view.findViewById(R.id.color_light_gray), Color.LTGRAY);
        setupColorButton(view.findViewById(R.id.color_white), Color.WHITE);

        setupColorButton(view.findViewById(R.id.color_red), Color.RED);
        setupColorButton(view.findViewById(R.id.color_orange), 0xFFFF8800);
        setupColorButton(view.findViewById(R.id.color_yellow), Color.YELLOW);
        setupColorButton(view.findViewById(R.id.color_green), Color.GREEN);
        setupColorButton(view.findViewById(R.id.color_cyan), Color.CYAN);
        setupColorButton(view.findViewById(R.id.color_blue), Color.BLUE);
        setupColorButton(view.findViewById(R.id.color_purple), 0xFF8800FF);

        setupColorButton(view.findViewById(R.id.color_pink), Color.MAGENTA);
        setupColorButton(view.findViewById(R.id.color_brown), 0xFF8B4513);
        setupColorButton(view.findViewById(R.id.color_beige), 0xFFF5F5DC);
        setupColorButton(view.findViewById(R.id.color_olive), 0xFF808000);
        setupColorButton(view.findViewById(R.id.color_teal), 0xFF008080);
        setupColorButton(view.findViewById(R.id.color_pink), 0xFF000080);

        // Bouton de validation
        Button okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onColorSizeSelected(selectedColor, strokeSize);
                }
                dismiss();
            }
        });
    }

    private void setupColorButton(View colorButton, final int color) {
        colorButton.setBackgroundColor(color);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = color;
                highlightSelectedColor(v);
            }
        });

        // Si c'est la couleur initialement sélectionnée
        if (color == selectedColor) {
            highlightSelectedColor(colorButton);
        }
    }

    private void highlightSelectedColor(View selectedView) {
        // Mettre à jour la bordure pour montrer la sélection
        // (Cette partie sera implémentée dans le layout)

        // Vous pourriez ajouter ici du code pour indiquer visuellement la couleur sélectionnée
        // Par exemple, en ajoutant une bordure ou en changeant l'apparence du bouton
    }
}