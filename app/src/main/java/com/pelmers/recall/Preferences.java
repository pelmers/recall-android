package com.pelmers.recall;

import java.io.Serializable;

/**
 * Object to hold preferences.
 */
public final class Preferences implements Serializable {
    // Time until first reminder, in seconds
    private static final long DEFAULT_FIRST_REMINDER = 9000;
    // Exponential scaling factor
    private static final double DEFAULT_REPETITION_SPACING = 2.0;
    // Interval ceiling at 4 weeks in seconds.
    private static final long DEFAULT_INTERVAL_CEILING = 2419200;
    private long firstReminder;
    private double exponentBase;
    private long intervalCeiling;
    private boolean confirmKeywords;

    /**
     * Construct a new preferences instance with the defaults.
     */
    public Preferences() {
        this(DEFAULT_FIRST_REMINDER, DEFAULT_REPETITION_SPACING, DEFAULT_INTERVAL_CEILING, true);
    }

    /**
     * Construct preferences object with given parameters.
     * @param firstReminder time in seconds until first reminder shown
     * @param exponentBase base of exponential scaling factor
     * @param intervalCeiling the longest interval between reminders
     * @param confirmKeywords whether to confirm keywords with a pop-up on reminders
     */
    public Preferences(long firstReminder, double exponentBase, long intervalCeiling, boolean confirmKeywords) {
        setConfirmKeywords(confirmKeywords);
        setFirstReminder(firstReminder);
        setIntervalCeiling(intervalCeiling);
        setExponentBase(exponentBase);
    }

    public long getFirstReminder() {
        return firstReminder;
    }

    public void setFirstReminder(long firstReminder) {
        this.firstReminder = firstReminder;
    }

    public double getExponentBase() {
        return exponentBase;
    }

    public void setExponentBase(double exponentBase) {
        this.exponentBase = exponentBase;
    }

    public boolean confirmKeywords() {
        return confirmKeywords;
    }

    public void setConfirmKeywords(boolean confirmKeywords) {
        this.confirmKeywords = confirmKeywords;
    }

    public long getIntervalCeiling() {
        return intervalCeiling;
    }

    public void setIntervalCeiling(long intervalCeiling) {
        this.intervalCeiling = intervalCeiling;
    }
}
