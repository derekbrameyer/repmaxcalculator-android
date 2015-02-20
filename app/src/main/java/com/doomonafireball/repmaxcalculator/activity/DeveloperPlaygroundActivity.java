package com.doomonafireball.repmaxcalculator.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.doomonafireball.repmaxcalculator.R;

import java.util.ArrayList;

/**
 * Created by derek on 11/19/14.
 */
public class DeveloperPlaygroundActivity extends ActionBarActivity implements View.OnClickListener {

    private static int sRequestCode = 1337;

    private String intentCode = "Intent intent = new Intent();"
            + "\n"
            + "intent.setComponent(new ComponentName(\"com.doomonafireball.repmaxcalculator\", \"com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity\"));"
            + "\n"
            + "intent.putExtra(\"com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.TITLE_EXTRA\", \"%1$s\");"
            + "\n"
            + "startActivityForResult(intent, YOUR_REQUEST_CODE);";

    private String returnCode = "@Override"
            + "\n"
            + "protected void onActivityResult(int requestCode, int resultCode, Intent data) {"
            + "\n"
            + "  if (requestCode == sRequestCode) {"
            + "\n"
            + "    if (resultCode == RESULT_OK) {"
            + "\n"
            + "      ArrayList<String> results = data.getExtras().getStringArrayList(\"com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.RESULT_WEIGHTS\");"
            + "\n"
            + "      boolean isMetric = data.getExtras().getBoolean(\"com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.IS_METRIC\");"
            + "\n"
            + "    }"
            + "\n"
            + "  }"
            + "\n"
            + "}";

    private Button mFireIntent;
    private TextView mCode;
    private TextView mReturnCode;
    private TextView mResults;
    private EditText mActionBarTitleEditText;

    private String emptyActionBarTitleText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_playground);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFireIntent = (Button) findViewById(R.id.fire_intent);
        mFireIntent.setOnClickListener(this);

        mCode = (TextView) findViewById(R.id.code);
        mReturnCode = (TextView) findViewById(R.id.return_code);
        mResults = (TextView) findViewById(R.id.returned_values);
        mActionBarTitleEditText = (EditText) findViewById(R.id.action_bar_title_edit_text);
        mActionBarTitleEditText.addTextChangedListener(mTextWatcher);

        emptyActionBarTitleText = getString(R.string.action_bar_title_optional);

        mCode.setText(String.format(intentCode, emptyActionBarTitleText));

        mReturnCode.setText(returnCode);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String actionBarTitle = mActionBarTitleEditText.getText().toString();
            if (!TextUtils.isEmpty(actionBarTitle)) {
                mCode.setText(String.format(intentCode, mActionBarTitleEditText.getText().toString()));
            } else {
                mCode.setText(String.format(intentCode, emptyActionBarTitleText));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fire_intent:
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.doomonafireball.repmaxcalculator", "com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity"));
                intent.putExtra("com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.TITLE_EXTRA", mActionBarTitleEditText.getText().toString());
                startActivityForResult(intent, sRequestCode);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == sRequestCode) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> results = data.getExtras().getStringArrayList("com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.RESULT_WEIGHTS");
                boolean isMetric = data.getExtras().getBoolean("com.doomonafireball.repmaxcalculator.activity.IntentCalculationActivity.IS_METRIC");
                mResults.setText("isMetric: " + isMetric + "\n\n" + results.toString());
            } else {
                mResults.setText("");
            }
        }
    }
}
