package com.pelmers.recall;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Handle methods for saving and loading serializable objects.
 */
public class ObjectIO {
    public static void saveObject(Object object, String filename, Context context) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
        objectStream.writeObject(object);
        objectStream.close();
        outputStream.close();
    }

    public static Object loadObject(String filename, Context context) throws IOException, ClassNotFoundException {
        FileInputStream inputStream = context.openFileInput(filename);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        Object object = objectStream.readObject();
        objectStream.close();
        inputStream.close();
        return object;
    }
}