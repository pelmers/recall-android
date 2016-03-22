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

import static com.pelmers.recall.PreferenceLoader.loadPreferences;

/**
 * Class for recall notes, the user-defined items to recall.
 */
public final class RecallNote implements Serializable, Comparable<RecallNote> {
    protected static final String ACTION = "com.pelmers.recall.BROADCAST";
    /**
     * User-defined keywords field for this note.
     */
    private String keywords;
    /**
     * User-defined description field for the note.
     */
    private String description;
    /**
     * When to trigger the next reminder of this note.
     */
    private Date nextReminder;
    /**
     * UUID for this note.
     */
    private UUID id;
    /**
     * ID for notification alarms associated with this note. Ideally it's unique.
     */
    private int alarmID;
    /**
     * Count the number of times we have reminded this note.
     */
    private int timesReminded = -1;
    /**
     * Whether the user has viewed this note since the last reminder.
     */
    private boolean viewed = true;

    /**
     * Construct note using given keyword and description fields, and the current app context.
     */
    public RecallNote(String key, String description, Context ctx) {
        this.keywords = key;
        this.description = description;
        this.nextReminder = new Date();
        id = UUID.randomUUID();
        alarmID = pickAlarmID();
        incrementReminder(ctx);
    }

    /**
     * Find the index of note with given id in a list of notes.
     * @param notes list of notes
     * @param id to find
     * @return index of note with given id, -1 if not found.
     */
    public static int findByID(List<RecallNote> notes, String id) {
        int position = -1;
        for (int i = 0; i < notes.size(); i++) {
            RecallNote note = notes.get(i);
            if (note.getId().toString().equals(id)) {
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

    /**
     * Increment the reminder count, compute the next reminder time,
     * and set an alarm for a notification.
     */
    public void incrementReminder(Context context) {
        // increment the reminder and set an alarm for the next one.
        timesReminded++;
        Preferences prefs = loadPreferences(context);
        // Calculate by formula, then multiply by 1000 to go to milliseconds
        long nextInterval = Math.min(prefs.getIntervalCeiling(),
                                     (long) Math.pow(prefs.getExponentBase(), timesReminded)
                                            * prefs.getFirstReminder()) * 1000;
        nextReminder.setTime(nextReminder.getTime() + nextInterval);
        // Set our alarm with the alarm manager service.
        Intent alarmIntent = new Intent(ACTION);
        alarmIntent.putExtra("_id", id.toString());
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        Log.d("recall", "id: " + id);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC, nextReminder.getTime(), sender);
    }

    /**
     * Cancel any pending alarm broadcast for this note.
     * Call before removing the note or resetting its reminders.
     */
    public void cancelBroadcast(Context context) {
        // please call this before the recall note is replaced
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

    /**
     * Sort by whether the item is viewed; break ties by time.
     * @return less than 0 if this should come before another, > 0 otherwise
     */
    @Override
    public int compareTo(@NonNull RecallNote another) {
        if (another.isViewed() && !isViewed())
            return -1;
        else if (!another.isViewed() && isViewed())
            return 1;
        return nextReminder.compareTo(another.getNextReminder());
    }

}
