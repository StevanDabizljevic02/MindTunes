package edu.raf.diplomski.gui.initial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Set;

import edu.raf.diplomski.Constants;
import edu.raf.diplomski.R;
import edu.raf.diplomski.gui.AppViewModel;
import edu.raf.diplomski.gui.GenreAdapter;
import edu.raf.diplomski.util.SharedPrefsUtil;

public class GenreFragment extends Fragment {

    private GenreAdapter genreAdapter;
    private AppViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_selection, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        RecyclerView genreRecyclerView = view.findViewById(R.id.genre_recycler_view);
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        genreAdapter = new GenreAdapter(Constants.GENRES);
        genreRecyclerView.setAdapter(genreAdapter);

        Button continueButton = view.findViewById(R.id.continue_to_lsl_button);
        continueButton.setOnClickListener(v -> {
            Set<String> selectedGenres = genreAdapter.getSelectedGenres();
            Log.d("SelectedGenres", "Selected genres: " + selectedGenres.toString());

            if(!selectedGenres.isEmpty()){
                SharedPrefsUtil.setGenres(this.getContext(), selectedGenres);
                SharedPrefsUtil.setInitialized(this.getContext(), true);
                viewModel.setSelectedGenres(selectedGenres);

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_genreSelectionFragment_to_mainFlow);
            }else{
                Toast.makeText(getContext(), "Please select at least one genre", Toast.LENGTH_SHORT).show();
            }


        });

        return view;
    }
}
