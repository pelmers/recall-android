package com.pelmers.recall;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.pelmers.recall.MainActivity.launchActivity;

public class ViewActivity extends ActionBarActivity {

    private int position;
    private BroadcastReceiver receiver;
    private boolean feedbackRemoved = false;

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
        NotePersistence loader = NotePersistence.getInstance(this);
        List<RecallNote> notes = loader.loadThings();
        if (extras != null) {
            position = RecallNote.findByID(notes, (String) extras.get("_id"));
            if (position == -1) {
                Log.d("???", "Position not found in notes?");
                finish();
            }
        } else {
            Log.d("???", "Position not found in intent bundle?");
        }

        final RecallNote item = notes.get(position);
        // set the text for key and description
        TextView keyText = (TextView) findViewById(R.id.key_text);
        TextView descText = (TextView) findViewById(R.id.description_text);
        keyText.setText(item.getKeywords());
        descText.setText(item.getDescription());
        descText.setTextColor(Color.BLACK);
        setTimes(notes.get(position));
        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!item.isViewed()) {
            item.setViewed(true);
            // reset notifications if necessary
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = AlarmReceiver.buildNotification(this, notes);
            if (builder != null)
                mNotificationManager.notify(0, builder.build());
            else
                mNotificationManager.cancel(0);
            // show an alert dialog to ask them for input
            TextInputAlertDialog.showInputAlertDialog(this, "Retype your keywords", "OK", new TextInputAlertDialog.TextInputClickListener() {
                @Override
                public void onClick(DialogInterface dialog, String text) {
                    if (text.equals(item.getKeywords())) {
                        Toast.makeText(getBaseContext(), "Correct", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // already viewed, hide feedback buttons
            RadioGroup feedbackGroup = (RadioGroup) findViewById(R.id.feedback_group);
            TextView feedbackText = (TextView) findViewById(R.id.feedback_text);
            feedbackGroup.removeAllViews();
            feedbackText.setText("");
            feedbackRemoved = true;
        }
        loader.saveThings(notes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new ViewReceiver();
        IntentFilter filter = new IntentFilter(RecallNote.ACTION);
        filter.setPriority(100);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        if (!feedbackRemoved) {
            // view the feedback
            RadioGroup feedbackGroup = (RadioGroup) findViewById(R.id.feedback_group);
            TextView feedbackText = (TextView) findViewById(R.id.feedback_text);
            int selected = feedbackGroup.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton) feedbackGroup.findViewById(selected);
            PreferenceLoader preferenceLoader = PreferenceLoader.getInstance(this);
            Preferences prefs = preferenceLoader.loadPreferences();
            if (selected == R.id.feedback_early) {
                prefs.setFirstReminder((long) (prefs.getFirstReminder() * 1.1));
            } else if (selected == R.id.feedback_late) {
                prefs.setFirstReminder((long) (prefs.getFirstReminder() / 1.1));
            }
            preferenceLoader.savePreferences(prefs);
            feedbackGroup.removeAllViews();
            feedbackText.setText("");
            Toast.makeText(this, "Feedback saved: " + rb.getText(), Toast.LENGTH_SHORT).show();
            feedbackRemoved = true;
        }
    }

    private void setTimes(RecallNote note) {
        // set fields related to number of times reminded and the next reminder time
        TextView numReminders = (TextView) findViewById(R.id.times_reminded_text);
        numReminders.setText(getString(R.string.times_reminded) + " " + note.getTimesReminded());
        TextView nextDate = (TextView) findViewById(R.id.next_reminder_text);
        nextDate.setText(getString(R.string.next_reminder) + " " + RecallNote.formatDate(note.getNextReminder()));
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
            launchActivity(this, SettingsActivity.class);
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
            RecallNote.NotePositionTuple tuple = AlarmReceiver.incrementID(intent.getStringExtra("_id"), context, true);
            abortBroadcast();
            if (tuple == null)
                return;
            if (tuple.position == position) {
                setTimes(tuple.note);
            }
        }
    }
}
