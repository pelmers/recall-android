package com.pelmers.recall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Receive alarm broadcasts.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("thingID");
        Log.d("id", id);
        ThingPersistence loader = ThingPersistence.getInstance(context);
        List<RecallThing> things = loader.loadThings();
        for (RecallThing thing : things) {
            if (thing.getId().toString().equals(id)) {
                Log.d("recv", thing.toString());
                thing.incrementReminder(context);
                break;
            }
        }
        loader.saveThings(things);
    }
}
