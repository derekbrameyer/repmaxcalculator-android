package com.doomonafireball.repmaxcalculator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.content.SharedPreferences;

@Singleton
public class Datastore {

    private static final String DEVICE_VERSION = "DeviceVersion";
    private static final String IS_IMPERIAL = "IsImperial";
    private static final String SHOWN_EULA = "ShownEula";
    private static final String RM_FORMULA = "RmFormula";

    @Inject EncryptedSharedPreferences encryptedSharedPreferences;

    private SharedPreferences.Editor getEditor() {
        return encryptedSharedPreferences.edit();
    }

    private SharedPreferences getPrefs() {
        return encryptedSharedPreferences;
    }
    public int getVersion() {
        return getPrefs().getInt(DEVICE_VERSION, 0);
    }
    public void persistVersion(int version) {
        getEditor().putInt(DEVICE_VERSION, version).commit();
    }

    public boolean getPersistedIsImperial() {
        return getPrefs().getBoolean(IS_IMPERIAL, true);
    }

    public void persistIsImperial(boolean isImperial) {
        getEditor().putBoolean(IS_IMPERIAL, isImperial).commit();
    }

    public boolean getPersistedShownEula() {
        return getPrefs().getBoolean(SHOWN_EULA, false);
    }

    public void persistShownEula(boolean shownEula) {
        getEditor().putBoolean(SHOWN_EULA, shownEula).commit();
    }

    public String getPersistedRmFormula() {
        return getPrefs().getString(RM_FORMULA, null);
    }

    public void persistRmFormula(String rmFormula) {
        getEditor().putString(RM_FORMULA, rmFormula).commit();
    }
}

