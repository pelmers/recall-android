package com.pelmers.recall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * The main activity for the app, the first one the user sees.
 */
public class MainActivity extends ActionBarActivity {

    protected static final int THEME_COLOR = Color.rgb(70, 183, 255);

    private List<RecallNote> notes;
    private RecallAdapter mainAdapter;
    private NotesLoader loader;
    private ListView mainListView;
    private BroadcastReceiver receiver;

    /**
     * Launch an activity with "" _id field.
     */
    public static void launchActivity(Context ctx, Class<?> activity) {
        launchActivity(ctx, activity, "");
    }

    /**
     * Launch an activity with given _id field.
     */
    public static void launchActivity(Context ctx, Class<?> activity, String _id) {
        Intent intent = new Intent(ctx, activity);
        intent.putExtra("_id", _id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        loader.saveNotes(notes);
    }

    /**
     * Reload notes from persistent storage and update the list view adapter.
     */
    private void updateAdapter() {
        notes = loader.loadNotes();
        Collections.sort(notes);
        mainAdapter = new RecallAdapter(getApplicationContext(), R.layout.note, android.R.id.text1, notes);
        mainListView.setAdapter(mainAdapter);
        loader.saveNotes(notes);
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
        loader = NotesLoader.getInstance(this);
        // if we somehow skipped over something, fast-forward it
        Date now = new Date();
        notes = loader.loadNotes();
        for (RecallNote t : notes) {
            while (t.getNextReminder().compareTo(now) < 0) {
                t.incrementReminder(this);
                Log.d("Main", "forwarding something: " + t);
            }
        }
        loader.saveNotes(notes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            launchActivity(this, SettingsActivity.class);
            return true;
        } else if (id == R.id.action_add) {
            // start an add activity
            launchActivity(this, AddActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
