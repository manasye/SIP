package com.example.sip;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Objects;

public class HistoryContent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        HistoryModel model = Objects.requireNonNull(bundle).getParcelable("data");

        TextView dateTextView = findViewById(R.id.date_content);
        dateTextView.setText(formatDate(Objects.requireNonNull(model).getDate()));

        TextView stepCounterTextView = findViewById(R.id.step_count_content);
        stepCounterTextView.setText(String.valueOf(model.getStepCount()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    String formatDate(String date) {
        String[] splittedDate = date.split("-");
        return splittedDate[0] + " " +
                new DateFormatSymbols().getMonths()[Integer.parseInt(splittedDate[1]) - 1]
                + " " + splittedDate[2];
    }
}
