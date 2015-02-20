package com.doomonafireball.repmaxcalculator.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doomonafireball.repmaxcalculator.R;
import com.doomonafireball.repmaxcalculator.Units;

/**
 * Created by derek on 11/14/14.
 */
public class RepMaxView extends LinearLayout {

    private static final String NO_WEIGHT_TEXT = "--";

    private TextView mHeader;
    private TextView mWeight;
    private TextView mFooter;

    public RepMaxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RepMaxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RepMaxView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
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
        mWeight.setText(NO_WEIGHT_TEXT);
    }

    public String getWeight() {
        return mWeight.getText().toString();
    }

    public void setWeight(String weight) {
        mWeight.setText(weight);
    }

    public void clearWeight() {
        mWeight.setText(NO_WEIGHT_TEXT);
    }

    public void setUnits(int units) {
        if (units == Units.IMPERIAL) {
            mFooter.setText(R.string.pounds_short);
        } else {
            mFooter.setText(R.string.kg);
        }
    }

    public void setFooterVisibility(int visibility) {
        mFooter.setVisibility(visibility);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            mHeader.setTextAppearance(getContext(), R.style.BoldText);
            mWeight.setTextAppearance(getContext(), R.style.BoldText);
        } else {
            mHeader.setTextAppearance(getContext(), R.style.NormalText);
            mWeight.setTextAppearance(getContext(), R.style.NormalText);
        }
    }
}
