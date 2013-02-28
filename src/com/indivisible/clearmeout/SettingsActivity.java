package com.indivisible.clearmeout;

import java.io.File;

import com.mburman.fileexplore.FileExplore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;


@SuppressWarnings("deprecation")	//FIXME comment this to view where to update to newer functionality
public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnSharedPreferenceChangeListener
{

	//// data
	private static final int folderIntentRequestCode = 1;
	private static final String strInterval = "Clear the folder every %d minutes";
	private static final String TAG = "CMO:SettingsActivity";
	
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
		
		pFolder.setSummary(pFolder.getText());
		updateIntervalSummary();
	}

	// release the sharedPreferenceListener
	@Override
	protected void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		//TODO best here or in onPause()? Called too often up there or too rarely here?
		Intent updateIntent = new Intent(getApplicationContext(), UpdateAlarmsService.class);
		startService(updateIntent);
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
		
	}
	
	private void updateFolderPreference(Intent receivedIntent)
	{
		String selectedFilePath = receivedIntent.getStringExtra("filepath");
		
		int lastPathSep = selectedFilePath.lastIndexOf(File.separator);
		String selectedFolderPath = selectedFilePath.substring(0, lastPathSep);
		
		pFolder.setSummary(selectedFolderPath);
		pFolder.setText(selectedFolderPath);
		
//		Toast.makeText(this, "SET AS:\n\n"+pFolder.getText(), Toast.LENGTH_SHORT).show();
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
		// Folder preference
		if (preference.equals(pFolder))
		{
			// disable the EditText dialog (and soft keyboard) as we're using a file picking intent instead
			//TODO prob better to disable the pref opening a dialog at all
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//		    imm.hideSoftInputFromInputMethod(pFolder.getEditText().getApplicationWindowToken(), 0);
			pFolder.getDialog().dismiss();
			
			// create the file picking intent
			Intent folderIntent = new Intent(this, FileExplore.class);
			startActivityForResult(folderIntent, folderIntentRequestCode);
			
			return true;
		}
		
		// Interval Preference
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
		
		if (resultCode == 1)
		{
			switch (requestCode)
			{
			case folderIntentRequestCode:
				Log.d(TAG, "ActivityResult recieved correctly");
				
				updateFolderPreference(data);
				break;
			
			default:
				Log.e(TAG, "onActivityResult: invalid request code - " +requestCode);
				break;
			}
		}
		else
		{
			Log.w(TAG, "Received non 1 resultCode: " +resultCode);
		}
	}



	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.settings, menu);
//		return true;
//	}

}
