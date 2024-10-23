package edu.raf.diplomski.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.NonNull;

public class SharedPrefsUtil {

    private static final String PREFS_NAME = "MindTunes_prefs";
    private static final String GENRES = "genres";
    private static final String INITIALIZED = "initialized";

    public static void setGenres(@NonNull Context context, List<String> genres){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> set = new HashSet<>(genres);
        editor.putStringSet(GENRES, set);
        editor.apply();
    }

    public static void setGenres(@NonNull Context context, Set<String> genres){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putStringSet(GENRES, genres);
        editor.apply();
    }

    public static Set<String> getGenres(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(GENRES, new HashSet<>()); // Default is an empty set if nothing found
        return set;
    }

    public static void setInitialized(@NonNull Context context, boolean initialized){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(INITIALIZED, initialized);
        editor.apply();
    }

    public static boolean isInitialized(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(INITIALIZED, false);
    }

}
