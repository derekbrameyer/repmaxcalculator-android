package com.doomonafireball.repmaxcalculator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.doomonafireball.repmaxcalculator.R;
import com.doomonafireball.repmaxcalculator.widget.RepMaxView;

import java.util.ArrayList;

/**
 * Created by derek on 11/18/14.
 */
public class IntentCalculationActivity extends MainActivity {

    private static final String TITLE_EXTRA = "com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.TITLE_EXTRA";
    private static final String RESULT_WEIGHTS = "com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.RESULT_WEIGHTS";
    private static final String IS_METRIC = "com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.IS_METRIC";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String title = extras.getString(TITLE_EXTRA);
        if (!TextUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.intent_calculation_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done) {
            Intent data = new Intent();

            ArrayList<String> results = new ArrayList<String>();
            for (RepMaxView repMaxView : mRepMaxViews) {
                results.add(repMaxView.getWeight());
            }
            data.putExtra(RESULT_WEIGHTS, results);
            data.putExtra(IS_METRIC, mUseMetricSwitch.isChecked());

            setResult(RESULT_OK, data);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
