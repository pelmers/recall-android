package com.pelmers.recall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Encapsulate things about recall things.
 */
public class RecallThing implements Serializable {
    // Time until first reminder, in seconds
    private static final long FIRST_REMINDER = 10;
    // Exponential scaling factor
    private static final double REPETITION_SPACING = 3;

    private String keywords;
    private String description;
    private Date nextReminder;
    private UUID id;
    private int alarmID;
    private int timesReminded = 0;
    private boolean viewed = true;

    public RecallThing(String key, String description, Context ctx) {
        this.keywords = key;
        this.description = description;
        this.nextReminder = new Date();
        id = UUID.randomUUID();
        alarmID = pickAlarmID();
        incrementReminder(ctx);
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

    @Override
    public String toString() {
        return keywords + "\n" + "Next: " + nextReminder;
    }

    public void incrementReminder(Context context) {
        // increment the reminder and set an alarm for the next one.
        timesReminded++;
        // multiply by 1000 to go to milliseconds
        long nextInterval = (long) Math.pow(REPETITION_SPACING, timesReminded) * FIRST_REMINDER * 1000;
        nextReminder.setTime(nextReminder.getTime() + nextInterval);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("thingID", id.toString());
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        Log.d("recall", "id: " + id);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC, nextReminder.getTime(), sender);
    }

    public void cancelBroadcast(Context context) {
        // please call this before the recall thing is replaced
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public UUID getId() {
        return id;
    }
}
