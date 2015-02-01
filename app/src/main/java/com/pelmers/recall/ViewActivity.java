package com.pelmers.recall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class ViewActivity extends ActionBarActivity {

    private int position;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(MainActivity.THEME_COLOR));
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = (int) extras.get("position");
        } else {
            Log.d("???", "Position not found in intent bundle?");
        }

        ThingPersistence loader = ThingPersistence.getInstance(this);
        List<RecallThing> things = loader.loadThings();
        RecallThing item = things.get(position);
        // set the text for key and description
        TextView keyText = (TextView) findViewById(R.id.key_text);
        TextView descText = (TextView) findViewById(R.id.description_text);
        keyText.setText(item.getKeywords());
        descText.setText(item.getDescription());
        setTimes(things.get(position));
        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!item.isViewed()) {
            item.setViewed(true);
        } else {
            // already viewed, hide feedback buttons
            RadioGroup feedbackGroup = (RadioGroup) findViewById(R.id.feedback_group);
            TextView feedbackText = (TextView) findViewById(R.id.feedback_text);
            feedbackGroup.removeAllViews();
            feedbackText.setText("");
        }
        loader.saveThings(things);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new ViewReceiver();
        IntentFilter filter = new IntentFilter(RecallThing.ACTION);
        filter.setPriority(100);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void setTimes(RecallThing thing) {
        // set fields related to number of times reminded and the next reminder time
        TextView numReminders = (TextView) findViewById(R.id.times_reminded_text);
        numReminders.setText(getString(R.string.times_reminded) + " " + thing.getTimesReminded());
        TextView nextDate = (TextView) findViewById(R.id.next_reminder_text);
        nextDate.setText(getString(R.string.next_reminder) + " " + thing.getNextReminder());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
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

    private class ViewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            RecallThing.ThingPositionTuple tuple = AlarmReceiver.incrementID(intent.getStringExtra("thingID"), context, true);
            abortBroadcast();
            if (tuple == null)
                return;
            if (tuple.position == position) {
                setTimes(tuple.thing);
            }
        }
    }
}
