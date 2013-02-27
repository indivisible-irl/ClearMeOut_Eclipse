package com.indivisible.clearmeout;

import java.io.File;

import com.mburman.fileexplore.FileExplore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnSharedPreferenceChangeListener
{

	//// data
	private static final int folderIntentRequestCode = 1;
	private static final String strInterval = "Clear the folder every %d minutes";
	
	private EditTextPreference pFolder;
	private EditTextPreference pInterval;
	
	
	//// default Activity methods
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_settings);
		addPreferencesFromResource(R.xml.preferences);
		
		initPrefs();
	}
	
	// register as a sharedPreferenceListener
	@Override
	protected void onResume()
	{
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	// release the sharedPreferenceListener
	@Override
	protected void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	
	//// custom handling of some preferences

	private void initPrefs()
	{
		pFolder = (EditTextPreference) findPreference("folder");
		pInterval = (EditTextPreference) findPreference("clear_interval");
		
		// pFolder
		Intent folderIntent = new Intent(this, FileExplore.class);
		pFolder.setIntent(folderIntent);
		pFolder.setOnPreferenceClickListener(this);
		
		// pInterval
		updateIntervalSummary();
	}
	
	private void updateFolderPreference(Intent receivedIntent)
	{
		String selectedFilePath = receivedIntent.getStringExtra("filepath");
		
		int lastPathSep = selectedFilePath.lastIndexOf(File.separator);
		String selectedFolderPath = selectedFilePath.substring(0, lastPathSep);
		
		pFolder.setSummary(selectedFolderPath);
		pFolder.setText(selectedFolderPath);
	}

	private void updateIntervalSummary()
	{
		int interval = Integer.parseInt(pInterval.getText());
		pInterval.setSummary(String.format(strInterval, interval));
	}
	
	
	//// override onPreferenceClick and onPreferenceChange
	
	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		if (preference.equals(pFolder))
		{
			// disable the EditText dialog as we're using a file picking intent instead
			pFolder.getDialog().dismiss();
			
			// create the file picking intent
			Intent folderIntent = new Intent(this, FileExplore.class);
			startActivityForResult(folderIntent, folderIntentRequestCode);
			
			return true;
		}
		else if (preference.equals(pInterval))
		{
			// update the preference summary with the new value
			updateIntervalSummary();
			return true;
		}
		
		// for all other clicks
		return true;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		if (key.equals("clear_interval"))
		{
			updateIntervalSummary();
		}
		
	}

	
	/// triggered activity result handling
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode)
		{
		case folderIntentRequestCode:
			Log.d("SettingsActivity", "ActivityResult recieved correctly");
			
			updateFolderPreference(data);
			break;
		
		default:
			Log.e("SettingsActivity", "onActivityResult: invalid request code - " +requestCode);
			break;
		}
	}


	


	


	
	
	
	
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.settings, menu);
//		return true;
//	}

}
