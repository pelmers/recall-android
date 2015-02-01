package com.pelmers.recall;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;


public class AddActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("Add");
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(MainActivity.THEME_COLOR));
            bar.setDisplayHomeAsUpEnabled(true);
        }

        final ThingPersistence loader = ThingPersistence.getInstance(this);
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RecallThing> things = loader.loadThings();
                String key = ((EditText) findViewById(R.id.key_text)).getText().toString();
                String desc = ((EditText) findViewById(R.id.description_text)).getText().toString();
                // only make it if at least one is nonempty
                if (key.length() != 0 || desc.length() != 0) {
                    things.add(new RecallThing(key, desc, getBaseContext()));
                    loader.saveThings(things);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Key or description not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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
