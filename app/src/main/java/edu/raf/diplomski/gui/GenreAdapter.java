package edu.raf.diplomski.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.raf.diplomski.R;
import lombok.Getter;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private final List<String> genreList;
    @Getter
    private final Set<String> selectedGenres = new HashSet<>();

    public GenreAdapter(List<String> genreList) {
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        String genre = genreList.get(position);
        holder.genreName.setText(genre);

        holder.genreCheckBox.setChecked(selectedGenres.contains(genre));

        // Handle checkbox click
        holder.genreCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGenres.add(genre);
            } else {
                selectedGenres.remove(genre);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        public TextView genreName;
        public CheckBox genreCheckBox;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.genre_name);
            genreCheckBox = itemView.findViewById(R.id.genre_checkbox);
        }
    }
}
