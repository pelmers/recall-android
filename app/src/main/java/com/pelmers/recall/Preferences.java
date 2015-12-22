package com.pelmers.recall;

import java.io.Serializable;

/**
 * Object to hold preferences.
 */
public class Preferences implements Serializable {
    // Time until first reminder, in seconds
    private static long DEFAULT_FIRST_REMINDER = 10;
    // Exponential scaling factor
    private static double DEFAULT_REPETITION_SPACING = 3;
    private long firstReminder;
    private double exponentBase;
    private boolean confirmKeywords;

    /**
     * Construct a new preferences instance with the defaults.
     */
    public Preferences() {
        this(DEFAULT_FIRST_REMINDER, DEFAULT_REPETITION_SPACING, true);
    }

    /**
     * Construct preferences object with given parameters.
     * @param firstReminder time in seconds until first reminder shown
     * @param exponentBase base of exponential scaling factor
     * @param confirmKeywords whether to confirm keywords with a pop-up on reminders
     */
    public Preferences(long firstReminder, double exponentBase, boolean confirmKeywords) {
        setConfirmKeywords(confirmKeywords);
        setFirstReminder(firstReminder);
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
}
