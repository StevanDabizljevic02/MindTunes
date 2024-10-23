package edu.raf.diplomski.data;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class Song {

    private final String lastFmUrl;
    private final String track;
    private final String artist;
    private final List<String> seeds;
    private final int numberOfEmotionTags;
    private final double valenceTags;
    private final double arousalTags;
    private final double dominanceTags;
    private final String mbid;
    private final String spotifyId;
    private final String genre;

    // Constructor, getters, and setters
    public Song(String lastFmUrl, String track, String artist, List<String> seeds, int numberOfEmotionTags,
                double valenceTags, double arousalTags, double dominanceTags, String mbid, String spotifyId, String genre) {
        this.lastFmUrl = lastFmUrl;
        this.track = track;
        this.artist = artist;
        this.seeds = seeds;
        this.numberOfEmotionTags = numberOfEmotionTags;
        this.valenceTags = valenceTags;
        this.arousalTags = arousalTags;
        this.dominanceTags = dominanceTags;
        this.mbid = mbid;
        this.spotifyId = spotifyId;
        this.genre = genre;
    }

}
