package com.pelmers.recall;

import java.io.Serializable;
import java.util.Date;

/**
 * Encapsulate things about recall things.
 */
public class RecallThing implements Serializable {
    private static final long FIRST_REMINDER = 60;
    private static final double REPETITION_SPACING = 1.2;

    private String keywords;
    private String description;
    private Date nextReminder;
    private int timesReminded;

    public RecallThing(String key, String description) {
        this.keywords = key;
        this.description = description;
        this.nextReminder = new Date();
        this.timesReminded = 0;
        incrementReminder();
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
        return "RecallThing{" +
                "keywords='" + keywords + '\'' +
                ", description='" + description + '\'' +
                ", nextReminder=" + nextReminder +
                ", timesReminded=" + timesReminded +
                '}';
    }

    public void incrementReminder() {
        timesReminded++;
        // multiply by 1000 to go to milliseconds
        long nextInterval = (long) Math.pow(REPETITION_SPACING, timesReminded) * FIRST_REMINDER * 1000;
        nextReminder.setTime(nextReminder.getTime() + nextInterval);
    }
}
