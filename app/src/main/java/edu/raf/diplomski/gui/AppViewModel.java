package edu.raf.diplomski.gui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Set;

import edu.raf.diplomski.data.Song;
import edu.raf.diplomski.eeg.Channel;
import edu.raf.diplomski.eeg.EmotionalState;
import edu.raf.diplomski.eeg.EmotionalStateBuffer;
import edu.raf.diplomski.eeg.Layout;
import edu.raf.diplomski.lsl.LslStream;

public class AppViewModel extends ViewModel {

    private final MutableLiveData<LslStream> selectedStream = new MutableLiveData<>();
    private final MutableLiveData<List<Channel>> selectedStreamChannels = new MutableLiveData<>();
    private final MutableLiveData<Layout> layout = new MutableLiveData<>();
    private final MutableLiveData<Set<String>> selectedGenres = new MutableLiveData<>();
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> playing = new MutableLiveData<>();
    public final MutableLiveData<EmotionalState> baselineEmotionalState = new MutableLiveData<>();

    public void setSelectedStream(LslStream stream) {
        selectedStream.setValue(stream);
    }

    public LiveData<LslStream> getSelectedStream() {
        return selectedStream;
    }

    public void setSelectedStreamChannels(List<Channel> channels){
        System.out.println(channels);
        selectedStreamChannels.setValue(channels);
    }

    public LiveData<List<Channel>> getSelectedStreamChannels(){
        return selectedStreamChannels;
    }

    public void setLayout(Layout layout) {
        this.layout.setValue(layout);
    }

    public LiveData<Layout> getLayout() {
        return layout;
    }

    public void setSelectedGenres(Set<String> genres) {
        selectedGenres.setValue(genres);
    }

    public LiveData<Set<String>> getSelectedGenres() {
        return selectedGenres;
    }

    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
    }

    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public void setPlaying(boolean playing) {
        this.playing.setValue(playing);
    }

    public LiveData<Boolean> isPlaying() {
        return playing;
    }

    public void setBaselineEmotionalState(EmotionalState emotionalState) {
        baselineEmotionalState.setValue(emotionalState);
    }

    public LiveData<EmotionalState> getBaselineEmotionalState() {
        return baselineEmotionalState;
    }
}
