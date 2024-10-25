package edu.raf.diplomski.gui.regular;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;

import edu.raf.diplomski.R;
import edu.raf.diplomski.data.SongData;
import edu.raf.diplomski.eeg.EmotionalStateWorker;
import edu.raf.diplomski.eeg.EmotionalState;
import edu.raf.diplomski.gui.AppViewModel;

public class CalibrationFragment extends Fragment {
    // TODO Change this to the actual calibration time when done with the development
    public static final int CALIBRATION_TIME = 30;

    private AppViewModel viewModel;
    private LottieAnimationView lottieAnimationView;
    private Button startCalibrationButton;
    private ProgressBar progressBar;
    private TextView progressPercentage;
    private EmotionalStateWorker emotionalStateWorker;
    private Handler handler;
    private int progressStatus = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calibration, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        lottieAnimationView = view.findViewById(R.id.lottie_animation_view);
        startCalibrationButton = view.findViewById(R.id.start_calibration_button);
        progressBar = view.findViewById(R.id.progress_bar);
        progressPercentage = view.findViewById(R.id.progress_percentage);

        // Disable the animation from auto-playing at start
        lottieAnimationView.pauseAnimation();

        // Set up click listener for the Start Calibration button
        startCalibrationButton.setOnClickListener(v -> startCalibration());

        this.emotionalStateWorker = new EmotionalStateWorker(viewModel.getSelectedStream().getValue(), viewModel.getLayout().getValue());

        return view;
    }

    private void startCalibration() {
        // Disable the button and change its color
        startCalibrationButton.setEnabled(false);
        startCalibrationButton.setBackgroundTintList(getResources().getColorStateList(R.color.border)); // Assuming R.color.disabled_button is defined for disabled state

        // Start the animation
        lottieAnimationView.playAnimation();

        // Optionally, you can update the progress over time (simulation)
        emotionalStateWorker.start();
        updateProgressBar();
    }

    private void updateProgressBar() {
        handler = new Handler(Looper.getMainLooper());

        // Use a thread to update the progress bar more frequently (e.g., every 100ms)
        new Thread(() -> {
            while (progressStatus < CALIBRATION_TIME * 10) {  // Multiply by 10 to allow more frequent updates
                try {
                    Thread.sleep(100); // Update every 100ms
                    progressStatus++;
                    handler.post(() -> {
                        // Update progress bar and percentage text
                        int progressPercent = (progressStatus * 100) / (CALIBRATION_TIME * 10);
                        progressBar.setProgress(progressPercent);
                        progressPercentage.setText(progressPercent + "%");
                    });
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted");
                }
            }
            requireActivity().runOnUiThread(this::stopCalibrationAndStoreResult);
        }).start();
    }

    private void stopCalibrationAndStoreResult() {
        // Stop the baseline calculator when calibration is complete
        EmotionalState emotionalState = emotionalStateWorker.stopAndGetEmotionalStateMean();
        viewModel.setBaselineEmotionalState(emotionalState);
        Log.d("CalibrationFragment", "Baseline emotional state: " + emotionalState);

        // Stop the animation and reset button
        handler.post(() -> {
            lottieAnimationView.pauseAnimation();
            startCalibrationButton.setEnabled(true);
            startCalibrationButton.setBackgroundTintList(getResources().getColorStateList(R.color.button));
            setupButtonForNextScreen();
        });
    }

    private void setupButtonForNextScreen() {
        startCalibrationButton.setText("Play music!");
        startCalibrationButton.setOnClickListener(v -> {
            if(SongData.areSongsLoaded()) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_calibration_to_player);
            }else{
                Toast.makeText(getContext(), "Songs are not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
