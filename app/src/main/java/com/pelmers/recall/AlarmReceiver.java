package com.pelmers.recall;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Receive alarm broadcasts.
 */
public class AlarmReceiver extends BroadcastReceiver {

    protected static RecallThing.ThingPositionTuple incrementID(String id, Context ctx, boolean viewed) {
        // increment the reminder for this thing id
        Log.d("id", id);
        ThingPersistence loader = ThingPersistence.getInstance(ctx);
        List<RecallThing> things = loader.loadThings();
        int position = -1;
        for (int i = 0; i < things.size(); i++) {
            RecallThing thing = things.get(i);
            if (thing.getId().toString().equals(id)) {
                Log.d("recv", thing.toString());
                thing.incrementReminder(ctx);
                thing.setViewed(viewed);
                position = i;
                break;
            }
        }
        loader.saveThings(things);
        if (position == -1)
            return null;
        return new RecallThing.ThingPositionTuple(things.get(position), position);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("thingID");

        RecallThing.ThingPositionTuple tuple = incrementID(id, context, false);
        if (tuple == null) {
            Log.d("recv", "Empty match, skipping notify");
            return;
        }
        // pop up a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Recall")
                        .setContentText(tuple.thing.getKeywords())
                        .setAutoCancel(true);
        Intent result = new Intent(context, ViewActivity.class);
        result.putExtra("position", tuple.position);
        Intent parent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(parent);
        stackBuilder.addNextIntent(result);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        tuple.thing.getAlarmID(),
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(tuple.thing.getAlarmID(), mBuilder.build());
    }
}
