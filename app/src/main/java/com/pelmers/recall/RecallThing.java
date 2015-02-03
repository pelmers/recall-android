package com.pelmers.recall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Encapsulate things about recall things.
 */
public class RecallThing implements Serializable, Comparable<RecallThing> {
    // Time until first reminder, in seconds
    protected static long DEFAULT_FIRST_REMINDER = 10;
    // Exponential scaling factor
    protected static double DEFAULT_REPETITION_SPACING = 3;
    protected static final String ACTION = "com.pelmers.recall.BROADCAST";

    private String keywords;
    private String description;
    private Date nextReminder;
    private UUID id;
    private int alarmID;
    private int timesReminded = -1;
    private boolean viewed = true;

    public RecallThing(String key, String description, Context ctx) {
        this.keywords = key;
        this.description = description;
        this.nextReminder = new Date();
        id = UUID.randomUUID();
        alarmID = pickAlarmID();
        incrementReminder(ctx);
    }

    public static int findByID(List<RecallThing> things, String id) {
        // index of thing with given id, else -1
        int position = -1;
        for (int i = 0; i < things.size(); i++) {
            RecallThing thing = things.get(i);
            if (thing.getId().toString().equals(id)) {
                position = i;
                break;
            }
        }
        return position;
    }

    private int pickAlarmID() {
        return (int) (Integer.MAX_VALUE * Math.random());
    }

    public int getTimesReminded() {
        return timesReminded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getNextReminder() {
        return nextReminder;
    }

    public static String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.format("%s %d, %02d:%02d",
                             cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH),
                             cal.get(Calendar.DAY_OF_MONTH),
                             cal.get(Calendar.HOUR_OF_DAY),
                             cal.get(Calendar.MINUTE));
    }

    @Override
    public String toString() {
        return keywords + "\n" + formatDate(nextReminder);
    }

    public void incrementReminder(Context context) {
        // increment the reminder and set an alarm for the next one.
        timesReminded++;
        // load the preferences
        PreferenceLoader preferenceLoader = PreferenceLoader.getInstance(context);
        Preferences prefs = preferenceLoader.loadPreferences();
        // multiply by 1000 to go to milliseconds
        long nextInterval = (long) Math.pow(prefs.getExponentBase(), timesReminded) * prefs.getFirstReminder() * 1000;
        nextReminder.setTime(nextReminder.getTime() + nextInterval);
        Intent alarmIntent = new Intent(ACTION);
        alarmIntent.putExtra("_id", id.toString());
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        Log.d("recall", "id: " + id);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC, nextReminder.getTime(), sender);
    }

    public void cancelBroadcast(Context context) {
        // please call this before the recall thing is replaced
        Intent alarmIntent = new Intent(ACTION);
        PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public int getAlarmID() {
        return alarmID;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public int compareTo(@NonNull RecallThing another) {
        if (another.isViewed() && !isViewed())
            return -1;
        else if (!another.isViewed() && isViewed())
            return 1;
        return nextReminder.compareTo(another.getNextReminder());
    }

    public static class ThingPositionTuple {
        public RecallThing thing;
        public int position;
        public List<RecallThing> things;

        public ThingPositionTuple(RecallThing thing, int position, List<RecallThing> things) {
            this.thing = thing;
            this.position = position;
            this.things = things;
        }
    }
}
