package com.doomonafireball.repmaxcalculator.util;

import com.doomonafireball.repmaxcalculator.Formulas;

import java.util.Set;

/**
 * Created by derek on 11/14/14.
 */
public class CalculationUtils {

    public static float calculate1Rm(int reps, float weight, Set<Integer> formulas) {
        float oneRm = 0.0f;
        if (reps == 1) {
            return weight;
        } else {
            if (formulas.contains(Formulas.BRZYCKI)) {
                oneRm += weight / (1.0278f - (0.0278f * ((float) reps)));
            }
            if (formulas.contains(Formulas.EPLEY)) {
                oneRm += ((weight * ((float) reps)) / 30.0f) + weight;
            }
            if (formulas.contains(Formulas.LANDER)) {
                oneRm += (100.0f * weight) / (101.3f - (2.67123f * ((float) reps)));
            }
            if (formulas.contains(Formulas.OCONNER)) {
                oneRm += weight * (1.0f + (0.025f * ((float) reps)));
            }
            if (formulas.contains(Formulas.LOMBARDI)) {
                oneRm += weight * ((float) Math.pow(((float) reps), 0.1f));
            }
            if (formulas.contains(Formulas.MAYHEW)) {
                oneRm += (100.0f * weight) / (52.2f + (41.9f * ((float) Math
                        .exp(((double) -0.055f) * ((double) reps)))));
            }
            if (formulas.contains(Formulas.WATHEN)) {
                oneRm += (100.0f * weight) / (48.8f + (53.8f * ((float) Math
                        .exp(((double) -0.075f) * ((double) reps)))));
            }
        }
        return (oneRm / formulas.size());
    }

    public static float[] calculateRms(int reps, float weight, Set<Integer> formulas) {
        float[] rms = new float[12];
        float oneRm = calculate1Rm(reps, weight, formulas);
        rms[0] = oneRm;
        if (formulas.contains(Formulas.BRZYCKI)) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] += weight;
                } else {
                    // w = 1rm * (1.0278 - (0.0278*r))
                    rms[i] += oneRm * (1.0278f - (0.0278f * ((float) (i + 1))));
                }
            }
        }
        if (formulas.contains(Formulas.EPLEY)) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] += weight;
                } else {
                    // w = 1RM / (0.033*r + 1)
                    rms[i] += oneRm / ((1.0f / 30.0f) * ((float) (i + 1)) + 1);
                }
            }
        }
        if (formulas.contains(Formulas.LANDER)) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] += weight;
                } else {
                    // w = (1rm * (101.3 - 2.67123*r)) / 100
                    rms[i] += (oneRm * (101.3f - (2.67123f * ((float) (i + 1))))) / 100.0f;
                }
            }
        }
        if (formulas.contains(Formulas.OCONNER)) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] += weight;
                } else {
                    // w = 1rm / (1 + 0.025*r)
                    rms[i] += oneRm / (1.0f + (0.025f * ((float) (i + 1))));
                }
            }
        }
        if (formulas.contains(Formulas.LOMBARDI)) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] += weight;
                } else {
                    // w = 1rm / (r ^ 0.1)
                    rms[i] += oneRm / ((float) Math.pow(((float) (i + 1)), 0.1f));
                }
            }
        }
        if (formulas.contains(Formulas.MAYHEW)) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] += weight;
                } else {
                    // w = (1rm * (52.2 + (41.9 * e^(-0.055 * r))) / 100
                    rms[i] += (oneRm * ((52.2f + (41.9f * ((float) Math.exp(((double) -0.055f) * ((double) (i + 1))))))))
                            / 100.0f;
                }
            }
        }
        if (formulas.contains(Formulas.WATHEN)) {
            for (int i = 1; i < 12; i++) {
                if (i == (reps - 1)) {
                    rms[i] += weight;
                } else {
                    // w = (1rm * (48.8 + (53.8 * e^(-0.075 * r))) / 100
                    rms[i] += (oneRm * ((48.8f + (53.8f * ((float) Math.exp(((double) -0.075f) * ((double) (i + 1))))))))
                            / 100.0f;
                }
            }
        }
        for (int i = 1; i < 12; i++) {
            rms[i] = rms[i] / formulas.size();
        }
        return rms;
    }

    public static float lbsToKg(float lbs) {
        return lbs / 2.20462f;
    }

    public static float kgToLbs(float kg) {
        return kg * 2.20462f;
    }
}
