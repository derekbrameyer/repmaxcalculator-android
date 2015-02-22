package com.doomonafireball.repmaxcalculator.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroupOverlay;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.doomonafireball.repmaxcalculator.Formulas;
import com.doomonafireball.repmaxcalculator.R;
import com.doomonafireball.repmaxcalculator.Units;
import com.doomonafireball.repmaxcalculator.util.CalculationUtils;
import com.doomonafireball.repmaxcalculator.util.Utils;
import com.doomonafireball.repmaxcalculator.widget.RepMaxFormulasView;
import com.doomonafireball.repmaxcalculator.widget.RepMaxView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String PREFERENCE_SHOW_UNITS = "PreferenceShowUnits";
    private static final String PREFERENCE_USE_METRIC_UNITS = "PreferenceUseMetricUnits";
    private static final String PREFERENCE_CALCULATOR_STYLE_NUMBERS = "PreferenceCalculatorStyleNumbers";
    private static final String PREFERENCE_SELECTED_FORMULAS = "PreferenceSelectedFormulas";

    private static final String STATE_BUFFER_CONTENTS = "MainActivity_StateBufferContents";
    private static final String STATE_REP_MAX_SELECTED = "MainActivity_StateRepMaxSelected";

    private static final int MAX_BUFFER_LENGTH = 7;
    private static final DecimalFormat NO_DECIMAL_FORMAT = new DecimalFormat("#");
    private static final DecimalFormat WITH_DECIMAL_FORMAT = new DecimalFormat("#.0#");

    private ViewPager mPadViewPager;
    private View mDisplayView;
    private View mDeleteButton;
    private SwitchCompat mShowUnitsSwitch;
    public SwitchCompat mUseMetricSwitch;
    private SwitchCompat mCalculatorPadSwitch;

    private RepMaxView mCurrentRepMaxView;
    public List<RepMaxView> mRepMaxViews;
    private Animator mCurrentAnimator;
    private View mRepMaxFormulasDialogView;

    private String mBuffer = "";
    private Set<Integer> mSelectedFormulas;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayView = findViewById(R.id.display_rep_maxes);
        mPadViewPager = (ViewPager) findViewById(R.id.pad_pager);
        mDeleteButton = findViewById(R.id.del);

        mCurrentRepMaxView = (RepMaxView) findViewById(R.id.rep_max_1);
        mCurrentRepMaxView.setSelected(true);

        mRepMaxViews = new ArrayList<RepMaxView>();
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_1));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_2));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_3));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_4));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_5));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_6));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_7));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_8));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_9));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_10));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_11));
        mRepMaxViews.add((RepMaxView) findViewById(R.id.rep_max_12));

        mDeleteButton.setOnLongClickListener(this);

        for (RepMaxView repMaxView : mRepMaxViews) {
            repMaxView.setOnLongClickListener(this);
        }

        // Since the CheckBoxes need to be blue in the Dialogs and the Switches white...
        FrameLayout showUnitsSwitchContainer = (FrameLayout) findViewById(R.id.show_units_switch_container);
        ContextThemeWrapper wrapper = new ContextThemeWrapper(
                getSupportActionBar().getThemedContext(), R.style.SwitchTheme);
        View.inflate(wrapper, R.layout.switch_widget, showUnitsSwitchContainer);
        mShowUnitsSwitch = (SwitchCompat) showUnitsSwitchContainer.getChildAt(0);
        mShowUnitsSwitch.setId(R.id.show_units_switch);
        mShowUnitsSwitch.setText(R.string.show_units);

        FrameLayout useMetricSwitchContainer = (FrameLayout) findViewById(R.id.use_metric_switch_container);
        View.inflate(wrapper, R.layout.switch_widget, useMetricSwitchContainer);
        mUseMetricSwitch = (SwitchCompat) useMetricSwitchContainer.getChildAt(0);
        mUseMetricSwitch.setId(R.id.use_metric_switch);
        mUseMetricSwitch.setText(R.string.use_metric_units);

        FrameLayout calculatorPadSwitchContainer = (FrameLayout) findViewById(R.id.calculator_style_numbers_switch_container);
        View.inflate(wrapper, R.layout.switch_widget, calculatorPadSwitchContainer);
        mCalculatorPadSwitch = (SwitchCompat) calculatorPadSwitchContainer.getChildAt(0);
        mCalculatorPadSwitch.setId(R.id.calculator_pad_switch);
        mCalculatorPadSwitch.setText(R.string.calculator_style_numbers);

        mShowUnitsSwitch.setOnCheckedChangeListener(this);
        mUseMetricSwitch.setOnCheckedChangeListener(this);
        mCalculatorPadSwitch.setOnCheckedChangeListener(this);

        Button selectRepMaxFormulas = (Button) findViewById(R.id.select_rep_max_formulas);
        Button lbKgConverter = (Button) findViewById(R.id.lb_kg_converter);

        selectRepMaxFormulas.setOnClickListener(this);
        lbKgConverter.setOnClickListener(this);

        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean showUnits = mSharedPreferences.getBoolean(PREFERENCE_SHOW_UNITS, false);
        boolean useMetricUnits = mSharedPreferences.getBoolean(PREFERENCE_USE_METRIC_UNITS, false);
        boolean calculatorStyleNumbers = mSharedPreferences.getBoolean(PREFERENCE_CALCULATOR_STYLE_NUMBERS, false);
        Set<String> selectedFormulas = mSharedPreferences.getStringSet(PREFERENCE_SELECTED_FORMULAS, null);

        mShowUnitsSwitch.setChecked(showUnits);
        mUseMetricSwitch.setChecked(useMetricUnits);
        mCalculatorPadSwitch.setChecked(calculatorStyleNumbers);

        onUseMetric(useMetricUnits);

        if (selectedFormulas != null) {
            mSelectedFormulas = new TreeSet<Integer>();
            for (String formula : selectedFormulas) {
                mSelectedFormulas.add(Integer.parseInt(formula));
            }
        } else {
            mSelectedFormulas = new TreeSet<Integer>();
            mSelectedFormulas.add(Formulas.BRZYCKI);
            mSelectedFormulas.add(Formulas.EPLEY);
            mSelectedFormulas.add(Formulas.LANDER);
            mSelectedFormulas.add(Formulas.OCONNER);
            mSelectedFormulas.add(Formulas.LOMBARDI);
            mSelectedFormulas.add(Formulas.MAYHEW);
            mSelectedFormulas.add(Formulas.WATHEN);
        }

        // Restore the instance state if necessary
        if (savedInstanceState != null) {
            mBuffer = savedInstanceState.getString(STATE_BUFFER_CONTENTS);
            mCurrentRepMaxView.setSelected(false);
            mCurrentRepMaxView = mRepMaxViews.get(savedInstanceState.getInt(STATE_REP_MAX_SELECTED) - 1);
            mCurrentRepMaxView.setSelected(true);

            calculateRms();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_BUFFER_CONTENTS, mBuffer);
        savedInstanceState.putInt(STATE_REP_MAX_SELECTED, mRepMaxViews.indexOf(mCurrentRepMaxView) + 1);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PREFERENCE_SHOW_UNITS, mShowUnitsSwitch.isChecked());
        editor.putBoolean(PREFERENCE_USE_METRIC_UNITS, mUseMetricSwitch.isChecked());
        editor.putBoolean(PREFERENCE_CALCULATOR_STYLE_NUMBERS, mCalculatorPadSwitch.isChecked());

        Set<String> selectedFormulas = new TreeSet<String>();
        for (Integer formula : mSelectedFormulas) {
            selectedFormulas.add(Integer.toString(formula));
        }
        editor.putStringSet(PREFERENCE_SELECTED_FORMULAS, selectedFormulas);

        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.paste) {
            try {
                String clipboardContents;
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
                    mBuffer = clipboardContents;
                    calculateRms();
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.pasted_text_error, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.pasted_text_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mPadViewPager == null || mPadViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first pad (or the pad is not paged),
            // allow the system to handle the Back button.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous pad.
            mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        // If there's an animation in progress, cancel it so the user interaction can be handled
        // immediately.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
    }

    public void onRepMaxViewClick(View view) {
        mCurrentRepMaxView.setSelected(false);
        mCurrentRepMaxView = (RepMaxView) view;
        mCurrentRepMaxView.setSelected(true);

        // Reset the buffer
        mBuffer = "";
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.del:
                onDelete();
                break;
            case R.id.dec_point:
                onDecimalPoint();
                break;
            default:
                if (mBuffer.length() < MAX_BUFFER_LENGTH) {
                    mBuffer += (((Button) view).getText());

                    calculateRms();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_rep_max_formulas:
                showSelectRepMaxFormulasDialog();
                break;
            case R.id.lb_kg_converter:
                startActivity(new Intent(this, ConverterActivity.class));
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.del) {
            onClear();
            return true;
        }
        if (view instanceof RepMaxView) {
            Utils.copyStringToClipboard(this, ((RepMaxView) view).getWeight());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.calculator_pad_switch:
                onCalculatorPad(isChecked);
                break;
            case R.id.use_metric_switch:
                onUseMetric(isChecked);
                break;
            case R.id.show_units_switch:
                onShowUnits(isChecked);
                break;
        }
    }

    private void showSelectRepMaxFormulasDialog() {
        mRepMaxFormulasDialogView = LayoutInflater.from(this).inflate(R.layout.rep_max_formulas_dialog_custom_view, null);
        final RepMaxFormulasView formulasView = (RepMaxFormulasView) mRepMaxFormulasDialogView.findViewById(R.id.rep_max_formulas_view);
        formulasView.setSelectedFormulas(mSelectedFormulas);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedFormulas = formulasView.getSelectedFormulas();
                dialogInterface.dismiss();
                calculateRms();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(mRepMaxFormulasDialogView);
        builder.setTitle(R.string.select_rep_max_formulas);
        builder.show();
    }

    private void onCalculatorPad(boolean calculatorPad) {
        if (calculatorPad) {
            ((Button) findViewById(R.id.digit_1)).setText(R.string.digit_7);
            ((Button) findViewById(R.id.digit_2)).setText(R.string.digit_8);
            ((Button) findViewById(R.id.digit_3)).setText(R.string.digit_9);
            ((Button) findViewById(R.id.digit_7)).setText(R.string.digit_1);
            ((Button) findViewById(R.id.digit_8)).setText(R.string.digit_2);
            ((Button) findViewById(R.id.digit_9)).setText(R.string.digit_3);
        } else {
            ((Button) findViewById(R.id.digit_1)).setText(R.string.digit_1);
            ((Button) findViewById(R.id.digit_2)).setText(R.string.digit_2);
            ((Button) findViewById(R.id.digit_3)).setText(R.string.digit_3);
            ((Button) findViewById(R.id.digit_7)).setText(R.string.digit_7);
            ((Button) findViewById(R.id.digit_8)).setText(R.string.digit_8);
            ((Button) findViewById(R.id.digit_9)).setText(R.string.digit_9);
        }
    }

    private void onUseMetric(boolean useMetric) {
        for (RepMaxView repMaxView : mRepMaxViews) {
            repMaxView.setUnits(useMetric ? Units.METRIC : Units.IMPERIAL);
        }
    }

    private void onShowUnits(boolean showUnits) {
        for (RepMaxView repMaxView : mRepMaxViews) {
            repMaxView.setFooterVisibility(showUnits ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void calculateRms() {
        if (mBuffer.length() == 0) {
            for (RepMaxView repMaxView : mRepMaxViews) {
                repMaxView.clearWeight();
            }
            return;
        }

        float weight = Float.parseFloat(mBuffer);
        int reps = mRepMaxViews.indexOf(mCurrentRepMaxView) + 1;
        float[] repMaxes = CalculationUtils.calculateRms(reps, weight, mSelectedFormulas);

        DecimalFormat selectedFormat = NO_DECIMAL_FORMAT;
        if (mBuffer.contains(".")) {
            selectedFormat = WITH_DECIMAL_FORMAT;
        }
        for (int i = 0; i < mRepMaxViews.size(); i++) {
            mRepMaxViews.get(i).setWeight(selectedFormat.format(repMaxes[i]));
        }
    }

    private void onDecimalPoint() {
        if (mBuffer.contains(".")) {
            return;
        }
        if (mBuffer.length() == 0) {
            // Starting off with a decimal
            mBuffer = "0.";
        } else {
            mBuffer += ".";
        }

        calculateRms();
    }

    private void onDelete() {
        // Delete works like backspace; remove the last character from the expression.
        if (mBuffer.length() > 0) {
            mBuffer = mBuffer.substring(0, mBuffer.length() - 1);

            calculateRms();
        }
    }

    private void onClear() {
        if (TextUtils.isEmpty(mBuffer)) {
            for (RepMaxView repMaxView : mRepMaxViews) {
                repMaxView.clearWeight();
            }
            return;
        }

        reveal(mDeleteButton, R.color.blue_700, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBuffer = "";
                for (RepMaxView repMaxView : mRepMaxViews) {
                    repMaxView.clearWeight();
                }
            }
        });
    }

    private void reveal(View sourceView, int colorRes, Animator.AnimatorListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            listener.onAnimationEnd(null);
            return;
        }

        final ViewGroupOverlay groupOverlay =
                (ViewGroupOverlay) getWindow().getDecorView().getOverlay();

        final Rect displayRect = new Rect();
        mDisplayView.getGlobalVisibleRect(displayRect);

        // Make reveal cover the display and status bar.
        final View revealView = new View(this);
        revealView.setBottom(displayRect.bottom);
        revealView.setLeft(displayRect.left);
        revealView.setRight(displayRect.right);
        revealView.setBackgroundColor(getResources().getColor(colorRes));
        groupOverlay.add(revealView);

        final int[] clearLocation = new int[2];
        sourceView.getLocationInWindow(clearLocation);
        clearLocation[0] += sourceView.getWidth() / 2;
        clearLocation[1] += sourceView.getHeight() / 2;

        final int revealCenterX = clearLocation[0] - revealView.getLeft();
        final int revealCenterY = clearLocation[1] - revealView.getTop();

        final double x1_2 = Math.pow(revealView.getLeft() - revealCenterX, 2);
        final double x2_2 = Math.pow(revealView.getRight() - revealCenterX, 2);
        final double y_2 = Math.pow(revealView.getTop() - revealCenterY, 2);
        final float revealRadius = (float) Math.max(Math.sqrt(x1_2 + y_2), Math.sqrt(x2_2 + y_2));

        final Animator revealAnimator =
                ViewAnimationUtils.createCircularReveal(revealView,
                        revealCenterX, revealCenterY, 0.0f, revealRadius);
        revealAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_longAnimTime));
        revealAnimator.addListener(listener);

        final Animator alphaAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0.0f);
        alphaAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_mediumAnimTime));

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).before(alphaAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                groupOverlay.remove(revealView);
                mCurrentAnimator = null;
            }
        });

        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }
}
