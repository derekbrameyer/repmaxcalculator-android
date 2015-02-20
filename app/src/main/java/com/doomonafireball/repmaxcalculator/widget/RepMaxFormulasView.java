package com.doomonafireball.repmaxcalculator.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.doomonafireball.repmaxcalculator.Formulas;
import com.doomonafireball.repmaxcalculator.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by derek on 11/17/14.
 */
public class RepMaxFormulasView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private CheckBox mUseAllFormulas;
    private CheckBox brzycki, epley, lander, lombardi, mayhew, oconner, wathen;
    private List<CheckBox> mFormulaCheckBoxes;

    public RepMaxFormulasView(Context context) {
        super(context);
        init(context);
    }

    public RepMaxFormulasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RepMaxFormulasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RepMaxFormulasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.rep_max_formulas_view, this);

        mUseAllFormulas = (CheckBox) findViewById(R.id.use_all_formulas);
        mUseAllFormulas.setOnCheckedChangeListener(this);

        brzycki = (CheckBox) findViewById(R.id.brzycki);
        epley = (CheckBox) findViewById(R.id.epley);
        lander = (CheckBox) findViewById(R.id.lander);
        lombardi = (CheckBox) findViewById(R.id.lombardi);
        mayhew = (CheckBox) findViewById(R.id.mayhew);
        oconner = (CheckBox) findViewById(R.id.oconner);
        wathen = (CheckBox) findViewById(R.id.wathen);

        mFormulaCheckBoxes = new ArrayList<CheckBox>();
        mFormulaCheckBoxes.add(brzycki);
        mFormulaCheckBoxes.add(epley);
        mFormulaCheckBoxes.add(lander);
        mFormulaCheckBoxes.add(lombardi);
        mFormulaCheckBoxes.add(mayhew);
        mFormulaCheckBoxes.add(oconner);
        mFormulaCheckBoxes.add(wathen);

        for (CheckBox checkBox : mFormulaCheckBoxes) {
            checkBox.setOnCheckedChangeListener(this);
        }
    }

    public Set<Integer> getSelectedFormulas() {
        Set<Integer> selectedFormulas = new TreeSet<Integer>();
        if (brzycki.isChecked()) {
            selectedFormulas.add(Formulas.BRZYCKI);
        }
        if (epley.isChecked()) {
            selectedFormulas.add(Formulas.EPLEY);
        }
        if (lander.isChecked()) {
            selectedFormulas.add(Formulas.LANDER);
        }
        if (lombardi.isChecked()) {
            selectedFormulas.add(Formulas.LOMBARDI);
        }
        if (mayhew.isChecked()) {
            selectedFormulas.add(Formulas.MAYHEW);
        }
        if (oconner.isChecked()) {
            selectedFormulas.add(Formulas.OCONNER);
        }
        if (wathen.isChecked()) {
            selectedFormulas.add(Formulas.WATHEN);
        }
        return selectedFormulas;
    }

    public void setSelectedFormulas(Set<Integer> selectedFormulas) {
        if (selectedFormulas.contains(Formulas.BRZYCKI)) {
            brzycki.setChecked(true);
        }
        if (selectedFormulas.contains(Formulas.EPLEY)) {
            epley.setChecked(true);
        }
        if (selectedFormulas.contains(Formulas.LANDER)) {
            lander.setChecked(true);
        }
        if (selectedFormulas.contains(Formulas.LOMBARDI)) {
            lombardi.setChecked(true);
        }
        if (selectedFormulas.contains(Formulas.MAYHEW)) {
            mayhew.setChecked(true);
        }
        if (selectedFormulas.contains(Formulas.OCONNER)) {
            oconner.setChecked(true);
        }
        if (selectedFormulas.contains(Formulas.WATHEN)) {
            wathen.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.use_all_formulas:
                if (compoundButton.isPressed()) {
                    for (CheckBox checkBox : mFormulaCheckBoxes) {
                        checkBox.setChecked(isChecked);
                    }
                }
                return;
        }

        boolean allChecked = true;
        for (CheckBox checkBox : mFormulaCheckBoxes) {
            if (!checkBox.isChecked()) {
                allChecked = false;
            }
        }
        mUseAllFormulas.setChecked(allChecked);
    }
}
