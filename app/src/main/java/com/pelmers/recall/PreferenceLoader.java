package com.pelmers.recall;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

/**
 * Load preferences from preferences file.
 */
public class PreferenceLoader {
    /** Singleton instance. */
    private static PreferenceLoader instance;
    /** Filename for preferences saving. */
    private final static String FILENAME = "PREFS";
    /** App context. */
    private Context context;

    // Private constructor enforces singleton.
    private PreferenceLoader() {
    }

    /**
     * Return an instance of PreferenceLoader for given context.
     */
    public static PreferenceLoader getInstance(Context context) {
        if (instance == null)
            instance = new PreferenceLoader();
        instance.context = context;
        return instance;
    }

    /**
     * Save preferences to a file.
     */
    protected void savePreferences(Preferences prefs) {
        try {
            ObjectIO.saveObject(prefs, FILENAME, context);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving preferences", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reload preferences from the file. If unable to, return defaults.
     */
    protected Preferences loadPreferences() {
        try {
            return (Preferences) ObjectIO.loadObject(FILENAME, context);
        } catch (IOException | ClassNotFoundException e) {
            return new Preferences();
        }
    }
}
