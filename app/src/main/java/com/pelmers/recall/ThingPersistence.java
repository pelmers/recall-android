package com.pelmers.recall;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton handle loading and saving things to some file.
 */
public class ThingPersistence {
    private static final String FILENAME = "THINGS";
    private static ThingPersistence instance = null;
    private Context context = null;

    public static ThingPersistence getInstance(Context ctx) {
        if (instance == null)
            instance = new ThingPersistence();
        instance.context = ctx;
        return instance;
    }

    private ThingPersistence() {
    }

    protected void saveThings(List<RecallThing> things) {
        // save things to persistent storage
        try {
            ObjectIO.saveObject(things, FILENAME, context);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving lists", Toast.LENGTH_SHORT).show();
        }
    }

    protected List<RecallThing> loadThings() {
        List<RecallThing> things;
        try {
            //noinspection unchecked
            things = (List<RecallThing>) ObjectIO.loadObject(FILENAME, context);
        } catch (IOException e) {
            // assume that if we can't load that is because we haven't saved yet
            things = new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            things = new ArrayList<>();
            Toast.makeText(context, "Error loading things", Toast.LENGTH_SHORT).show();
        }
        return things;
    }

}
