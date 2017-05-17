package net.alexhyisen.eta1.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
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
}
