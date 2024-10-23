package edu.raf.diplomski.gui.regular;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import edu.raf.diplomski.R;
import edu.raf.diplomski.data.SongData;
import edu.raf.diplomski.eeg.EmotionalState;
import edu.raf.diplomski.eeg.EmotionalStateBuffer;
import edu.raf.diplomski.eeg.EmotionalStateWorker;
import edu.raf.diplomski.gui.AppViewModel;

public class PlayerFragment extends Fragment {

    private static final String CLIENT_ID = "983c7b45f57145b1a2a63f4dbb095e53";
    private static final String REDIRECT_URI = "diplomski://callback";
    private static final int BUFFER_SIZE = 50; // Example buffer size
    private static final int WORKER_INTERVAL_SECONDS = 30;

    private SpotifyAppRemote mSpotifyAppRemote;
    private ImageButton playButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private LottieAnimationView animationView;
    private TextView songTitle;
    private AppViewModel viewModel;
    private EmotionalStateBuffer emotionalStateBuffer;
    private EmotionalStateWorker emotionalStateWorker;
    private Handler workerHandler;
    private Runnable workerRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        animationView = view.findViewById(R.id.animation_view_player);

        emotionalStateBuffer = new EmotionalStateBuffer(BUFFER_SIZE);
        emotionalStateBuffer.putValue(viewModel.getBaselineEmotionalState().getValue());

        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build();

        SpotifyAppRemote.connect(getContext(), connectionParams, new Connector.ConnectionListener() {
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("PlayerFragment", "Connected! Yay!");
                findAndPlaySong();

                // This plays the next song automatically if it finishes playing
                mSpotifyAppRemote.getPlayerApi()
                        .subscribeToPlayerState()
                        .setEventCallback(playerState -> {
                            if (playerState.track != null) {
                                long positionInMs = playerState.playbackPosition;
                                long trackDurationInMs = playerState.track.duration;

                                // If we are very close to the end of the track
                                if (trackDurationInMs - positionInMs < 1000 && !playerState.isPaused) {
                                    // Stop the current song and play the next one
                                    findAndPlaySong();
                                }
                            }
                        });
            }

            public void onFailure(Throwable throwable) {
                Log.e("MyActivity", throwable.getMessage(), throwable);
                Toast.makeText(getContext(), "Failed to connect to Spotify. Restart the app.", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            if(isPlaying){
                playButton.setBackgroundResource(R.drawable.stop);
                animationView.resumeAnimation();
            }else{
                playButton.setBackgroundResource(R.drawable.play);
                animationView.pauseAnimation();
            }
        });
        viewModel.setPlaying(true);

        playButton = view.findViewById(R.id.play_pause_button);
        playButton.setOnClickListener( v -> {
            if(Boolean.TRUE.equals(viewModel.isPlaying().getValue())) {
                mSpotifyAppRemote.getPlayerApi().pause();
                viewModel.setPlaying(false);
            }else {
                mSpotifyAppRemote.getPlayerApi().resume();
                viewModel.setPlaying(true);
            }
        });

        songTitle = view.findViewById(R.id.song_label);
        viewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            String songTitleText = song.getArtist() + " - " + song.getTrack();
            songTitle.setText(songTitleText);
        });

        prevButton = view.findViewById(R.id.previous_button);
        prevButton.setOnClickListener(v -> {
            mSpotifyAppRemote.getPlayerApi().skipPrevious();
        });

        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            findAndPlaySong();
        });

        startEmotionalStateWorkerLoop();
        findAndPlaySong();

        return view;
    }

    private void startEmotionalStateWorkerLoop() {
        workerHandler = new Handler(Looper.getMainLooper());
        workerRunnable = () -> {
            if (emotionalStateWorker != null) {
                EmotionalState emotionalState = emotionalStateWorker.stopAndGetEmotionalStateMean();
                emotionalStateBuffer.putValue(emotionalState);
                Log.d("PlayerFragment", "EmotionalState added to buffer: Valence = " +
                        emotionalState.getValence() + ", Arousal = " + emotionalState.getArousal());
                Log.d("PlayerFragment", "Current state in the buffer. Valence=  " + emotionalStateBuffer.getValenceMean() + ", Arousal = " + emotionalStateBuffer.getArousalMean());
            }

            emotionalStateWorker = new EmotionalStateWorker(viewModel.getSelectedStream().getValue(), viewModel.getLayout().getValue());
            emotionalStateWorker.start();
            Log.d("PlayerFragment", "New EmotionalStateWorker started.");

            // Schedule this runnable to run again after the specified interval
            workerHandler.postDelayed(workerRunnable, WORKER_INTERVAL_SECONDS * 1000);
        };

        workerHandler.post(workerRunnable);
    }

    private void findAndPlaySong(){
        CompletableFuture.supplyAsync(() -> {
            List<String> genres = new ArrayList<>(viewModel.getSelectedGenres().getValue());
            return SongData.getSongByEmotionShuffled(genres,  emotionalStateBuffer.getValenceMean(), emotionalStateBuffer.getArousalMean());
        }).thenAccept(song -> {
            String songID = song.getSpotifyId();
            mSpotifyAppRemote.getPlayerApi().play("spotify:track:" + songID);
            Log.i("PlayerFragment", "Playing song: " + song);
            requireActivity().runOnUiThread(() -> viewModel.setCurrentSong(song));
        });
    }
}
