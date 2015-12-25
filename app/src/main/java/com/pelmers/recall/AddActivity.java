package com.pelmers.recall;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import static com.pelmers.recall.MainActivity.handleMenuBarClick;
import static com.pelmers.recall.NotesLoader.loadNotes;
import static com.pelmers.recall.NotesLoader.saveNotes;

/**
 * The activity for the screen to add a new note.
 */
public class AddActivity extends AppCompatActivity {

    /**
     * Instantiate the view and set a listener for the save button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(MainActivity.THEME_COLOR));
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallNote> notes = loadNotes(AddActivity.this);
                String key = ((EditText) findViewById(R.id.key_text)).getText().toString();
                String desc = ((EditText) findViewById(R.id.description_text)).getText().toString();
                // only make it if at least one is nonempty
                if (key.length() != 0 || desc.length() != 0) {
                    notes.add(new RecallNote(key, desc, getBaseContext()));
                    saveNotes(AddActivity.this, notes);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Key or description not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Inflate options into the action bar at the top.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    /**
     * Handle clicks on the action bar at the top.
     */
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
}
