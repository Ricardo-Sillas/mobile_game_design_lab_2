package edu.utep.cs.cs4381.tappydefender;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class HighScoreRecorder {
    private static final String PREF_FILE = "highScore";
    private static final String PREF_KEY = "fastestTime";
    private static final long DEFAULT_VALUE = Long.MAX_VALUE;
    private static HighScoreRecorder theInstance;
    private Context context;

    private static SharedPreferences prefs;

    private HighScoreRecorder(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        this.context = ctx;
    }

    public void store(long time) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREF_KEY, time);
        editor.commit();
    }

    public static long retrieve() {
        long fastestTime = prefs.getLong(PREF_KEY, DEFAULT_VALUE);
        return fastestTime;
    }

    // Gets time in milliseconds
    static String formatTime(String label, long time) {
        return String.format("%s: %d.%03ds", label, time/1000, time%1000);
    }

    public static HighScoreRecorder instance(@Nullable Context ctx) {
        if (theInstance == null) {
            theInstance = new HighScoreRecorder(ctx);
        }
        return theInstance;
    }
}
