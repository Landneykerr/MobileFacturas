package com.facturas.julian.mobilefacturas;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by GrupoDesarrollo on 13/01/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_REPORTING_ON = "pref_report_energy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
