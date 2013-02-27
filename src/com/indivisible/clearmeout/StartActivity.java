package com.indivisible.clearmeout;

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
				refillFolder();
				
				break;
		}
		
	}
	
	
	
	//// private methods
	
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

	
	private void performDelete()				//TODO move to new class
	{
//		Toast.makeText(this, "Folder:\n\n"+folder, Toast.LENGTH_SHORT).show();
		Intent deleteIntent = new Intent(this, DeleteActivity.class);
		deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(deleteIntent);
	}
	
	private void refillFolder()
	{
		
	}

}
