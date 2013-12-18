package com.indivisible.clearmeout;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class CustomFileFilter implements FileFilter
{
	private static final String TAG = "CMO:CustFileFilter";
	
	// filter files by their extension
	private boolean useExtensionFilter;
	private boolean deleteExtensionMatches;
	private String[] extensionsToFilter;
	
	// filter files with regex matches
//	private boolean usePatternFilter;
//	private boolean deletePatternMatches;
//	private String[] patternsToFilter;

	
	// use a constructor to populate the user's settings' vars
	public CustomFileFilter(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		useExtensionFilter = prefs.getBoolean(context.getString(R.string.pref_key_use_filter_extensions), false);
		if (useExtensionFilter)
		{
			deleteExtensionMatches = prefs.getBoolean(context.getString(R.string.pref_key_delete_files_matching_extension), true);	//IDEA declare these defaults somewhere more central?
			
			String rawExtensionFilters = prefs.getString(context.getString(R.string.pref_key_filter_extensions), null);
			if (rawExtensionFilters == null)
			{
				Log.w(TAG, "Extensions choices was made null as could not access saved value. disabling filter");
				useExtensionFilter = false;
				//TODO change useExtensionFilter value to false in actual preferences
			}
			else
			{
				// split extension choices up
				extensionsToFilter = rawExtensionFilters.replace(" ", "").split(",");
				Log.d(TAG, String.format("Split %s extensions: %s", extensionsToFilter.length, Arrays.toString(extensionsToFilter)));
			}
		}
		else
		{
			Log.i(TAG, "filter: extensions filter inactive");
		}
		
		//TODO throw in the usePatternFilter bits here
		
		
	}
	

	@Override
	public boolean accept(File pathname)
	{
		
		
		
		
		
		
		return false;
	}

}
