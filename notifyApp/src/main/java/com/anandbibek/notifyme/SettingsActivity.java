package com.anandbibek.notifyme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        findPreference("defaultSettings").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                showDefault();
                return false;
            }
        });

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN){
            findPreference("lowPriority").setEnabled(false);
            findPreference("lowPriority").setSummary("Only available on Android 4.1 or above");
        }

    }

    private void showDefault(){
        startActivity(new Intent(this, EditFilterActivity.class)
                .putExtra("filter", 9999)
                .setAction("edit"));
    }
}
