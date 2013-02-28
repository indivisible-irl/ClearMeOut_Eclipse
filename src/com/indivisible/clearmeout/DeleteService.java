package com.indivisible.clearmeout;

import java.io.File;
import java.io.FilenameFilter;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DeleteService extends Service
{

	//// data
	
	private String folder;
	private File root;
	private boolean recursiveDelete;
	
	private static final String TAG = "CMO:DeleteService";
	private static final String key_active = "active";
	private static final String key_recursive = "recursive_delete";
	private static final String key_folder = "folder";
	
	
	
	//// perform on intent calls for this service
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "DeleteService started...");
		
		// get needed settings
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		folder = prefs.getString(key_folder, "---");
		
		if (folder.equals("---"))
		{
			Log.w(TAG, "Default folder value found. Shutting down (and disabling service)");
			
			// set service preference to false
			Editor edit = prefs.edit();
			edit.putBoolean(key_active, false);
			edit.commit();
			
			// run UpdateAlarmsService to trigger disable
			Intent disableServiceIntent = new Intent(getApplicationContext(), UpdateAlarmsService.class);
			startService(disableServiceIntent);
			stopSelf();
		}
		else
		{
			// check and set recursive or non-recursive
			recursiveDelete = prefs.getBoolean(key_recursive, false);
			
			// notify user and perform delete
//			Toast.makeText(this, "ClearMeOut emptying folder:\n" +folder, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "ClearMeOut emptying folder:\n" +folder);
			performDelete();
			
			// end service
			stopSelf();
		}
	}
	
	
	/**
	 * Parent method to delete files and/or folders from a directory based on the shared preference "recursive_delete"
	 */
	private void performDelete()
	{
		root = new File(folder);
		if (root.exists() && root.canWrite())
		{
			Log.d(TAG, "Can write to: " +root.getAbsolutePath());
		}
		else
		{
			Log.e(TAG, "Cannot perform recursive delete: " +root.getAbsolutePath());
//			finish();
		}
		
		
		if (recursiveDelete)
		{
			Log.d(TAG, "Performing recursive delete on " +root.getAbsolutePath());
			performRecursiveDelete(root);
		}
		else
		{
			Log.d(TAG, "Performing non-recursive delete on " +root.getAbsolutePath());
			performNonRecursiveDelete();
		}
	}


	/**
	 * Delete all files AND folders from a directory
	 * @param file
	 */
	private void performRecursiveDelete(File file)
	{
		if (file.isFile())
		{
			Log.d(TAG, "Del: " +file.getAbsolutePath());
			file.delete();
		}
		else
		{
			String[] filesAndFolders = file.list();
			
			for (String fname : filesAndFolders)
			{
				performRecursiveDelete(new File(file, fname));
			}
			
			if (file != root)
			{
				Log.d(TAG, "Del (F): " +file.getAbsolutePath());
				file.delete();
			}
			else
				Log.d(TAG, "Did not delete root dir: " +root.getAbsolutePath());
		}
		
	}
	
	
	/**
	 * Delete ONLY the files from a folder leaving all sub-folders untouched
	 */
	private void performNonRecursiveDelete()
	{
		FilenameFilter fileOnlyFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if ((new File(dir, filename).isFile()))
					return true;
				else
					return false;
			}
		};
		
		String[] files = root.list(fileOnlyFilter);
		File delFile;
		if (files != null)
		{
			for (String file : files)
			{
				delFile = new File(root, file);
				Log.d(TAG, "Del: " +delFile.getAbsolutePath());
				delFile.delete();
			}
		}
	}


	//// unused binder

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	

}
