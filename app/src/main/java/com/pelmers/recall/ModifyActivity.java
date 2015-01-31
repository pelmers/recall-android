package com.pelmers.recall;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class ModifyActivity extends ActionBarActivity {

    // position of this thing in the list of things
    private int position = 0;
    private ThingPersistence loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(MainActivity.THEME_COLOR));
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = (int) extras.get("position");
        } else {
            System.out.println("Position not found in intent bundle?");
        }

        // attach listeners to the buttons
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallThing> things = loader.loadThings();
                things.remove(position);
                loader.saveThings(things);
                finish();
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallThing> things = loader.loadThings();
                RecallThing oldThing = things.get(position);
                things.set(position, new RecallThing(oldThing.getKeywords(), oldThing.getDescription()));
                setTimes(things);
                loader.saveThings(things);
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallThing> things = loader.loadThings();
                String key = ((EditText) findViewById(R.id.key_text)).getText().toString();
                String desc = ((EditText) findViewById(R.id.description_text)).getText().toString();
                if (key.length() != 0 || desc.length() != 0) {
                    things.get(position).setKeywords(key);
                    things.get(position).setDescription(desc);
                    loader.saveThings(things);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Key or description not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // makes sure loader always has valid reference to a context
        loader = new ThingPersistence(this);
        initFields();
    }

    private void initFields() {
        // set text of all the fields
        EditText keyText = (EditText) findViewById(R.id.key_text);
        EditText descText = (EditText) findViewById(R.id.description_text);
        List<RecallThing> things = loader.loadThings();
        keyText.setText(things.get(position).getKeywords());
        descText.setText(things.get(position).getDescription());
        setTimes(things);
    }

    private void setTimes(List<RecallThing> things) {
        // set fields related to number of times reminded and the next reminder time
        TextView numReminders = (TextView) findViewById(R.id.times_reminded_text);
        numReminders.setText(getString(R.string.times_reminded) + " " + things.get(position).getTimesReminded());
        TextView nextDate = (TextView) findViewById(R.id.next_reminder_text);
        nextDate.setText(getString(R.string.next_reminder) + " " + things.get(position).getNextReminder());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify, menu);
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
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
