package com.pelmers.recall;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

/**
 * About dialog that gives a bit of background on the app and has a donation button.
 */
public final class AboutDialog {
    public static void show(final Context context) {
        final TextView textView = new TextView(context);
        textView.setText(R.string.about_text);
        textView.setLineSpacing(0, 1.1f);
        textView.setPadding(40, 22, 40, 15);
        new AlertDialog.Builder(context)
                .setTitle("About Recall")
                .setView(textView)
                .setNegativeButton("Not today", null)
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.paypal.me/pelmers/3")));
                    }
                })
                .show();
    }
}
