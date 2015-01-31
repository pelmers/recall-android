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

    private List<RecallThing> things;
    private ArrayAdapter<RecallThing> mainAdapter;
    private ThingPersistence loader;

    @Override
    protected void onPause() {
        super.onPause();
        loader.saveThings(things, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load things from persistent storage
        things = loader.loadThings(this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(THEME_COLOR));
        loader = new ThingPersistence();
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
