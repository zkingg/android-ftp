package com.ftp.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activit� des pref�rences, affiche le menu de configuration
 */
public class Preferences extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
}
