package com.indivisible.clearmeout;

import java.io.File;
import java.io.IOException;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends Activity implements OnClickListener
{
	
	//TODO runs on boot (and app close?)
	//TODO only need one alarm id (repeating)
	

	//// data
	private Button bPref, bDelete, bRefill;
	private TextView tvFolderHint;
	
	private String folder;
	private static final String textFolderHint = "Targetting:\n%s";
	
	
	
	//// default Activity methods
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		initViews();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		folder = prefs.getString("folder", "");
		
		tvFolderHint.setText(String.format(textFolderHint, folder));
	}

	
	//// onCLick
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.start_bPref:
				Log.d("StartActivity", "Opening SettingsActivity...");
				Intent openPrefIntent = new Intent(this, SettingsActivity.class);
				startActivity(openPrefIntent);
				break;
				
			case R.id.start_bDelete:
				performDelete();
				
				break;
				
			case R.id.start_bFillDir:
				try
				{
					refillFolder();
				}
				catch (IOException e)
				{
					Log.e("StartActivity", "Error while populating test folder");
					e.printStackTrace();
					finish();
				}
				
				break;
		}
		
	}
	
	
	
	//// private methods
	
	/**
	 * Initialise the layout's Views
	 */
	private void initViews()
	{
		tvFolderHint = (TextView) findViewById(R.id.start_tvFolderHint);
		bPref = (Button) findViewById(R.id.start_bPref);
		bDelete = (Button) findViewById(R.id.start_bDelete);
		bRefill = (Button) findViewById(R.id.start_bFillDir);
		
		bPref.setOnClickListener(this);
		bDelete.setOnClickListener(this);
		bRefill.setOnClickListener(this);
	}

	/**
	 * Start the DeleteService to perform the folder clear (recursive depending on SharedPreference)
	 */
	private void performDelete()				//TODO move to new class
	{
//		Toast.makeText(this, "Folder:\n\n"+folder, Toast.LENGTH_SHORT).show();
		Intent deleteIntent = new Intent(this, DeleteService.class);
		startService(deleteIntent);
	}
	
	/**
	 * Method to populate the target folder with folders and files for testing purposes
	 * @throws IOException
	 */
	private void refillFolder() throws IOException
	{
		File root = new File(folder);
		
		String[] newFolders = {"0", "1", "2"};
		String[] newFiles = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"};
		
		File useFolder;
		File newFile;
		for (int i=0; i<newFiles.length; i++)
		{
			if ((i/3) < 1)
			{
				useFolder = new File(root, newFolders[0]);
				useFolder.mkdir();
				newFile = new File(useFolder, newFiles[i]+".txt");
				newFile.createNewFile();
			}
			else if ((i/3) < 2)
			{
				useFolder = new File(root, newFolders[1]);
				useFolder.mkdir();
				newFile = new File(useFolder, newFiles[i]+".txt");
				newFile.createNewFile();
			}
			else if ((i/3) < 3)
			{
				useFolder = new File(root, newFolders[2]);
				useFolder.mkdir();
				newFile = new File(useFolder, newFiles[i]+".txt");
				newFile.createNewFile();
			}
			else
			{
				newFile = new File(root, newFiles[i]+".txt");
				newFile.createNewFile();
			}
		}
	}

}
