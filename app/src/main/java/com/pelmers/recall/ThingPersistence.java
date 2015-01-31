package com.pelmers.recall;

import android.app.Activity;
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
 * Handle loading and saving things to some file.
 */
public class ThingPersistence {
    private static final String FILENAME = "THINGS";

    protected void saveThings(List<RecallThing> things, Activity activity) {
        // save things to persistent storage
        try {
            FileOutputStream outputStream = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            objectStream.writeObject(things);
            objectStream.close(); outputStream.close();
            Toast.makeText(activity, "Recall saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error saving lists", Toast.LENGTH_SHORT).show();
        }
    }

    protected List<RecallThing> loadThings(Activity activity) {
        List<RecallThing> things;
        try {
            FileInputStream inputStream = activity.openFileInput(FILENAME);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            //noinspection unchecked
            things = (List<RecallThing>) objectStream.readObject();
            objectStream.close(); inputStream.close();
        } catch (IOException e) {
            // assume that if we can't load that is because we haven't saved yet
            things = new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            things = new ArrayList<>();
            Toast.makeText(activity, "Error loading things", Toast.LENGTH_SHORT).show();
        }
        return things;
    }
}
