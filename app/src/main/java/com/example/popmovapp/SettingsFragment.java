package com.example.popmovapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat
                              implements SharedPreferences.OnSharedPreferenceChangeListener
{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_sort_order);

        SharedPreferences sP = getPreferenceManager().getSharedPreferences();
        PreferenceScreen pS  = getPreferenceScreen();

        for (int i =0; i<pS.getPreferenceCount(); i++) {
            Preference p =  getPreferenceScreen().getPreference(i);
            if (p instanceof ListPreference)
                p.setSummary("Showing movies ordered by "+((ListPreference)p).getEntry());
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference p = findPreference(key);
        if (p instanceof  ListPreference){
                p.setSummary("Showing movies ordered by "+
                        ((ListPreference) p).getEntry());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
