package com.rrapps.heavensdoor;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_settings);
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new SettingsFragment())
            .commit();
    }
}
