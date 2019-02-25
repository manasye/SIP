package com.example.sip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
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

        int stepCount = model.getStepCount();
        int target = model.getTarget();

        TextView dateTextView = findViewById(R.id.date_content);
        dateTextView.setText(formatDate(Objects.requireNonNull(model).getDate()));

        TextView stepCounterTextView = findViewById(R.id.step_count_content);
        stepCounterTextView.setText(String.valueOf(stepCount));

        TextView targetTextView = findViewById(R.id.target_content);
        targetTextView.setText(String.valueOf(target));

        TextView caloriesTextView = findViewById(R.id.calories_content);
        caloriesTextView.setText("Calories burned: " + String.valueOf(countCalories(68, stepCount)));

        View conditionView = findViewById(R.id.view_icon);
        TextView conditionTextView = findViewById(R.id.target_condition);
        if (stepCount >= target){
            conditionView.setBackground(getResources().getDrawable(R.drawable.ic_check_circle_green_24dp));
            conditionTextView.setText("Target Achieved");
            conditionTextView.setTextColor(Color.parseColor("#00FF00"));
        }
        else {
            conditionView.setBackground(getResources().getDrawable(R.drawable.ic_cross_red_circle_24dp));
            conditionTextView.setText("Target not Achieved");
            conditionTextView.setTextColor(Color.parseColor("#FF0000"));
        }
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

    int countCalories(int weight, int step){
        double calories;
        calories = 0.0175 * 3.5 * weight * step / 120;
        return (int) Math.floor(calories);
    }

}
