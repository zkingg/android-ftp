package com.ftp.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activité des preférences, affiche le menu de configuration
 */
public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
}
