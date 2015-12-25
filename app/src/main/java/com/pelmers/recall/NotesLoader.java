package com.pelmers.recall;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton handle loading and saving notes to some file.
 */
public class NotesLoader {
    private static final String FILENAME = "THINGS";

    /**
     * Save a list of notes.
     */
    protected static void saveNotes(Context context, List<RecallNote> notes) {
        // save notes to persistent storage
        try {
            ObjectIO.saveObject(notes, FILENAME, context);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving notes", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load a previously saved list of notes.
     */
    protected static List<RecallNote> loadNotes(Context context) {
        List<RecallNote> notes;
        try {
            //noinspection unchecked
            notes = (List<RecallNote>) ObjectIO.loadObject(FILENAME, context);
        } catch (IOException e) {
            // assume that if we can't load that is because we haven't saved yet
            notes = new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            notes = new ArrayList<>();
            Toast.makeText(context, "Error loading notes", Toast.LENGTH_SHORT).show();
        }
        return notes;
    }

}
