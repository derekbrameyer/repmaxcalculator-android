package com.doomonafireball.repmaxcalculator.activity;

import com.google.inject.Inject;

import com.doomonafireball.repmaxcalculator.Datastore;
import com.doomonafireball.repmaxcalculator.R;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * User: derek Date: 7/13/12 Time: 10:40 AM
 */
public class EulaActivity extends RoboActivity {

    @InjectView(R.id.ok) TextView ok;

    @Inject Datastore mDatastore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eula_activity);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatastore.persistShownEula(true);
                finish();
            }
        });
    }
}
