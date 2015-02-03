package com.pelmers.recall;

import java.io.Serializable;

/**
 * Object to hold preferences.
 */
public class Preferences implements Serializable {
    private long firstReminder;
    private double exponentBase;

    public Preferences(long firstReminder, double exponentBase) {
        this.firstReminder = firstReminder;
        this.exponentBase = exponentBase;
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
}
