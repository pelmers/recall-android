package com.pelmers.recall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * Method that shows an alert dialog which can take text input and has two buttons.
 */
public final class TextInputAlertDialog {
    private TextInputAlertDialog() {}

    /**
     * Show an alert dialog that takes some text input.
     * @param activity the current activity
     * @param title the title of the dialog
     * @param positiveText positive button label
     * @param onPositive handle positive button press
     */
    public static void showInputAlertDialog(Activity activity, String title, String positiveText, TextInputClickListener onPositive) {
        showInputAlertDialog(activity, title, "", "Cancel", positiveText, new TextInputClickListener() {
            @Override
            public void onClick(DialogInterface dialog, String text) {
            }
        }, onPositive);
    }

    /**
     * Show an alert dialog that takes some text input.
     * @param activity the current activity
     * @param title the title of the dialog
     * @param hintText default text field contents
     * @param negativeText negative button label
     * @param positiveText positive button label
     * @param onNegative handle negative button press
     * @param onPositive handle positive button press
     */
    public static void showInputAlertDialog(Activity activity, String title, String hintText, String negativeText, String positiveText, final TextInputClickListener onNegative, final TextInputClickListener onPositive) {
        showInputAlertDialog(activity, title, hintText, negativeText, null, positiveText, null, null, onPositive);
    }

    /**
     * Show an alert dialog that takes some text input.
     * @param activity the current activity
     * @param title the title of the dialog
     * @param hintText default text field contents
     * @param negativeText negative button label
     * @param neutralText neutral button label
     * @param positiveText positive button label
     * @param onNeutral handle neutral press
     * @param onNegative handle negative button press
     * @param onPositive handle positive button press
     */
    public static void showInputAlertDialog(Activity activity, String title, String hintText, String negativeText, String neutralText, String positiveText, final TextInputClickListener onNegative, final TextInputClickListener onNeutral, final TextInputClickListener onPositive) {
        final EditText editText = new EditText(activity);
        editText.setText(hintText);
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(editText)
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onNegative != null)
                            onNegative.onClick(dialog, editText.getText().toString());
                    }
                })
                .setNeutralButton(neutralText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onNeutral != null)
                            onNeutral.onClick(dialog, editText.getText().toString());
                    }
                })
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onPositive != null)
                            onPositive.onClick(dialog, editText.getText().toString());
                    }
                })
                .show();
    }
    /**
     * Click listener for a button that takes as input the current text field's value.
     */
    public interface TextInputClickListener {
        void onClick(DialogInterface dialog, String text);
    }
}

