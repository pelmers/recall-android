package com.pelmers.recall;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

/**
 * Load preferences from preferences file.
 */
public final class PreferenceLoader {
    /** Filename for preferences saving. */
    private static final String FILENAME = "PREFS";

    private PreferenceLoader() {}

    /**
     * Save preferences to a file.
     */
    protected static void savePreferences(Context context, Preferences prefs) {
        try {
            ObjectIO.saveObject(prefs, FILENAME, context);
            Toast.makeText(context, "Preferences saved.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving preferences", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reload preferences from the file. If unable to, return defaults.
     */
    protected static Preferences loadPreferences(Context context) {
        try {
            return (Preferences) ObjectIO.loadObject(FILENAME, context);
        } catch (IOException | ClassNotFoundException e) {
            return new Preferences();
        }
    }
}
