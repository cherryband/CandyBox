package org.quna.candybox.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;

import org.quna.candybox.R;
import org.quna.candybox.typeface.TypefaceEnum;
import org.quna.candybox.typeface.TypefaceSpan;

public class CommentViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        // Update the action bar title with the TypefaceSpan instance
        SpannableString s = new SpannableString("Comments");
        s.setSpan(new TypefaceSpan(this, TypefaceEnum.BOOK.getPath()), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

}
