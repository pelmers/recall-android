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

    // position of this note in the list of notes
    private int position = 0;
    private NotePersistence loader;
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

        loader = NotePersistence.getInstance(this);
        notes = loader.loadThings();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = RecallNote.findByID(notes, (String) extras.get("_id"));
            if (position == -1)
                finish();
        } else {
            System.out.println("Position not found in intent bundle?");
        }

        // attach listeners to the buttons
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallNote> notes = loader.loadThings();
                notes.get(position).cancelBroadcast(getBaseContext());
                notes.remove(position);
                loader.saveThings(notes);
                finish();
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallNote> notes = loader.loadThings();
                RecallNote oldThing = notes.get(position);
                oldThing.cancelBroadcast(getBaseContext());
                notes.set(position, new RecallNote(oldThing.getKeywords(), oldThing.getDescription(), getBaseContext()));
                setTimes(notes);
                loader.saveThings(notes);
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallNote> notes = loader.loadThings();
                String key = ((EditText) findViewById(R.id.key_text)).getText().toString();
                String desc = ((EditText) findViewById(R.id.description_text)).getText().toString();
                if (key.length() != 0 || desc.length() != 0) {
                    notes.get(position).setKeywords(key);
                    notes.get(position).setDescription(desc);
                    loader.saveThings(notes);
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
        // makes sure loader always has valid reference to a context
        loader = NotePersistence.getInstance(this);
        notes = loader.loadThings();
        initFields();
        receiver = new ModifyReceiver();
        IntentFilter filter = new IntentFilter(RecallNote.ACTION);
        filter.setPriority(100);
        registerReceiver(receiver, filter);
    }

    private void initFields() {
        // set text of all the fields
        EditText keyText = (EditText) findViewById(R.id.key_text);
        EditText descText = (EditText) findViewById(R.id.description_text);
        keyText.setText(this.notes.get(position).getKeywords());
        descText.setText(this.notes.get(position).getDescription());
        setTimes(this.notes);
    }

    private void setTimes(List<RecallNote> notes) {
        // set fields related to number of times reminded and the next reminder time
        TextView numReminders = (TextView) findViewById(R.id.times_reminded_text);
        numReminders.setText(getString(R.string.times_reminded) + " " + notes.get(position).getTimesReminded());
        TextView nextDate = (TextView) findViewById(R.id.next_reminder_text);
        nextDate.setText(getString(R.string.next_reminder) + " " + notes.get(position).getNextReminder());
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
            launchActivity(this, SettingsActivity.class);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ModifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            RecallNote.NotePositionTuple tuple = AlarmReceiver.incrementID(intent.getStringExtra("_id"), context, true);
            abortBroadcast();
            if (tuple == null)
                return;
            if (tuple.position == position) {
                setTimes(tuple.notes);
            }
        }
    }
}
