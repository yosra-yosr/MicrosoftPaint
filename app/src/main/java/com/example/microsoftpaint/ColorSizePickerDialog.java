package com.example.microsoftpaint;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ColorSizePickerDialog extends Dialog {

    private OnColorSizeSelectedListener listener;
    private int selectedColor = Color.RED;
    private int strokeSize = 10;
    private View lastSelectedColorView = null;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_size_picker, null);
        setContentView(view);

        // Configuration pour prendre toute la largeur de l'écran
        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        // Configuration du slider de taille
        SeekBar sizeSeekBar = view.findViewById(R.id.size_seekbar);
        final TextView sizeValueText = view.findViewById(R.id.size_value);
        final View sizeExample = view.findViewById(R.id.size_example);

        sizeSeekBar.setProgress(strokeSize);
        sizeValueText.setText(String.valueOf(strokeSize));
        updateSizeExample(sizeExample, strokeSize, selectedColor);

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                strokeSize = progress + 1; // Pour éviter le 0
                sizeValueText.setText(String.valueOf(strokeSize));
                updateSizeExample(sizeExample, strokeSize, selectedColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Configuration des boutons de couleur
        setupColorCircle(view.findViewById(R.id.color_black), Color.BLACK);
        setupColorCircle(view.findViewById(R.id.color_dark_gray), Color.DKGRAY);
        setupColorCircle(view.findViewById(R.id.color_gray), Color.GRAY);
        setupColorCircle(view.findViewById(R.id.color_light_gray), Color.LTGRAY);
        setupColorCircle(view.findViewById(R.id.color_white), Color.WHITE);

        setupColorCircle(view.findViewById(R.id.color_red), Color.RED);
        setupColorCircle(view.findViewById(R.id.color_orange), 0xFFFF8800);
        setupColorCircle(view.findViewById(R.id.color_yellow), Color.YELLOW);
        setupColorCircle(view.findViewById(R.id.color_green), Color.GREEN);
        setupColorCircle(view.findViewById(R.id.color_cyan), Color.CYAN);

        setupColorCircle(view.findViewById(R.id.color_blue), Color.BLUE);
        setupColorCircle(view.findViewById(R.id.color_purple), 0xFF8800FF);
        setupColorCircle(view.findViewById(R.id.color_pink), Color.MAGENTA);
        setupColorCircle(view.findViewById(R.id.color_brown), 0xFF8B4513);
        setupColorCircle(view.findViewById(R.id.color_beige), 0xFFF5F5DC);

        setupColorCircle(view.findViewById(R.id.color_magenta), 0xFFFF00FF);
        setupColorCircle(view.findViewById(R.id.color_teal), 0xFF008080);
        setupColorCircle(view.findViewById(R.id.color_olive), 0xFF808000);
        setupColorCircle(view.findViewById(R.id.color_navy), 0xFF000080);
        setupColorCircle(view.findViewById(R.id.color_lavender), 0xFFE6E6FA);

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

        // Sélectionner la couleur initiale
        highlightInitialColor(view);
    }

    private void updateSizeExample(View sizeExample, int size, int color) {
        ViewGroup.LayoutParams params = sizeExample.getLayoutParams();
        params.height = size;
        sizeExample.setLayoutParams(params);
        sizeExample.setBackgroundColor(color);
    }

    private void setupColorCircle(View colorView, final int color) {
        if (colorView instanceof ImageView) {
            ImageView imageView = (ImageView) colorView;
            GradientDrawable drawable = (GradientDrawable) imageView.getContext().getDrawable(R.drawable.circle_color).mutate();

            if (drawable != null) {
                drawable.setColor(color);
                imageView.setImageDrawable(drawable);
            }

            colorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedColor = color;
                    highlightSelectedColor(v);
                    View sizeExample = ((View) v.getParent().getParent()).findViewById(R.id.size_example);
                    updateSizeExample(sizeExample, strokeSize, selectedColor);
                }
            });
        }
    }

    private void highlightInitialColor(View rootView) {
        // Recherche la vue de la couleur initiale et la sélectionne
        int[] colorIds = new int[] {
                R.id.color_black, R.id.color_dark_gray, R.id.color_gray, R.id.color_light_gray, R.id.color_white,
                R.id.color_red, R.id.color_orange, R.id.color_yellow, R.id.color_green, R.id.color_cyan,
                R.id.color_blue, R.id.color_purple, R.id.color_pink, R.id.color_brown, R.id.color_beige,
                R.id.color_magenta, R.id.color_teal, R.id.color_olive, R.id.color_navy, R.id.color_lavender
        };

        int[] colorValues = new int[] {
                Color.BLACK, Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.WHITE,
                Color.RED, 0xFFFF8800, Color.YELLOW, Color.GREEN, Color.CYAN,
                Color.BLUE, 0xFF8800FF, Color.MAGENTA, 0xFF8B4513, 0xFFF5F5DC,
                0xFFFF00FF, 0xFF008080, 0xFF808000, 0xFF000080, 0xFFE6E6FA
        };

        // Trouver la couleur la plus proche
        int closestColorIndex = 0;
        int minDifference = Integer.MAX_VALUE;

        for (int i = 0; i < colorValues.length; i++) {
            int diff = colorDifference(selectedColor, colorValues[i]);
            if (diff < minDifference) {
                minDifference = diff;
                closestColorIndex = i;
            }
        }

        // Sélectionner la couleur la plus proche
        View colorView = rootView.findViewById(colorIds[closestColorIndex]);
        if (colorView != null) {
            highlightSelectedColor(colorView);
        }
    }

    private int colorDifference(int color1, int color2) {
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);

        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);

        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    private void highlightSelectedColor(View selectedView) {
        // Réinitialiser l'apparence de la dernière vue sélectionnée
        if (lastSelectedColorView != null) {
            lastSelectedColorView.setBackground(lastSelectedColorView.getContext().getDrawable(R.drawable.circle_background));
        }

        // Mettre à jour l'apparence de la nouvelle vue sélectionnée
        selectedView.setBackground(selectedView.getContext().getDrawable(R.drawable.selected_circle));
        lastSelectedColorView = selectedView;
    }
}