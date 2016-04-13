package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Albertv on 4/13/2016.
 */
public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            TextView detailTextView = ((TextView) findViewById(R.id.detail_text));
            if (detailTextView != null) {
                detailTextView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }
    }
}
