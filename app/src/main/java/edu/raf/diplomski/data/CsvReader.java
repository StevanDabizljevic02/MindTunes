package edu.raf.diplomski.data;

import android.content.Context;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvReader {
    private Context context;

    public CsvReader(Context context) {
        this.context = context;
    }

    public List<Song> loadSongsFromCsv() {
        System.out.println("LOADING SONGS");
        List<Song> songList = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("song_data.csv");
            InputStreamReader isr = new InputStreamReader(is);

            CSVReader csvReader = new CSVReader(isr);

            // Skip the header
            List<String[]> allLines = csvReader.readAll();
            for (int i = 1; i < allLines.size(); i++) { // Skip header row
                String[] tokens = allLines.get(i);

                if (tokens.length < 11) {
                    continue;  // Skip invalid rows with missing data
                }

                // Extract and parse data from each row
                String lastFmUrl = tokens[0];
                String track = tokens[1];
                String artist = tokens[2];
                List<String> seeds = parseSeeds(tokens[3]);
                int numberOfEmotionTags = Integer.parseInt(tokens[4]);
                double valenceTags = Double.parseDouble(tokens[5]);
                double arousalTags = Double.parseDouble(tokens[6]);
                double dominanceTags = Double.parseDouble(tokens[7]);
                String mbid = tokens[8];
                String spotifyId = tokens[9];
                String genre = tokens[10];

                Song song = new Song(lastFmUrl, track, artist, seeds, numberOfEmotionTags, valenceTags,
                        arousalTags, dominanceTags, mbid, spotifyId, genre);
                songList.add(song);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("SONGS LOADED");
        return songList;
    }

    private List<String> parseSeeds(String seedsColumn) {
        // Remove brackets and split by comma
        seedsColumn = seedsColumn.replace("[", "").replace("]", "");
        return Arrays.asList(seedsColumn.split(","));
    }
}

