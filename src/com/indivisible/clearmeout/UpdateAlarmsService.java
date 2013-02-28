package com.indivisible.clearmeout;

import java.util.Calendar;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;

public class UpdateAlarmsService extends Service
{

	private static final int alarmId = 20901;
	private static final int minsToWaitAfterBoot = 1;
	private static final String defaultInterval = "99";
	
	private static final String TAG = "CMO:UpdateAlarmService";
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "UpdateAlarmsService started...");
		
		// get the AlarmManager
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

		// grab all preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		// end activity if not set to active
		if (!prefs.getBoolean("active", false))
		{
			Log.d(TAG, "Service not active. Removing scheduled alarms and stopping...");
			
			// recreate alarm to cancel it
			Intent dupIntent = new Intent(this, DeleteService.class);
			PendingIntent dupPIntent = PendingIntent.getService(this, alarmId, dupIntent, 0);
			am.cancel(dupPIntent);
			
			// stop service
			stopSelf();
		}
		else
		{
			Log.d(TAG, "Service is active. Resetting Alarms...");
			
			// get the bits we need for setting the appropriate alarm based on prefs
			int intervalPrefMinutes = Integer.parseInt(prefs.getString("clear_interval", defaultInterval));
			long intervalPrefMillis = intervalPrefMinutes * 60000;
			boolean strictInterval = prefs.getBoolean("strict_interval", false);
			Calendar firstTrigger = Calendar.getInstance();
			firstTrigger.add(Calendar.MINUTE, minsToWaitAfterBoot);
			
			// set up the activity calls
			Intent deleteIntent = new Intent(this, DeleteService.class);
			PendingIntent clearMeOutIntent = PendingIntent.getService(this, alarmId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	
			// set repeating alarm
			if (strictInterval)
			{
				Log.d(TAG, "Setting strict repeating alarm. Mins apart: " +intervalPrefMinutes);
				am.setRepeating(AlarmManager.RTC_WAKEUP, firstTrigger.getTimeInMillis(), intervalPrefMillis, clearMeOutIntent);
			}
			else
			{
				Log.d(TAG, "Setting loose repeating alarm. Mins apart: " +intervalPrefMinutes);
				am.setRepeating(AlarmManager.RTC, firstTrigger.getTimeInMillis(), intervalPrefMillis, clearMeOutIntent);
			}
			
			// stop service
			stopSelf();
		}
		
	}

	//// unused Binder
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
