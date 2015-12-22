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


public class MainActivity extends ActionBarActivity {

    protected static final int THEME_COLOR = Color.rgb(70, 183, 255);

    private List<RecallNote> notes;
    private RecallAdapter mainAdapter;
    private NotePersistence loader;
    private ListView mainListView;
    private BroadcastReceiver receiver;

    public static void launchActivity(Context ctx, Class<?> activity) {
        launchActivity(ctx, activity, "");
    }

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
        loader.saveThings(notes);
    }

    private void updateAdapter() {
        // load notes from persistent storage
        notes = loader.loadThings();
        Collections.sort(notes);
        mainAdapter = new RecallAdapter(getApplicationContext(), R.layout.note, android.R.id.text1, notes);
        mainListView.setAdapter(mainAdapter);
        loader.saveThings(notes);
    }

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
        loader = NotePersistence.getInstance(this);
        // if we somehow skipped over something, fast-forward it
        Date now = new Date();
        notes = loader.loadThings();
        for (RecallNote t : notes) {
            while (t.getNextReminder().compareTo(now) < 0) {
                t.incrementReminder(this);
                Log.d("Main", "forwarding something: " + t);
            }
        }
        loader.saveThings(notes);
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
            launchActivity(this, SettingsActivity.class);
            return true;
        } else if (id == R.id.action_add) {
            // start an add activity
            launchActivity(this, AddActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AlarmReceiver.incrementID(intent.getStringExtra("_id"), context, false);
            updateAdapter();
            abortBroadcast();
        }
    }
}
