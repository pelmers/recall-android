package com.pelmers.recall;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    protected static final int THEME_COLOR = Color.rgb(70, 183, 255);

    private List<RecallThing> things;
    private ArrayAdapter<RecallThing> mainAdapter;
    private ThingPersistence loader;

    @Override
    protected void onPause() {
        super.onPause();
        loader.saveThings(things);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load things from persistent storage
        things = loader.loadThings();
        final ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mainAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.thing, android.R.id.text1, things);
        mainListView.setAdapter(mainAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: launch activity to view this item
                Intent viewIntent = new Intent(getBaseContext(), ViewActivity.class);
                viewIntent.putExtra("position", position);
                startActivity(viewIntent);
                mainAdapter.notifyDataSetChanged();
            }
        });
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: launch activity to modify this item
                Intent modifyIntent = new Intent(getBaseContext(), ModifyActivity.class);
                modifyIntent.putExtra("position", position);
                startActivity(modifyIntent);
                mainAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(THEME_COLOR));
        loader = new ThingPersistence(this);
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
            return true;
        } else if (id == R.id.action_add) {
            // start an add activity
            Intent addIntent = new Intent(getBaseContext(), AddActivity.class);
            startActivity(addIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
