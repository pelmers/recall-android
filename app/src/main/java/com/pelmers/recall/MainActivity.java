package com.pelmers.recall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    protected static final int THEME_COLOR = Color.rgb(70, 183, 255);

    private static final String FILENAME = "THINGS";

    private List<RecallThing> things;
    private ArrayAdapter<RecallThing> mainAdapter;

    @Override
    protected void onPause() {
        super.onPause();
        saveThings(things);
    }

    protected void saveThings(List<RecallThing> things) {
        // save things to persistent storage
        try {
            FileOutputStream outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            objectStream.writeObject(things);
            objectStream.close(); outputStream.close();
            Toast.makeText(this, "Recall saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving lists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(THEME_COLOR));
        // load things from persistent storage
        things = loadThings();
        things.add(new RecallThing("Something", "description"));
        final ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mainAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.thing, android.R.id.text1, things);
        mainListView.setAdapter(mainAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // handle click on this position
                things.get(position).incrementReminder();
                mainAdapter.notifyDataSetChanged();
            }
        });
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                things.remove(position);
                mainAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    protected List<RecallThing> loadThings() {
        List<RecallThing> things;
        try {
            FileInputStream inputStream = openFileInput(FILENAME);
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
            Toast.makeText(this, "Error loading things", Toast.LENGTH_SHORT).show();
        }
        return things;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            // start an add activity
            Intent addIntent = new Intent(getBaseContext(), AddActivity.class);
            startActivity(addIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
