package com.example.microsoftpaint;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DrawingFragment extends Fragment {

    private DrawingView drawingView;
    private ImageButton pencilButton, brushButton, forkButton;
    private Button colorRedButton, colorBlueButton, colorGreenButton, colorBlackButton;
    private SeekBar strokeWidthSeekBar;
    private Button clearButton, saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        // Initialiser les vues
        drawingView = view.findViewById(R.id.drawing_view);
        pencilButton = view.findViewById(R.id.pencil_button);
        brushButton = view.findViewById(R.id.brush_button);
        forkButton = view.findViewById(R.id.fork_button);
        colorRedButton = view.findViewById(R.id.color_red);
        colorBlueButton = view.findViewById(R.id.color_blue);
        colorGreenButton = view.findViewById(R.id.color_green);
        colorBlackButton = view.findViewById(R.id.color_black);
        strokeWidthSeekBar = view.findViewById(R.id.stroke_width_seekbar);
        clearButton = view.findViewById(R.id.clear_button);
        saveButton = view.findViewById(R.id.save_button);

        // Configuration des outils de dessin
        pencilButton.setOnClickListener(v -> {
            drawingView.setTool("PENCIL");
            highlightSelectedTool(pencilButton);
        });

        brushButton.setOnClickListener(v -> {
            drawingView.setTool("BRUSH");
            highlightSelectedTool(brushButton);
        });

        forkButton.setOnClickListener(v -> {
            drawingView.setTool("FORK");
            highlightSelectedTool(forkButton);
        });

        // Configuration des boutons de couleur
        colorRedButton.setOnClickListener(v -> drawingView.setColor(Color.RED));
        colorBlueButton.setOnClickListener(v -> drawingView.setColor(Color.BLUE));
        colorGreenButton.setOnClickListener(v -> drawingView.setColor(Color.GREEN));
        colorBlackButton.setOnClickListener(v -> drawingView.setColor(Color.BLACK));

        // Configuration du contrôle de largeur du trait
        strokeWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.setStrokeWidth(progress + 5); // Minimum width of 5
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Configuration du bouton d'effacement
        clearButton.setOnClickListener(v -> drawingView.clearCanvas());

        // Configuration du bouton de sauvegarde
        saveButton.setOnClickListener(v -> saveDrawing());

        // État initial - sélectionner l'outil crayon par défaut
        highlightSelectedTool(pencilButton);

        return view;
    }

    private void highlightSelectedTool(ImageButton selectedButton) {
        // Réinitialiser l'état de tous les boutons d'outils
        pencilButton.setSelected(false);
        brushButton.setSelected(false);
        forkButton.setSelected(false);

        // Définir l'état sélectionné pour le bouton actuel
        selectedButton.setSelected(true);
    }

    private void saveDrawing() {
        if (drawingView == null) {
            Toast.makeText(getContext(), "Impossible de sauvegarder le dessin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap = drawingView.getBitmap();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "PAINT_" + timeStamp + ".jpg";

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri imageUri = getContext().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (imageUri != null) {
                OutputStream outputStream = getContext().getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                Toast.makeText(getContext(), "Image sauvegardée dans la galerie", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erreur lors de la sauvegarde: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}