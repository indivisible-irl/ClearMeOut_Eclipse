package com.indivisible.clearmeout;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Activity;
import android.content.SharedPreferences;

public class DeleteActivity extends Activity
{

	private String folder;
	private File root;
	private boolean recursiveDelete;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_delete);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		folder = prefs.getString("folder", "");
		recursiveDelete = prefs.getBoolean("recursive_delete", false);
		
		performDelete();
		finish();
	}
	
	
	/**
	 * Parent method to delete files and/or folders from a directory based on the shared preference "recursive_delete"
	 */
	private void performDelete()
	{
		root = new File(folder);
		if (root.exists() && root.canWrite())
		{
			Log.d("DeleteActivity", "Can write to: " +root.getAbsolutePath());
		}
		else
		{
			Log.e("DeleteActivity", "Cannot perform recursive delete: " +root.getAbsolutePath());
			finish();
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
			Log.d("DeleteActivity", "Del: " +file.getAbsolutePath());
		}
		else
		{
			String[] filesAndFolders = file.list();
			
			for (String fname : filesAndFolders)
			{
				performRecursiveDelete(new File(file, fname));
			}
			
			if (file != root)
				Log.d("DeleteActivity", "Del (F): " +file.getAbsolutePath());
			else
				Log.d("DeleteActivity", "Did not delete root dir");
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
			Log.d("DeleteActivity", "Del: " +delFile.getAbsolutePath());
		}
	}


	

}
