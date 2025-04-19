package com.example.microsoftpaint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Classe pour représenter un élément de langue
class LanguageItem {
    private String code;
    private String name;
    private int flagResId;

    public LanguageItem(String code, String name, int flagResId) {
        this.code = code;
        this.name = name;
        this.flagResId = flagResId;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getFlagResId() { return flagResId; }
}

// Adaptateur pour la RecyclerView
class LanguageSelectorAdapter extends RecyclerView.Adapter<LanguageSelectorAdapter.ViewHolder> {
    private List<LanguageItem> languages;
    private OnLanguageSelectedListener listener;

    interface OnLanguageSelectedListener {
        void onLanguageSelected(String languageCode);
    }

    public LanguageSelectorAdapter(List<LanguageItem> languages, OnLanguageSelectedListener listener) {
        this.languages = languages;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.language_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LanguageItem item = languages.get(position);
        holder.flagImage.setImageResource(item.getFlagResId());
        holder.languageName.setText(item.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLanguageSelected(item.getCode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView flagImage;
        TextView languageName;

        ViewHolder(View itemView) {
            super(itemView);
            flagImage = itemView.findViewById(R.id.language_flag);
            languageName = itemView.findViewById(R.id.language_name);
        }
    }
}
