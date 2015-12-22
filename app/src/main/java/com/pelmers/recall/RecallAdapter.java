package com.pelmers.recall;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
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
public class RecallAdapter extends ArrayAdapter<RecallNote> {
    public RecallAdapter(Context activity, int layout_id, int text_id, List<RecallNote> notes) {
        super(activity, layout_id, text_id, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view =  (TextView) super.getView(position, convertView, parent);
        markSmaller(view);
        if (!getItem(position).isViewed())
            markUnread(view);
        return view;
    }

    private void markSmaller(TextView view) {
        // mark the last line of view as smaller
        String text = String.valueOf(view.getText());
        int lastN = text.lastIndexOf('\n');
        view.setText(Html.fromHtml(String.format("%s<br><small><small><i>%s</i></small></small>",
                                                  text.substring(0, lastN),
                                                  text.substring(lastN))));
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

