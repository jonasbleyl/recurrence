package com.bleyl.recurrence.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import com.bleyl.recurrence.R;

public class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        updatePreferenceSummary();
    }

    public void updatePreferenceSummary() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        int defaultMinutes = getActivity().getResources().getInteger(R.integer.default_snooze_minutes);
        int minutes = sharedPreferences.getInt("snoozeLength", defaultMinutes);
        String minutesText = getActivity().getResources().getQuantityString(R.plurals.time_minute, minutes);
        Preference preference = findPreference("snoozeLength");
        preference.setSummary(String.format(minutesText, minutes));
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferenceSummary();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}