package com.pelmers.recall;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

/**
 * Load preferences from preferences file.
 */
public class PreferenceLoader {
    private static PreferenceLoader instance;
    private final static String FILENAME = "PREFS";
    private Context context;

    private PreferenceLoader() {
    }

    public static PreferenceLoader getInstance(Context context) {
        if (instance == null)
            instance = new PreferenceLoader();
        instance.context = context;
        return instance;
    }

    protected void savePreferences(Preferences prefs) {
        try {
            ObjectIO.saveObject(prefs, FILENAME, context);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving preferences", Toast.LENGTH_SHORT).show();
        }
    }

    protected Preferences loadPreferences() {
        try {
            return (Preferences) ObjectIO.loadObject(FILENAME, context);
        } catch (IOException | ClassNotFoundException e) {
            return new Preferences(RecallThing.DEFAULT_FIRST_REMINDER, RecallThing.DEFAULT_REPETITION_SPACING);
        }
    }
}
