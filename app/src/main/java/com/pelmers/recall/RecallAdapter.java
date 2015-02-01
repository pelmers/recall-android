package com.pelmers.recall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter based on ArrayAdapter
 */
public class RecallAdapter extends ArrayAdapter<RecallThing> {
    public RecallAdapter(Context activity, int layout_id, int text_id, List<RecallThing> things) {
        super(activity, layout_id, text_id, things);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view =  (TextView) super.getView(position, convertView, parent);
        if (getItem(position).isViewed())
            markRead(view);
        else
            markUnread(view);
        return view;
    }

    /**
     * Modify the style of a text view to indicate completion
     * @param textView to mark completed
     */
    public void markRead(TextView textView) {
        textView.setTextColor(Color.BLACK);
        textView.setText(textView.getText().toString());
    }

    /**
     * Mark a text view as incomplete (should undo what markComplete does)
     * @param textView to mark incomplete
     */
    public void markUnread(TextView textView) {
        SpannableString spannableString = new SpannableString(textView.getText());
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), 0);
        textView.setTextColor(MainActivity.THEME_COLOR);
        textView.setText(spannableString);
    }
}

