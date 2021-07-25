package com.grupo.Diversitios.presentacion;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.grupo.Diversitios.R;


public class PreferenciasFragment extends PreferenceFragment {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }


}