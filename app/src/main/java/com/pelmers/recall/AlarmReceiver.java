package com.pelmers.recall;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Receive alarm broadcasts.
 */
public class AlarmReceiver extends BroadcastReceiver {

    protected static RecallNote.NotePositionTuple incrementID(String id, Context ctx, boolean viewed) {
        // increment the reminder for this note id
        if (id == null)
            return null;
        Log.d("id", id);
        NotePersistence loader = NotePersistence.getInstance(ctx);
        List<RecallNote> things = loader.loadThings();
        int position = RecallNote.findByID(things, id);
        if (position == -1)
            return null;
        RecallNote thing = things.get(position);
        Log.d("recv", thing.toString());
        thing.incrementReminder(ctx);
        thing.setViewed(viewed);
        loader.saveThings(things);
        return new RecallNote.NotePositionTuple(things.get(position), position, things);
    }

    protected static NotificationCompat.Builder buildNotification(Context context, List<RecallNote> things) {
        List<String> keywords = new ArrayList<>();
        RecallNote theThing = null;
        for (RecallNote thing: things) {
            if (!thing.isViewed()) {
                keywords.add(thing.getKeywords());
                theThing = thing;
            }
        }
        if (theThing == null)
            return null;
        String title = theThing.getKeywords();
        String text = theThing.getDescription();
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
        if (keywords.size() == 1) {
            Intent result = new Intent(context, ViewActivity.class);
            result.putExtra("_id", theThing.getId().toString());
            stackBuilder.addNextIntent(result);
        }
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        theThing.getAlarmID(),
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("_id");

        RecallNote.NotePositionTuple tuple = incrementID(id, context, false);
        if (tuple == null) {
            Log.d("recv", "Empty match, skipping notify");
            return;
        }
       NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = AlarmReceiver.buildNotification(context, tuple.notes);
        // overwrite any existing alarm
        mNotificationManager.cancel(0);
        if (builder != null)
            mNotificationManager.notify(0, builder.build());
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
