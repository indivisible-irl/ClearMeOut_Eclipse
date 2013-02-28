package com.indivisible.clearmeout;

import java.io.File;
import java.io.FilenameFilter;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;

public class DeleteService extends Service
{

	//// data
	
	private String folder;
	private File root;
	private boolean recursiveDelete;
	
	
//	//// constructor
//	
//	public DeleteService() {
//		super("ClearMeOut_DeleteService");
//	}
	
	
	
	//// perform on intent calls for this service
	
	@Override
	public void onCreate()
	{		
		// get needed settings
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		folder = prefs.getString("folder", "");
		recursiveDelete = prefs.getBoolean("recursive_delete", false);
		
		// notify user and perform delete
		Toast.makeText(this, "ClearMeOut emptying folder:\n" +folder, Toast.LENGTH_SHORT).show();
		performDelete();
		
		// end service
		stopSelf();
	}
	
	
	/**
	 * Parent method to delete files and/or folders from a directory based on the shared preference "recursive_delete"
	 */
	private void performDelete()
	{
		root = new File(folder);
		if (root.exists() && root.canWrite())
		{
			Log.d("DeleteService", "Can write to: " +root.getAbsolutePath());
		}
		else
		{
			Log.e("DeleteService", "Cannot perform recursive delete: " +root.getAbsolutePath());
//			finish();
		}
		
		
		if (recursiveDelete)
		{
			performRecursiveDelete(root);
		}
		else
		{
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
			Log.d("DeleteService", "Del: " +file.getAbsolutePath());
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
				Log.d("DeleteService", "Del (F): " +file.getAbsolutePath());
				file.delete();
			}
			else
				Log.d("DeleteService", "Did not delete root dir: " +root.getAbsolutePath());
		}
		
	}
	
	
	/**
	 * Delete ONLY the files from a folder leaving all sub-folders untouched
	 */
	private void performNonRecursiveDelete()
	{
		FilenameFilter filter = new FilenameFilter()
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
		
		String[] files = root.list(filter);
		File delFile;
		for (String file : files)
		{
			delFile = new File(root, file);
			Log.d("DeleteService", "Del: " +delFile.getAbsolutePath());
			delFile.delete();
		}
	}


	//// unused binder

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	

}
