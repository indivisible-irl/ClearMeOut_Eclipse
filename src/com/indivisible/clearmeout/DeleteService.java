package com.indivisible.clearmeout;

import java.io.File;
import java.io.FilenameFilter;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
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
	private boolean deleteFolders;
	private boolean notifyOnDelete;
	
	private static final String TAG = "CMO:DeleteService";
	
	
	//// perform on intent calls for this service
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "DeleteService started...");
		
		// get needed settings
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		folder = prefs.getString(getString(R.string.pref_key_target_folder), "---");
		
		if (folder.equals("---"))
		{
			Log.w(TAG, "Default folder value found. Shutting down (and disabling service)");
			
			// set service preference to false
			Editor edit = prefs.edit();
			edit.putBoolean(getString(R.string.pref_key_service_active), false);
			edit.commit();
			
			// run UpdateAlarmsService to trigger disable
			Intent disableServiceIntent = new Intent(getApplicationContext(), UpdateAlarmsService.class);
			startService(disableServiceIntent);
			stopSelf();
		}
		else
		{
			// get needed preferences
			recursiveDelete = prefs.getBoolean(getString(R.string.pref_key_use_recursive_delete), false);
			deleteFolders = prefs.getBoolean(getString(R.string.pref_key_delete_folders), false);
			notifyOnDelete = prefs.getBoolean(getString(R.string.pref_key_notify_on_delete), false);
			
			// perform delete
			Log.d(TAG, "ClearMeOut emptying folder: " +folder);
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
			Log.e(TAG, "Cannot perform delete: " +root.getAbsolutePath());
			if (notifyOnDelete)
			{
				Toast.makeText(
						this,
						"ClearMeOut failed to clear:\n" +folder+ "\nDid not have required access.",
						Toast.LENGTH_LONG).show();
			}
			stopSelf();
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
		
		Log.d(TAG, "Finished delete");
		
		if (notifyOnDelete)
		{
			Toast.makeText(this, "ClearMeOut emptied:\n" +folder, Toast.LENGTH_LONG).show();
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
			Log.d(TAG, "Del (F): " +file.getAbsolutePath());
			file.delete();
		}
		else
		{
			String[] filesAndFolders = file.list();
			
			for (String fname : filesAndFolders)
			{
				performRecursiveDelete(new File(file, fname));
			}
			
			if (file.isDirectory() && deleteFolders)
			{
				if (file != root)
				{
					Log.d(TAG, "Del (D): " +file.getAbsolutePath());
					file.delete();
				}
				else
				{
					Log.d(TAG, "Did not delete root dir: " +file.getAbsolutePath());
				}
			}
			else
			{
				Log.d(TAG, "Skipping delete of folder: " +file.getAbsolutePath());
			}
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
