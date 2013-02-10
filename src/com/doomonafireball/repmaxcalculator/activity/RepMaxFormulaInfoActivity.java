package com.doomonafireball.repmaxcalculator.activity;

import com.doomonafireball.repmaxcalculator.R;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * User: derek Date: 7/13/12 Time: 10:40 AM
 */
public class RepMaxFormulaInfoActivity extends RoboActivity {

    @InjectView(R.id.about_text) TextView aboutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rep_max_formula_info_activity);

        aboutText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
