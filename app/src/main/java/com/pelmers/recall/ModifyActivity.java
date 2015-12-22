package com.pelmers.recall;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.pelmers.recall.MainActivity.launchActivity;


public class ModifyActivity extends ActionBarActivity {

    /** Position of the note being modified in the list of notes. */
    private int position;
    private NotesLoader loader;
    private List<RecallNote> notes;
    private ModifyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(MainActivity.THEME_COLOR));
            bar.setDisplayHomeAsUpEnabled(true);
        }

        loader = NotesLoader.getInstance(this);
        notes = loader.loadNotes();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = RecallNote.findByID(notes, (String) extras.get("_id"));
            if (position == -1)
                finish();
        }

        // attach listeners to the buttons
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallNote> notes = loader.loadNotes();
                notes.get(position).cancelBroadcast(getBaseContext());
                notes.remove(position);
                loader.saveNotes(notes);
                finish();
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallNote> notes = loader.loadNotes();
                RecallNote oldThing = notes.get(position);
                oldThing.cancelBroadcast(getBaseContext());
                notes.set(position, new RecallNote(oldThing.getKeywords(), oldThing.getDescription(), getBaseContext()));
                setTimes(notes.get(position));
                loader.saveNotes(notes);
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallNote> notes = loader.loadNotes();
                String key = ((EditText) findViewById(R.id.key_text)).getText().toString();
                String desc = ((EditText) findViewById(R.id.description_text)).getText().toString();
                if (key.length() != 0 || desc.length() != 0) {
                    notes.get(position).setKeywords(key);
                    notes.get(position).setDescription(desc);
                    loader.saveNotes(notes);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Key or description not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RecallNote item = notes.get(position);
        if (!item.isViewed()) {
            item.setViewed(true);
            // clear any notification
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(item.getAlarmID());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Makes sure loader always has valid reference to a context
        loader = NotesLoader.getInstance(this);
        notes = loader.loadNotes();
        initFields();
        receiver = new ModifyReceiver();
        IntentFilter filter = new IntentFilter(RecallNote.ACTION);
        filter.setPriority(100);
        registerReceiver(receiver, filter);
    }

    /**
     * Set the text of all the description fields.
     */
    private void initFields() {
        EditText keyText = (EditText) findViewById(R.id.key_text);
        EditText descText = (EditText) findViewById(R.id.description_text);
        keyText.setText(notes.get(position).getKeywords());
        descText.setText(notes.get(position).getDescription());
        setTimes(notes.get(position));
    }

    /**
     * Update the time fields in the view to the values stored in the note.
     */
    private void setTimes(RecallNote note) {
        // set fields related to number of times reminded and the next reminder time
        TextView numReminders = (TextView) findViewById(R.id.times_reminded_text);
        numReminders.setText(getString(R.string.times_reminded) + " " + note.getTimesReminded());
        TextView nextDate = (TextView) findViewById(R.id.next_reminder_text);
        nextDate.setText(getString(R.string.next_reminder) + " " + note.getNextReminder());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            launchActivity(this, SettingsActivity.class);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Receiver for our alarm broadcasts: if it's for the current note then update the time fields.
     */
    private class ModifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Triple<RecallNote, Integer, List<RecallNote>> triple = AlarmReceiver.incrementID(intent.getStringExtra("_id"), context, true);
            abortBroadcast();
            if (triple != null && triple.second == position) {
                setTimes(triple.first);
            }
        }
    }
}
