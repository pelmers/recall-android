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

import java.util.ArrayList;
import java.util.Collection;
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
        int position = RecallThing.findByID(things, id);
        if (position == -1)
            return null;
        RecallThing thing = things.get(position);
        Log.d("recv", thing.toString());
        thing.incrementReminder(ctx);
        thing.setViewed(viewed);
        loader.saveThings(things);
        return new RecallThing.ThingPositionTuple(things.get(position), position, things);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("_id");

        RecallThing.ThingPositionTuple tuple = incrementID(id, context, false);
        if (tuple == null) {
            Log.d("recv", "Empty match, skipping notify");
            return;
        }

        List<String> keywords = new ArrayList<>();
        for (RecallThing thing: tuple.things) {
            if (!thing.isViewed())
                keywords.add(thing.getKeywords());
        }
        String title = tuple.thing.getKeywords();
        String text = tuple.thing.getDescription();
        if (keywords.size() > 1) {
            title = String.format("%d unviewed reminders.", keywords.size());
            text = joinStrings(keywords, ", ");
        }
        // consolidate notifications
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent parent = new Intent(context, MainActivity.class);
        stackBuilder.addNextIntent(parent);
        if (keywords.size() <= 1) {
            Intent result = new Intent(context, ViewActivity.class);
            result.putExtra("_id", tuple.thing.getId().toString());
            stackBuilder.addNextIntent(result);
        }
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        tuple.thing.getAlarmID(),
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // overwrite any existing alarm
        mNotificationManager.cancel(0);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private static String joinStrings(List<String> strings, String delimiter) {
        StringBuilder out = new StringBuilder();
        for (String s : strings)
            out.append(s).append(delimiter);
        // delete last usage of delimiter
        out.delete(out.length() - delimiter.length(), out.length());
        return out.toString();
    }
}
