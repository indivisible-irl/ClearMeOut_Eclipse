package com.indivisible.clearmeout;

import java.util.Calendar;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

public class OnBootActivity extends Activity {

	private static final int alarmId = 20901;
	private static final int minsToWaitAfterBoot = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// grab all preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		// end activity if not set to active
		if (prefs.getBoolean("active", false))
		{
			Log.d("OnBootActivity", "Service not active");
			finish();
		}
		
		// get the bits we need for here
		int intervalPref = prefs.getInt("clear_interval", 60);
		long intervalMillis = intervalPref * 60000;
		boolean strictInterval = prefs.getBoolean("strict_interval", false);
		Calendar firstTrigger = Calendar.getInstance();
		firstTrigger.add(Calendar.MINUTE, minsToWaitAfterBoot);
		
		// set up the activity calls
		Intent deleteIntent = new Intent(this, DeleteActivity.class);
		PendingIntent clearMeOutIntent = PendingIntent.getActivity(this, alarmId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		// set repeating alarm
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		if (strictInterval)
		{
			Log.d("OnBootActivity", "Setting strict repeating alarm. Mins apart: " +intervalPref);
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstTrigger.getTimeInMillis(), intervalMillis, clearMeOutIntent);
		}
		else
		{
			Log.d("OnBootActivity", "Setting loose repeating alarm. Mins apart: " +intervalPref);
			am.setRepeating(AlarmManager.RTC, firstTrigger.getTimeInMillis(), intervalMillis, clearMeOutIntent);
		}
		
		
	}

}
