package com.doomonafireball.repmaxcalculator.activity;

import com.google.inject.Inject;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doomonafireball.repmaxcalculator.Datastore;
import com.doomonafireball.repmaxcalculator.R;
import com.doomonafireball.repmaxcalculator.widget.RepMaxView;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import oak.widget.TextViewWithFont;
import roboguice.inject.InjectView;

/**
 * User: derek Date: 11/11/12 Time: 2:21 PM
 */
public class ConversionActivity extends RoboSherlockFragmentActivity {

    private Typeface robotoBoldTf;
    private String mCurrentWeight = "";

    @Inject Datastore mDatastore;

    @InjectView(R.id.kg_input) RepMaxView kgInput;
    @InjectView(R.id.lbs_input) RepMaxView lbsInput;
    @InjectView(R.id.kg_converted) RepMaxView kgConverted;
    @InjectView(R.id.lbs_converted) RepMaxView lbsConverted;
    @InjectView(R.id.copy_text) TextView copyText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversion_activity);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.pounds_to_kg));

        robotoBoldTf = TextViewWithFont.getStaticTypeFace(this, "Roboto-Bold.ttf");

        kgConverted.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                copyStringToClipboard(kgConverted.getWeight());
                return true;
            }
        });
        lbsConverted.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                copyStringToClipboard(lbsConverted.getWeight());
                return true;
            }
        });
        kgInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                copyStringToClipboard(kgInput.getWeight());
                return true;
            }
        });
        lbsInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                copyStringToClipboard(lbsInput.getWeight());
                return true;
            }
        });

        kgConverted.setTypeface(robotoBoldTf);
        lbsConverted.setTypeface(robotoBoldTf);
        kgInput.setWeight(mCurrentWeight);
        lbsInput.setWeight(mCurrentWeight);
        calculateConversions();
    }

    private void copyStringToClipboard(String textToClip) {
        if (textToClip.length() > 0) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);
                clipboard.setText(textToClip);
            } else {
                android.content.ClipboardManager clipboard
                        = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("weight", textToClip);
                clipboard.setPrimaryClip(clip);
            }
            copyText.setText(String.format(getString(R.string.copy_text), textToClip));
        }
    }

    public void onNumberClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView tv = (TextView) v;
        if (mCurrentWeight.length() < 5) {
            mCurrentWeight += tv.getText().toString();
            kgInput.setWeight(mCurrentWeight);
            lbsInput.setWeight(mCurrentWeight);
            calculateConversions();
        }
    }

    public void onClrClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        mCurrentWeight = "";
        kgInput.setWeight(mCurrentWeight);
        lbsInput.setWeight(mCurrentWeight);
        calculateConversions();
    }

    public void onDelClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (mCurrentWeight.length() > 0) {
            mCurrentWeight = mCurrentWeight.substring(0, mCurrentWeight.length() - 1);
            kgInput.setWeight(mCurrentWeight);
            lbsInput.setWeight(mCurrentWeight);
            calculateConversions();
        }
    }

    private void calculateConversions() {
        if (mCurrentWeight.equals("")) {
            kgConverted.setWeight(mCurrentWeight);
            lbsConverted.setWeight(mCurrentWeight);
        } else {
            float weightFloat = Float.parseFloat(mCurrentWeight) / 10.0f;
            kgConverted.setWeight(Float.toString(lbsToKg(weightFloat) * 10.0f));
            lbsConverted.setWeight(Float.toString(kgToLbs(weightFloat) * 10.0f));
        }
    }

    private float lbsToKg(float lbs) {
        return lbs / 2.2f;
    }

    private float kgToLbs(float kg) {
        return kg * 2.2f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.conversion_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_paste:
                try {
                    String clipboardContents = "";
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
                                Context.CLIPBOARD_SERVICE);
                        clipboardContents = clipboard.getText().toString();
                    } else {
                        android.content.ClipboardManager clipboard
                                = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardContents = clipboard.getPrimaryClip().getItemAt(0).coerceToText(this).toString();
                    }
                    float weight = Float.parseFloat(clipboardContents);
                    if (weight < 100000.0f) {
                        mCurrentWeight = Integer.toString((int) (weight * 10.0f));
                        kgInput.setWeight(mCurrentWeight);
                        lbsInput.setWeight(mCurrentWeight);
                        calculateConversions();
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    copyText.setText(getString(R.string.pasted_text));
                } catch (Exception e) {
                    copyText.setText(getString(R.string.pasted_text));
                }
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
