package edu.raf.diplomski.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SongData {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static List<Song> cachedSongs;
    private static final Random random = new Random();
    private static final Set<Song> playedSongs = new HashSet<>(); // To track played songs

    public interface OnSongsLoadedListener {
        void onSongsLoaded(List<Song> songs);
    }

    public static void loadSongsInBackground(Context context, OnSongsLoadedListener listener) {
        if (cachedSongs != null) {
            if (listener != null) {
                listener.onSongsLoaded(cachedSongs);
            }
            return;
        }

        executor.submit(() -> {
            CsvReader csvReader = new CsvReader(context);
            List<Song> songList = csvReader.loadSongsFromCsv();
            cachedSongs = songList;  // Cache the songs

            if (listener != null) {
                listener.onSongsLoaded(songList);
            }
        });
    }

    public static List<Song> getSongs() {
        if (cachedSongs == null) {
            throw new IllegalStateException("Songs have not been loaded yet. Call loadSongsInBackground first.");
        }
        return cachedSongs;
    }

    public static List<String> getDistinctGenres() {
        if (cachedSongs == null) {
            throw new IllegalStateException("Songs have not been loaded yet. Call loadSongsInBackground first.");
        }

        Set<String> distinctGenresSet = new HashSet<>();
        for (Song song : cachedSongs) {
            distinctGenresSet.add(song.getGenre());
        }

        // Convert the Set to a List and return
        return new ArrayList<>(distinctGenresSet);
    }

    public static Song getRandomSong(List<String> genres) {
        if (cachedSongs == null) {
            throw new IllegalStateException("Songs have not been loaded yet. Call loadSongsInBackground first.");
        }

        List<String> lowerCaseGenres = new ArrayList<>();
        for (String genre : genres) {
            lowerCaseGenres.add(genre.toLowerCase());
        }

        List<Song> matchingSongs = new ArrayList<>();
        for (Song song : cachedSongs) {
            String songGenreLower = song.getGenre().toLowerCase();
            for (String genre : lowerCaseGenres) {
                if (songGenreLower.contains(genre) && !playedSongs.contains(song)) { // Check if song is already played
                    matchingSongs.add(song);
                    break;
                }
            }
        }

        if (matchingSongs.isEmpty()) {
            return null;
        }

        // Select a random song from the list of unplayed songs
        Song selectedSong = matchingSongs.get(random.nextInt(matchingSongs.size()));
        playedSongs.add(selectedSong); // Mark this song as played
        return selectedSong;
    }

    public static Song getSongByEmotion(String genre, double targetValence, double targetArousal) {
        if (cachedSongs == null) {
            throw new IllegalStateException("Songs have not been loaded yet. Call loadSongsInBackground first.");
        }

        List<Song> matchingSongs = new ArrayList<>();
        String lowerCaseGenre = genre.toLowerCase();

        // Filter songs by genre and check if they have already been played
        for (Song song : cachedSongs) {
            String songGenreLower = song.getGenre().toLowerCase();
            if (songGenreLower.contains(lowerCaseGenre) && !playedSongs.contains(song) && song.getSpotifyId() != null && !song.getSpotifyId().isEmpty()) {
                matchingSongs.add(song);
            }
        }

        if (matchingSongs.isEmpty()) {
            return null;  // No unplayed songs found in the requested genre
        }

        // Find the song with the closest valence and arousal
        Song closestSong = null;
        double closestDistance = Double.MAX_VALUE;

        for (Song song : matchingSongs) {
            double valenceDifference = song.getValenceTags() - targetValence;
            double arousalDifference = song.getArousalTags() - targetArousal;
            double distance = Math.sqrt(Math.pow(valenceDifference, 2) + Math.pow(arousalDifference, 2));  // Euclidean distance

            if (distance < closestDistance) {
                closestDistance = distance;
                closestSong = song;
            }
        }

        // Mark the song as played
        if (closestSong != null) {
            playedSongs.add(closestSong);
        }

        return closestSong;
    }

    public static Song getSongByEmotionShuffled(List<String> genres, double targetValence, double targetArousal) {
        if (cachedSongs == null) {
            throw new IllegalStateException("Songs have not been loaded yet. Call loadSongsInBackground first.");
        }

        List<Song> matchingSongs = new ArrayList<>();
        double allowedDistance = 1.0; // Public static value for the distance threshold

        // Filter songs by genres and check if they have already been played
        for (Song song : cachedSongs) {
            String songGenreLower = song.getGenre().toLowerCase();
            for (String genre : genres) {
                if (songGenreLower.contains(genre.toLowerCase()) && !playedSongs.contains(song)
                        && song.getSpotifyId() != null && !song.getSpotifyId().isEmpty()) {
                    double valenceDifference = Math.abs(song.getValenceTags() - targetValence);
                    double arousalDifference = Math.abs(song.getArousalTags() - targetArousal);
                    if (valenceDifference <= allowedDistance && arousalDifference <= allowedDistance) {
                        matchingSongs.add(song);
                    }
                }
            }
        }

        if (matchingSongs.isEmpty()) {
            return null;  // No unplayed songs found within the requested range
        }

        // Randomize the list of matching songs to ensure different songs are returned
        Collections.shuffle(matchingSongs);

        // Select a song randomly from the shuffled list
        Song selectedSong = matchingSongs.get(random.nextInt(matchingSongs.size()));

        // Mark the song as played
        playedSongs.add(selectedSong);

        return selectedSong;
    }


    // Method to refresh memory and allow songs to repeat
    public static void refreshMemory() {
        playedSongs.clear();
    }

    public static boolean areSongsLoaded() {
        return cachedSongs != null;
    }
}
