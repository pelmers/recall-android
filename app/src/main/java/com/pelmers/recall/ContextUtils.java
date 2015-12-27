package com.pelmers.recall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Some methods which multiple activities share.
 */
public final class ContextUtils {
    private ContextUtils() {}

    /**
     * Launch an activity with "" _id field.
     */
    public static void launchActivity(Context ctx, Class<? extends Activity> activity) {
        launchActivity(ctx, activity, "");
    }

    /**
     * Launch an activity with given _id field.
     */
    public static void launchActivity(Context ctx, Class<? extends Activity> activity, String _id) {
        Intent intent = new Intent(ctx, activity);
        intent.putExtra("_id", _id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * Handle context-invariant types of clicks to the action bar.
     * @return true if we handled the click, false otherwise.
     */
    protected static boolean handleMenuBarClick(Context context, int id) {
        if (id == R.id.action_settings) {
            // start the settings activity
            launchActivity(context, SettingsActivity.class);
            return true;
        } else if (id == R.id.action_add) {
            // start an add activity
            launchActivity(context, AddActivity.class);
            return true;
        } else if (id == R.id.action_about) {
            // show the about dialog
            AboutDialog.show(context);
            return true;
        } else if (id == R.id.action_view_source) {
            // point browser to github page
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/pelmers/recall-android")));
        }
        return false;
    }
}
