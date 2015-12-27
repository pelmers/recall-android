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
import java.util.Collection;
import java.util.List;

import static com.pelmers.recall.NotesLoader.loadNotes;
import static com.pelmers.recall.NotesLoader.saveNotes;

/**
 * Receive our alarm broadcasts.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * Increment the reminder for the note of given ID, and return the note and its position.
     * If no note with given id found, return null.
     */
    protected static Triple<RecallNote, Integer, List<RecallNote>> incrementID(String id, Context ctx, boolean viewed) {
        if (id == null)
            return null;
        Log.d("id", id);
        List<RecallNote> notes = loadNotes(ctx);
        int position = RecallNote.findByID(notes, id);
        if (position == -1)
            return null;
        RecallNote note = notes.get(position);
        Log.d("recv", note.toString());
        note.incrementReminder(ctx);
        note.setViewed(viewed);
        saveNotes(ctx, notes);
        return new Triple<>(notes.get(position), position, notes);
    }

    /**
     * Return a triple of notification title, notification text, and list of all un-viewed notes,
     * null if no note is un-viewed.
     */
    private static Triple<String, String, List<RecallNote>> getNotificationInfo(List<RecallNote> notes) {
        List<String> keywords = new ArrayList<>();
        List<RecallNote> unread = new ArrayList<>();
        for (RecallNote note : notes) {
            if (!note.isViewed()) {
                keywords.add(note.getKeywords());
                unread.add(note);
            }
        }
        if (keywords.size() == 0)
            return null;
        String title = (keywords.size() == 1) ? keywords.get(0) :
                String.format("%d unviewed reminders.", keywords.size());
        return new Triple<>(title, joinStrings(keywords, ", "), unread);
    }

    /**
     * Return a notification for the given list of notes and context.
     */
    protected static NotificationCompat.Builder buildNotification(Context context, List<RecallNote> notes) {
        Triple<String, String, List<RecallNote>> notificationInfo = getNotificationInfo(notes);
        if (notificationInfo == null)
            return null;
        final List<RecallNote> unread = notificationInfo.third;
        // Create a notification with the info we found.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(notificationInfo.first)
                        .setContentText(notificationInfo.second)
                        .setAutoCancel(true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, MainActivity.class));
        // If there's only one unread activity, clicking the notification can go straight to it.
        if (unread.size() == 1) {
            stackBuilder.addNextIntent(new Intent(context, ViewActivity.class) {{
                putExtra("_id", unread.get(0).getId().toString());
            }});
        }
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                unread.get(0).getAlarmID(),
                PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder;
    }

    /**
     * Handle for when we receive our alarm.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("_id");

        Triple<RecallNote, Integer, List<RecallNote>> triple = incrementID(id, context, false);
        if (triple == null) {
            Log.d("recv", "Empty match, skipping notify");
            return;
        }
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = AlarmReceiver.buildNotification(context, triple.third);
        // Overwrite any existing alarm
        mNotificationManager.cancel(0);
        if (builder != null)
            mNotificationManager.notify(0, builder.build());
    }

    /**
     * Join a collection of strings with given delimiter between them.
     */
    private static String joinStrings(Collection<String> strings, String delimiter) {
        StringBuilder out = new StringBuilder();
        for (String s : strings)
            out.append(s).append(delimiter);
        // delete last usage of delimiter
        out.delete(out.length() - delimiter.length(), out.length());
        return out.toString();
    }
}
