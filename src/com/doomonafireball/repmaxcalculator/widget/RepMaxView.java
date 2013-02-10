package com.doomonafireball.repmaxcalculator.widget;

import com.doomonafireball.repmaxcalculator.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * User: derek Date: 11/21/12 Time: 11:39 AM
 */
public class RepMaxView extends LinearLayout {

    private TextView mHeader;
    private TextView mWeight;
    private TextView mFooter;
    private DecimalFormat df = new DecimalFormat("#.#");

    public RepMaxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        LayoutInflater.from(context).inflate(R.layout.rep_max_view, this);
        mHeader = (TextView) findViewById(R.id.header);
        mWeight = (TextView) findViewById(R.id.weight);
        mFooter = (TextView) findViewById(R.id.footer);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RepMaxView, 0, 0);
        String headerText = array.getString(R.styleable.RepMaxView_header);
        if (headerText != null) {
            mHeader.setText(headerText);
        }
        mWeight.setText("--");
    }

    public void setTypeface(Typeface tf) {
        mHeader.setTypeface(tf);
        mWeight.setTypeface(tf);
        mFooter.setTypeface(tf);
    }

    public String getWeight() {
        if (mWeight.getText().toString().equals("--")) {
            return "";
        }
        return mWeight.getText().toString();
    }

    public void setWeight(String weight) {
        if (weight.equals("")) {
            mWeight.setText("--");
        } else {
            float weightFloat = Float.parseFloat(weight) / 10.0f;
            mWeight.setText(String.format("%.1f", weightFloat));
        }
    }

    public void setFooter(String footer) {
        mFooter.setText(footer);
    }
}
