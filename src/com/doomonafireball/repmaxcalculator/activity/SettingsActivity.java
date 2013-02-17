package com.doomonafireball.repmaxcalculator.activity;

import com.google.inject.Inject;

import com.actionbarsherlock.view.MenuItem;
import com.doomonafireball.repmaxcalculator.Datastore;
import com.doomonafireball.repmaxcalculator.R;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import roboguice.inject.InjectView;

/**
 * User: derek Date: 11/6/12 Time: 6:57 PM
 */
public class SettingsActivity extends RoboSherlockFragmentActivity {

    @InjectView(R.id.units) TextView units;
    @InjectView(R.id.show_decimal) TextView showDecimal;
    @InjectView(R.id.about) TextView about;
    @InjectView(R.id.eula) TextView eula;
    @InjectView(R.id.version) TextView version;

    @Inject Datastore mDatastore;

    private String appVersionName = "x.x";
    private int appVersionCode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        setUnitsText();
        setShowDecimalText();

        try {
            appVersionName = getApplication().getPackageManager()
                    .getPackageInfo(getApplication().getPackageName(), 0).versionName;
            appVersionCode = getApplication().getPackageManager()
                    .getPackageInfo(getApplication().getPackageName(), 0).versionCode;
            version.setText("Version " + appVersionName + " b" + appVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            //Failed
        }

        units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatastore.persistIsImperial(!mDatastore.getPersistedIsImperial());
                setUnitsText();
            }
        });
        showDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatastore.persistShowDecimal(!mDatastore.getPersistedShowDecimal());
                setShowDecimalText();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            }
        });
        eula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, EulaActivity.class));
            }
        });
    }

    private void setShowDecimalText() {
        if (mDatastore.getPersistedShowDecimal()) {
            showDecimal.setText(R.string.show_decimal);
        } else {
            showDecimal.setText(R.string.dont_show_decimal);
        }
    }

    private void setUnitsText() {
        if (mDatastore.getPersistedIsImperial()) {
            units.setText(getString(R.string.units_imperial));
        } else {
            units.setText(getString(R.string.units_metric));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
