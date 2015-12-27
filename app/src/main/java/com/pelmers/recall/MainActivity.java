package com.pelmers.recall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.pelmers.recall.ContextUtils.handleMenuBarClick;
import static com.pelmers.recall.ContextUtils.launchActivity;
import static com.pelmers.recall.NotesLoader.loadNotes;
import static com.pelmers.recall.NotesLoader.saveNotes;


/**
 * The main activity for the app, the first one the user sees.
 */
public class MainActivity extends AppCompatActivity {

    protected static final int THEME_COLOR = Color.rgb(70, 183, 255);

    private List<RecallNote> notes;
    private RecallAdapter mainAdapter;
    private ListView mainListView;
    private BroadcastReceiver receiver;

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        saveNotes(this, notes);
    }

    /**
     * Reload notes from persistent storage and update the list view adapter.
     */
    private void updateAdapter() {
        notes = loadNotes(this);
        Collections.sort(notes);
        mainAdapter = new RecallAdapter(getApplicationContext(), R.layout.note, android.R.id.text1, notes);
        mainListView.setAdapter(mainAdapter);
        saveNotes(this, notes);
    }

    /**
     * Reactivate the view by reloading the list adapter and setting click listeners.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mainListView = (ListView) findViewById(R.id.mainListView);
        updateAdapter();
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchActivity(getBaseContext(), ViewActivity.class, notes.get(position).getId().toString());
                mainAdapter.notifyDataSetChanged();
            }
        });
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                launchActivity(getBaseContext(), ModifyActivity.class, notes.get(position).getId().toString());
                mainAdapter.notifyDataSetChanged();
                return true;
            }
        });
        receiver = new MainReceiver();
        IntentFilter filter = new IntentFilter(RecallNote.ACTION);
        filter.setPriority(100);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(THEME_COLOR));
        // if we somehow skipped over something, fast-forward it
        Date now = new Date();
        notes = loadNotes(this);
        for (RecallNote t : notes) {
            while (t.getNextReminder().compareTo(now) < 0) {
                t.incrementReminder(this);
                Log.d("Main", "forwarding something: " + t);
            }
        }
        saveNotes(this, notes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleMenuBarClick(this, item.getItemId()) || super.onOptionsItemSelected(item);
    }

    /**
     * This receiver catches our alarms if we're in the main view.
     */
    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AlarmReceiver.incrementID(intent.getStringExtra("_id"), context, false);
            updateAdapter();
            abortBroadcast();
        }
    }
}
