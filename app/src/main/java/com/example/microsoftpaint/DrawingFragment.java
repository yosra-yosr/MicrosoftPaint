package com.example.microsoftpaint;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DrawingFragment extends Fragment implements View.OnClickListener {

    private DrawingView drawingView;
    private ImageButton brushButton, pencilButton, forkButton, rectangleButton, saveButton,colorPickerButton,eraserButton,discardButton;
    private Button colorButton;
    private Spinner languageSpinner;
    private boolean isStrokeColorPickerMode = true; // Par défaut, on change la couleur du trait
    private String currentTool = "BRUSH";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        // Initialiser les vues
        drawingView = view.findViewById(R.id.drawing_view);
        brushButton = view.findViewById(R.id.brush_button);
        pencilButton = view.findViewById(R.id.pencil_button);
        forkButton = view.findViewById(R.id.fork_button);
        rectangleButton = view.findViewById(R.id.rectangle_button);
        colorButton = view.findViewById(R.id.color_red);
        colorPickerButton = view.findViewById(R.id.color_picker);
        saveButton = view.findViewById(R.id.save_button);
        languageSpinner = view.findViewById(R.id.language_spinner);
        eraserButton = view.findViewById(R.id.eraser_button);
        discardButton = view.findViewById(R.id.discard_button);


        // Configurer les écouteurs d'événements
        brushButton.setOnClickListener(this);
        pencilButton.setOnClickListener(this);
        forkButton.setOnClickListener(this);
        rectangleButton.setOnClickListener(this);
        colorButton.setOnClickListener(this);
        colorPickerButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        eraserButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);


        // Configuration du spinner de langues
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.languages,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Définir la langue par défaut
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                // Ici vous pourriez changer la langue de l'application
                Toast.makeText(getContext(), "Langue sélectionnée: " + selectedLanguage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // État initial - sélectionner l'outil pinceau par défaut
        brushButton.setSelected(true);
        drawingView.setTool("BRUSH");
        currentTool = "BRUSH";

        // Définir la couleur initiale du bouton de couleur
        colorButton.setBackgroundColor(drawingView.getStrokeColor());

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.brush_button) {
            setSelectedTool(brushButton);
            currentTool = "BRUSH";
            drawingView.setTool("BRUSH");
            showColorSizePicker();
        } else if (id == R.id.pencil_button) {
            setSelectedTool(pencilButton);
            currentTool = "PENCIL";
            drawingView.setTool(currentTool);
            showColorSizePicker();
        } else if (id == R.id.fork_button) {
            setSelectedTool(forkButton);
            currentTool = "FORK";
            drawingView.setTool(currentTool);
            showColorSizePicker();
        } else if (id == R.id.rectangle_button) {
            setSelectedTool(rectangleButton);
            currentTool = "RECTANGLE";
            drawingView.setTool(currentTool);
            showColorSizePicker();
        } else if (id == R.id.color_red) {
            showColorSelectionDialog();
        } else if (id == R.id.color_picker) {
            showBackgroundOrStrokeDialog();
        } else if (id == R.id.save_button) {
            saveDrawing();
        } else if (id == R.id.eraser_button) {
            setSelectedTool(eraserButton);
            currentTool = "ERASER";
            drawingView.setTool(currentTool);
        } else if (id == R.id.discard_button) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Effacer le dessin")
                    .setMessage("Voulez-vous vraiment effacer tout le dessin?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            drawingView.clearCanvas();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        }
    }

    private void showColorSizePicker() {
        // Ne pas afficher le sélecteur pour la gomme
        if (currentTool.equals("ERASER")) {
            return;
        }

        ColorSizePickerDialog dialog = new ColorSizePickerDialog(
                getContext(),
                new ColorSizePickerDialog.OnColorSizeSelectedListener() {
                    @Override
                    public void onColorSizeSelected(int color, int size) {
                        drawingView.setStrokeColor(color);
                        drawingView.setStrokeWidth(size);
                        colorButton.setBackgroundColor(color);
                    }
                },
                drawingView.getStrokeColor(),
                drawingView.getStrokeWidth()
        );
        dialog.show();
    }

    private void setSelectedTool(ImageButton selectedButton) {
        brushButton.setSelected(false);
        pencilButton.setSelected(false);
        forkButton.setSelected(false);
        rectangleButton.setSelected(false);
        eraserButton.setSelected(false);
        selectedButton.setSelected(true);
    }

    private void showBackgroundOrStrokeDialog() {
        final String[] choices = {"Couleur du trait", "Couleur de fond"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choisir la couleur à modifier")
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isStrokeColorPickerMode = (which == 0);
                        showColorSelectionDialog();
                    }
                });
        builder.create().show();
    }

    private void showColorSelectionDialog() {
        Activity activity = getActivity();
        if (activity == null) return;

        new ColorPickerDialog.Builder(activity)
                .setTitle("Choisir une couleur")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("Valider", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        int selectedColor = envelope.getColor();
                        if (isStrokeColorPickerMode) {
                            drawingView.setStrokeColor(selectedColor);
                            colorButton.setBackgroundColor(selectedColor);
                        } else {
                            drawingView.setBackgroundColor(selectedColor);
                        }
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show();
    }

    private void saveDrawing() {
        if (drawingView == null) {
            Toast.makeText(getContext(), "Impossible de sauvegarder le dessin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap = drawingView.getBitmap();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "LETSPAINT_" + timeStamp + ".jpg";

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