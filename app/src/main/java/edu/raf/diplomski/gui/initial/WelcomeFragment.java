package edu.raf.diplomski.gui.initial;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Set;

import edu.raf.diplomski.R;
import edu.raf.diplomski.gui.AppViewModel;
import edu.raf.diplomski.util.SharedPrefsUtil;

@SuppressLint("MissingInflatedId")
public class WelcomeFragment extends Fragment {

    AppViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        Set<String> cachedGenres = SharedPrefsUtil.getGenres(getContext());
        boolean initialized = SharedPrefsUtil.isInitialized(getContext());

        Button continueButton = view.findViewById(R.id.begin_button);
        ImageButton genreSelectionButton = view.findViewById(R.id.genre_selection_button);

        continueButton.setOnClickListener(v -> {
            if(!initialized) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_welcomeFragment_to_genreSelectionFragment);
            }else{
                viewModel.setSelectedGenres(cachedGenres);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_welcomeFragment_to_lslSelectionFragment);
            }
        });

        genreSelectionButton.setOnClickListener(v -> {
            SharedPrefsUtil.setGenres(getContext(), Set.of());
            SharedPrefsUtil.setInitialized(getContext(), false);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_welcomeFragment_to_genreSelectionFragment);
        });

        return view;
    }
}
