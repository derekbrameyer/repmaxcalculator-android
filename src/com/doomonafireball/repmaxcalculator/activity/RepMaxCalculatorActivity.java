package com.doomonafireball.repmaxcalculator.activity;

import com.google.inject.Inject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doomonafireball.repmaxcalculator.Datastore;
import com.doomonafireball.repmaxcalculator.R;
import com.doomonafireball.repmaxcalculator.widget.RepMaxView;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import oak.widget.TextViewWithFont;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * User: derek Date: 11/11/12 Time: 2:21 PM
 */
public class RepMaxCalculatorActivity extends RoboSherlockFragmentActivity implements ActionBar.OnNavigationListener {

    private Typeface robotoLightTf;
    private Typeface robotoBoldTf;
    private int mCurrentReps = 1;
    private String mCurrentType;
    private String mCurrentWeight = "";
    private ArrayList<RepMaxView> mRepMaxes = new ArrayList<RepMaxView>();
    private String mUnitsShort;
    private ArrayAdapter<CharSequence> mSpinnerAdapter;

    @Inject Datastore mDatastore;

    @InjectResource(R.array.rm_formulas) String[] rmFormulas;

    @InjectView(R.id.one_rm) RepMaxView oneRm;
    @InjectView(R.id.two_rm) RepMaxView twoRm;
    @InjectView(R.id.three_rm) RepMaxView threeRm;
    @InjectView(R.id.four_rm) RepMaxView fourRm;
    @InjectView(R.id.five_rm) RepMaxView fiveRm;
    @InjectView(R.id.six_rm) RepMaxView sixRm;
    @InjectView(R.id.seven_rm) RepMaxView sevenRm;
    @InjectView(R.id.eight_rm) RepMaxView eightRm;
    @InjectView(R.id.nine_rm) RepMaxView nineRm;
    @InjectView(R.id.ten_rm) RepMaxView tenRm;
    @InjectView(R.id.eleven_rm) RepMaxView elevenRm;
    @InjectView(R.id.twelve_rm) RepMaxView twelveRm;
    @InjectView(R.id.copy_text) TextView copyText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rep_max_calculator_activity);

        mCurrentType = mDatastore.getPersistedRmFormula();
        if (mCurrentType == null) {
            mCurrentType = getString(R.string.brzycki);
        }

        mSpinnerAdapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(), R.array.rm_formulas,
                R.layout.sherlock_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, this);
        getSupportActionBar().setSelectedNavigationItem(mSpinnerAdapter.getPosition(mCurrentType));

        robotoLightTf = TextViewWithFont.getStaticTypeFace(this, "Roboto-Light.ttf");
        robotoBoldTf = TextViewWithFont.getStaticTypeFace(this, "Roboto-Bold.ttf");

        mRepMaxes.add(oneRm);
        mRepMaxes.add(twoRm);
        mRepMaxes.add(threeRm);
        mRepMaxes.add(fourRm);
        mRepMaxes.add(fiveRm);
        mRepMaxes.add(sixRm);
        mRepMaxes.add(sevenRm);
        mRepMaxes.add(eightRm);
        mRepMaxes.add(nineRm);
        mRepMaxes.add(tenRm);
        mRepMaxes.add(elevenRm);
        mRepMaxes.add(twelveRm);

        for (int i = 0; i < mRepMaxes.size(); i++) {
            mRepMaxes.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentReps = Integer.parseInt((String) v.getTag());
                    mCurrentWeight = mRepMaxes.get(mCurrentReps - 1).getWeight();
                    mCurrentWeight = mCurrentWeight.replaceAll("\\.", "");
                    mCurrentWeight = mCurrentWeight.replaceAll("\\,", "");
                    setRepMaxTypefaces();
                }
            });
            mRepMaxes.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    String textToClip = mRepMaxes.get(Integer.parseInt((String) v.getTag()) - 1).getWeight();
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
                    return true;
                }
            });
        }

        setRepMaxTypefaces();

        if (!mDatastore.getPersistedShownEula()) {
            startActivity(new Intent(this, EulaActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean showDecimal = mDatastore.getPersistedShowDecimal();
        if (mDatastore.getPersistedIsImperial()) {
            mUnitsShort = getString(R.string.lbs);
        } else {
            mUnitsShort = getString(R.string.kg);
        }
        for (int i = 0; i < mRepMaxes.size(); i++) {
            mRepMaxes.get(i).setShowDecimal(showDecimal);
            mRepMaxes.get(i).setFooter(mUnitsShort);
        }
        mRepMaxes.get(mCurrentReps - 1).setWeight(mCurrentWeight);
        calculateRms();
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mCurrentType = rmFormulas[itemPosition];
        mDatastore.persistRmFormula(mCurrentType);
        calculateRms();
        return true;
    }

    public void onNumberClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView tv = (TextView) v;
        if (mCurrentWeight.length() < 5) {
            mCurrentWeight += tv.getText().toString();
            mRepMaxes.get(mCurrentReps - 1).setWeight(mCurrentWeight);
            calculateRms();
        }
    }

    public void onClrClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        mCurrentWeight = "";
        mRepMaxes.get(mCurrentReps - 1).setWeight(mCurrentWeight);
        calculateRms();
    }

    public void onDelClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (mCurrentWeight.length() > 0) {
            mCurrentWeight = mCurrentWeight.substring(0, mCurrentWeight.length() - 1);
            mRepMaxes.get(mCurrentReps - 1).setWeight(mCurrentWeight);
            calculateRms();
        }
    }

    private void calculateRms() {
        if (mCurrentWeight.equals("")) {
            for (int i = 0; i < mRepMaxes.size(); i++) {
                mRepMaxes.get(i).setWeight(mCurrentWeight);
            }
        } else {
            float weightFloat = Float.parseFloat(mCurrentWeight) / 10.0f;
            float[] rms = calculateRm(mCurrentReps, weightFloat, mCurrentType);
            for (int i = 0; i < rms.length; i++) {
                mRepMaxes.get(i).setWeight(Float.toString(rms[i] * 10.0f));
            }
        }
    }

    private void setRepMaxTypefaces() {
        for (int i = 0; i < mRepMaxes.size(); i++) {
            if (i == (mCurrentReps - 1)) {
                mRepMaxes.get(i).setTypeface(robotoBoldTf);
            } else {
                mRepMaxes.get(i).setTypeface(robotoLightTf);
            }
        }
    }

    private float calculateAverage1Rm(int reps, float weight) {
        float oneRm = 0.0f;
        oneRm += weight / (1.0278f - (0.0278f * ((float) reps)));
        oneRm += ((weight * ((float) reps)) / 30.0f) + weight;
        oneRm += (100.0f * weight) / (101.3f - (2.67123f * ((float) reps)));
        oneRm += weight * (1.0f + (0.025f * ((float) reps)));
        oneRm += weight * ((float) Math.pow(((float) reps), 0.1f));
        oneRm += (100.0f * weight) / (52.2f + (41.9f * ((float) Math
                .exp(((double) -0.055f) * ((double) reps)))));
        oneRm += (100.0f * weight) / (48.8f + (53.8f * ((float) Math
                .exp(((double) -0.075f) * ((double) reps)))));
        oneRm /= 7.0f;
        return oneRm;
    }

    private float calculate1Rm(int reps, float weight, String type) {
        float oneRm = 0.0f;
        if (reps == 1) {
            return weight;
        } else {
            if (type.equals(getString(R.string.brzycki))) {
                oneRm = weight / (1.0278f - (0.0278f * ((float) reps)));
            } else if (type.equals(getString(R.string.epley))) {
                oneRm = ((weight * ((float) reps)) / 30.0f) + weight;
            } else if (type.equals(getString(R.string.lander))) {
                oneRm = (100.0f * weight) / (101.3f - (2.67123f * ((float) reps)));
            } else if (type.equals(getString(R.string.o_conner))) {
                oneRm = weight * (1.0f + (0.025f * ((float) reps)));
            } else if (type.equals(getString(R.string.lombardi))) {
                oneRm = weight * ((float) Math.pow(((float) reps), 0.1f));
            } else if (type.equals(getString(R.string.mayhew))) {
                oneRm = (100.0f * weight) / (52.2f + (41.9f * ((float) Math
                        .exp(((double) -0.055f) * ((double) reps)))));
            } else if (type.equals(getString(R.string.wathan))) {
                oneRm = (100.0f * weight) / (48.8f + (53.8f * ((float) Math
                        .exp(((double) -0.075f) * ((double) reps)))));
            } else if (type.equals(getString(R.string.average))) {
                oneRm = calculateAverage1Rm(reps, weight);
            }
        }
        return oneRm;
    }

    private float calculateAverageRm(int reps, float oneRm) {
        float rm = 0.0f;
        rm += oneRm * (1.0278f - (0.0278f * ((float) (reps))));
        rm += oneRm / ((1.0f / 30.0f) * ((float) (reps)) + 1);
        rm += (oneRm * (101.3f - (2.67123f * ((float) (reps))))) / 100.0f;
        rm += oneRm / (1.0f + (0.025f * ((float) (reps))));
        rm += oneRm / ((float) Math.pow(((float) (reps)), 0.1f));
        rm += (oneRm * ((52.2f + (41.9f * ((float) Math.exp(((double) -0.055f) * ((double) (reps))))))))
                / 100.0f;
        rm += (oneRm * ((48.8f + (53.8f * ((float) Math.exp(((double) -0.075f) * ((double) (reps))))))))
                / 100.0f;
        rm /= 7.0f;
        return rm;
    }

    private float[] calculateRm(int reps, float weight, String type) {
        float[] rms = new float[12];
        float oneRm = calculate1Rm(reps, weight, type);
        rms[0] = oneRm;
        if (type.equals(getString(R.string.brzycki))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    // w = 1rm * (1.0278 - (0.0278*r))
                    rms[i] = oneRm * (1.0278f - (0.0278f * ((float) (i + 1))));
                }
            }
        } else if (type.equals(getString(R.string.epley))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    // w = 1RM / (0.033*r + 1)
                    rms[i] = oneRm / ((1.0f / 30.0f) * ((float) (i + 1)) + 1);
                }
            }
        } else if (type.equals(getString(R.string.lander))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    // w = (1rm * (101.3 - 2.67123*r)) / 100
                    rms[i] = (oneRm * (101.3f - (2.67123f * ((float) (i + 1))))) / 100.0f;
                }
            }
        } else if (type.equals(getString(R.string.o_conner))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    // w = 1rm / (1 + 0.025*r)
                    rms[i] = oneRm / (1.0f + (0.025f * ((float) (i + 1))));
                }
            }
        } else if (type.equals(getString(R.string.lombardi))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    // w = 1rm / (r ^ 0.1)
                    rms[i] = oneRm / ((float) Math.pow(((float) (i + 1)), 0.1f));
                }
            }
        } else if (type.equals(getString(R.string.mayhew))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    // w = (1rm * (52.2 + (41.9 * e^(-0.055 * r))) / 100
                    rms[i] = (oneRm * ((52.2f + (41.9f * ((float) Math.exp(((double) -0.055f) * ((double) (i + 1))))))))
                            / 100.0f;
                }
            }
        } else if (type.equals(getString(R.string.wathan))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    // w = (1rm * (48.8 + (53.8 * e^(-0.075 * r))) / 100
                    rms[i] = (oneRm * ((48.8f + (53.8f * ((float) Math.exp(((double) -0.075f) * ((double) (i + 1))))))))
                            / 100.0f;
                }
            }
        } else if (type.equals(getString(R.string.average))) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] = weight;
                } else {
                    rms[i] = calculateAverageRm((i + 1), oneRm);
                }
            }
        }
        return rms;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.rep_max_calculator_menu, menu);
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
                        mRepMaxes.get(mCurrentReps - 1).setWeight(mCurrentWeight);
                        calculateRms();
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    copyText.setText(getString(R.string.pasted_text));
                } catch (Exception e) {
                    copyText.setText(getString(R.string.pasted_text));
                }
                break;
            case R.id.menu_rep_max_forumlas:
                startActivity(new Intent(this, RepMaxFormulaInfoActivity.class));
                break;
            case R.id.menu_conversion:
                startActivity(new Intent(this, ConversionActivity.class));
                return false;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
