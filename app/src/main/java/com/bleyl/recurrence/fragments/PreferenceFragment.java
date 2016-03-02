package com.bleyl.recurrence.fragments;

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

        // Set nagging preference summary
        int nagMinutes = sharedPreferences.getInt("nagMinutes", getResources().getInteger(R.integer.default_nag_minutes));
        int nagSeconds = sharedPreferences.getInt("nagSeconds", getResources().getInteger(R.integer.default_nag_seconds));
        Preference nagPreference = findPreference("nagInterval");
        String nagMinutesText = String.format(getActivity().getResources().getQuantityString(R.plurals.time_minute, nagMinutes), nagMinutes);
        String nagSecondsText = String.format(getActivity().getResources().getQuantityString(R.plurals.time_second, nagSeconds), nagSeconds);
        nagPreference.setSummary(String.format("%s %s", nagMinutesText, nagSecondsText));
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