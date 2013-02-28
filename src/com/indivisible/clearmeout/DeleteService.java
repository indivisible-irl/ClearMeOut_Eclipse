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

public class DeleteService extends Service
{

	//// data
	
	private String folder;
	private File root;
	private boolean recursiveDelete;
	
	private static final String tag = "CMO:DeleteService";
	private static final String key_active = "active";
	private static final String key_recursive = "recursive_delete";
	private static final String key_folder = "folder";
	
	
//	//// constructor
//	
//	public DeleteService() {
//		super("ClearMeOut_DeleteService");
//	}
	
	
	
	//// perform on intent calls for this service
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(tag, "DeleteService started...");
		
		// get needed settings
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean active = prefs.getBoolean(key_active, false);
		
		// ensure the service should be running (in case of accidental non update of alarms
		if (!active)
		{
			Log.w(tag, "ClearMeOut not active, this service should not have been started");
			stopSelf();
		}
		else
		{
			folder = prefs.getString(key_folder, "");
			recursiveDelete = prefs.getBoolean(key_recursive, false);
			
			// notify user and perform delete
			Toast.makeText(this, "ClearMeOut emptying folder:\n" +folder, Toast.LENGTH_SHORT).show();
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
			Log.d(tag, "Can write to: " +root.getAbsolutePath());
		}
		else
		{
			Log.e(tag, "Cannot perform recursive delete: " +root.getAbsolutePath());
//			finish();
		}
		
		
		if (recursiveDelete)
		{
			Log.d(tag, "Performing recursive delete on " +root.getAbsolutePath());
			performRecursiveDelete(root);
		}
		else
		{
			Log.d(tag, "Performing non-recursive delete on " +root.getAbsolutePath());
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
			Log.d(tag, "Del: " +file.getAbsolutePath());
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
				Log.d(tag, "Del (F): " +file.getAbsolutePath());
				file.delete();
			}
			else
				Log.d(tag, "Did not delete root dir: " +root.getAbsolutePath());
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
		for (String file : files)
		{
			delFile = new File(root, file);
			Log.d(tag, "Del: " +delFile.getAbsolutePath());
			delFile.delete();
		}
	}


	//// unused binder

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	

}
