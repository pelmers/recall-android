package com.pelmers.recall;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.pelmers.recall.MainActivity.handleMenuBarClick;


/**
 * Activity for changing app settings.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private EditText firstReminderText;
    private EditText scalingFactorText;
    private CheckBox checkBoxConfirmKeywords;
    private List<String> timesList;
    private ArrayAdapter<String> timesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timesList = new ArrayList<>();
        setContentView(R.layout.activity_settings);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(MainActivity.THEME_COLOR));
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneButton.bringToFront();
        firstReminderText = (EditText) findViewById(R.id.first_reminder_field);
        firstReminderText.addTextChangedListener(new SettingsTextWatcher());
        scalingFactorText = (EditText) findViewById(R.id.scaling_factor_field);
        scalingFactorText.addTextChangedListener(new SettingsTextWatcher());
        checkBoxConfirmKeywords = (CheckBox) findViewById(R.id.checkbox_confirm_keywords);
        ListView timesListView = (ListView) findViewById(R.id.reminder_times_list);
        timesListAdapter = new ArrayAdapter<>(this, R.layout.note, android.R.id.text1, timesList);
        timesListView.setAdapter(timesListAdapter);
    }

    private void updateTimesList() {
        // clear the current list of times and put in the first 10
        timesList.clear();
        try {
            long first = Long.parseLong(firstReminderText.getText().toString());
            double scale = Double.parseDouble(scalingFactorText.getText().toString());
            for (int i = 0; i < 10; i++) {
                timesList.add(prettySeconds((long) (first * Math.pow(scale, i))));
            }
        } catch (NumberFormatException exc) {
            Log.d(TAG, "Format exception on update" + exc.toString());
            timesList.add("Number format exception");
        }
        timesListAdapter.notifyDataSetChanged();
    }

    private static String prettySeconds(long seconds) {
        // human readable format seconds
        long weeks = seconds / (604800);
        seconds %= 604800;
        long days = seconds / (86400);
        seconds %= 86400;
        long hours = seconds / (3600);
        seconds %= 3600;
        long minutes = seconds / (60);
        StringBuilder sb = new StringBuilder();
        if (weeks > 0)
            sb.append(weeks).append(" weeks, ");
        if (days > 0)
            sb.append(days).append(" days, ");
        sb.append(String.format("%02d:%02d", hours, minutes));
        return sb.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore the settings from disk
        PreferenceLoader preferenceLoader = PreferenceLoader.getInstance(this);
        Preferences preferences = preferenceLoader.loadPreferences();
        firstReminderText.setText(Long.toString(preferences.getFirstReminder()));
        scalingFactorText.setText(Double.toString(preferences.getExponentBase()));
        checkBoxConfirmKeywords.setChecked(preferences.confirmKeywords());
        updateTimesList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save the settings to disk
        PreferenceLoader preferenceLoader = PreferenceLoader.getInstance(this);
        Preferences preferences = preferenceLoader.loadPreferences();
        try {
            preferences.setFirstReminder(Long.parseLong(firstReminderText.getText().toString()));
            preferences.setExponentBase(Double.parseDouble(scalingFactorText.getText().toString()));
            preferences.setConfirmKeywords(checkBoxConfirmKeywords.isChecked());
            // min the first reminder at 60 seconds, and the exponent at 1
            preferences.setFirstReminder(Math.max(60, preferences.getFirstReminder()));
            preferences.setExponentBase(Math.max(1, preferences.getExponentBase()));
            preferenceLoader.savePreferences(preferences);
        } catch (NumberFormatException ex) {
            Log.d(TAG, "Format exception on update" + ex.toString());
            Toast.makeText(this, "Check number format.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (handleMenuBarClick(this, id)) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SettingsTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            updateTimesList();
        }
    }
}
