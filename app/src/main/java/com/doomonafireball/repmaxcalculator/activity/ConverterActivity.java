package com.doomonafireball.repmaxcalculator.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroupOverlay;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.repmaxcalculator.R;
import com.doomonafireball.repmaxcalculator.util.CalculationUtils;
import com.doomonafireball.repmaxcalculator.util.Utils;

import java.text.DecimalFormat;

/**
 * Created by derek on 11/18/14.
 */
public class ConverterActivity extends ActionBarActivity implements View.OnLongClickListener {

    private static final int MAX_BUFFER_LENGTH = 7;
    private static final DecimalFormat NO_DECIMAL_FORMAT = new DecimalFormat("#");
    private static final DecimalFormat WITH_DECIMAL_FORMAT = new DecimalFormat("#.0#");

    private View mDisplayView;
    private View mDeleteButton;
    private TextView lbsReference, kgReference;
    private TextView kgConversion, lbsConversion;
    private View kgConversionContainer, lbsConversionContainer;

    private Animator mCurrentAnimator;
    private String mBuffer = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        mDisplayView = findViewById(R.id.display_view);
        mDeleteButton = findViewById(R.id.del);
        mDeleteButton.setOnLongClickListener(this);

        lbsReference = (TextView) findViewById(R.id.lbs_reference);
        kgReference = (TextView) findViewById(R.id.kg_reference);
        lbsConversion = (TextView) findViewById(R.id.lbs_conversion);
        kgConversion = (TextView) findViewById(R.id.kg_conversion);

        kgConversionContainer = findViewById(R.id.kg_conversion_container);
        lbsConversionContainer = findViewById(R.id.lbs_conversion_container);

        kgConversionContainer.setOnLongClickListener(this);
        lbsConversionContainer.setOnLongClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.converter_activity_menu, menu);
        return true;
    }

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
                    calculateWeights();
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
    public void onUserInteraction() {
        super.onUserInteraction();

        // If there's an animation in progress, cancel it so the user interaction can be handled
        // immediately.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
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

                    calculateWeights();
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.del) {
            onClear();
            return true;
        }
        switch (view.getId()) {
            case R.id.del:
                onClear();
                return true;
            case R.id.kg_conversion_container:
                Utils.copyStringToClipboard(this, kgConversion.getText().toString());
                return true;
            case R.id.lbs_conversion_container:
                Utils.copyStringToClipboard(this, lbsConversion.getText().toString());
                return true;
        }

        return false;
    }

    private void calculateWeights() {
        if (mBuffer.length() == 0) {
            lbsReference.setText("--");
            kgReference.setText("--");
            lbsConversion.setText("--");
            kgConversion.setText("--");
            return;
        }

        float weight = Float.parseFloat(mBuffer);


        DecimalFormat selectedFormat = NO_DECIMAL_FORMAT;
        if (mBuffer.contains(".")) {
            selectedFormat = WITH_DECIMAL_FORMAT;
        }
        lbsReference.setText(selectedFormat.format(weight));
        kgReference.setText(selectedFormat.format(weight));
        lbsConversion.setText(selectedFormat.format(CalculationUtils.kgToLbs(weight)));
        kgConversion.setText(selectedFormat.format(CalculationUtils.lbsToKg(weight)));
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

        calculateWeights();
    }

    private void onDelete() {
        // Delete works like backspace; remove the last character from the expression.
        if (mBuffer.length() > 0) {
            mBuffer = mBuffer.substring(0, mBuffer.length() - 1);

            calculateWeights();
        }
    }

    private void onClear() {
        if (TextUtils.isEmpty(mBuffer)) {
            return;
        }

        reveal(mDeleteButton, R.color.blue_700, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBuffer = "";
                lbsReference.setText("--");
                kgReference.setText("--");
                lbsConversion.setText("--");
                kgConversion.setText("--");
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
