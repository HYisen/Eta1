package net.alexhyisen.eta1.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.inputmethod.EditorInfo;

import net.alexhyisen.eta1.R;

/**
 * Created by Alex on 2017/5/5.
 * soul of setting
 */

public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
        setupEditTextPreferenceSummary("pref_host");
        setupEditTextPreferenceSummary("pref_port");
        setupListPreferenceSummary("pref_telnetMode");
        setupEditTextPreferenceSummary("pref_next_card_font_size");
        setupEditTextPreferenceSummary("pref_text_card_font_size");
        setupCheckBoxPreferenceSummary("pref_list_order",
                new String[]{getString(R.string.order_desc), getString(R.string.order_asc)});
    }

    private void setupEditTextPreferenceSummary(CharSequence key) {
        final EditTextPreference pref = (EditTextPreference) getPreferenceManager().findPreference(key);
        pref.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                pref.onClick(pref.getDialog(), Dialog.BUTTON_POSITIVE);
                pref.getDialog().dismiss();
                handled = true;
            }
            return handled;
        });
        pref.setSummary(pref.getText());
        pref.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setSummary(newValue.toString());
            return true;
        });
    }

    private void setupListPreferenceSummary(CharSequence key) {
        final ListPreference pref = (ListPreference) getPreferenceManager().findPreference(key);
        Preference.OnPreferenceChangeListener listener = (preference, newValue) -> {
            preference.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);
            return true;
        };
        listener.onPreferenceChange(pref, pref.getValue());
        pref.setOnPreferenceChangeListener(listener);
    }

    private void setupCheckBoxPreferenceSummary(CharSequence key, String[] summary) {
        final CheckBoxPreference pref = (CheckBoxPreference) getPreferenceManager().findPreference(key);
        Preference.OnPreferenceChangeListener listener = (preference, newValue) -> {
            preference.setSummary(summary[newValue.equals(true) ? 0 : 1]);
            return true;
        };
        listener.onPreferenceChange(pref, pref.isChecked());
        pref.setOnPreferenceChangeListener(listener);
    }
}
