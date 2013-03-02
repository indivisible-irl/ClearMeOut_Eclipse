package com.indivisible.clearmeout;

import java.util.Arrays;
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

	private int alarmId;
	private static final int minsToWaitForFirstRun = 1;
	private static final long millisPerMinute = 60000L;
	private static final long millisPerDay =  86400000L;
	private static final String TAG = "CMO:UpdateAlarmService";
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "UpdateAlarmsService started...");
		
		// get the AlarmManager
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmId = Integer.parseInt(getString(R.string.alarm_id));

		// grab all preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		// end activity if not set to active
		if (!prefs.getBoolean(getString(R.string.pref_key_service_active), false))
		{
			Log.d(TAG, "Service not active. Removing scheduled alarms and stopping...");
			
			// recreate alarm to cancel it
			Intent dupIntent = new Intent(this, DeleteService.class);
			PendingIntent dupPIntent = PendingIntent.getService(getApplicationContext(), alarmId, dupIntent, 0);
			am.cancel(dupPIntent);
			
			// stop service
			stopSelf();
		}
		else
		{
			Log.d(TAG, "Service is active. Resetting Alarms...");
			
			// time triggers for alarms
			long nextRunMillis;
			long repeatMillis;
			Calendar nowCal = Calendar.getInstance();
			
			// type of interval to use
			if(prefs.getBoolean(getString(R.string.pref_key_interval_type), true))
			{
				Log.d(TAG, "Interval set to Daily");

				repeatMillis = millisPerDay;	// one day in milliseconds
				
				// get saved time pref
				String[] values = prefs.getString(getString(R.string.pref_key_daily_at), "00:00").split(":");
				Log.d(TAG, "Time retrieved as: " +Arrays.toString(values));
				int hrs = Integer.parseInt(values[0]);
				int mins = Integer.parseInt(values[1]);
				
				// apply pref time to triggerCal
				Calendar triggerCal = Calendar.getInstance();
				triggerCal.set(Calendar.HOUR_OF_DAY, hrs);
				triggerCal.set(Calendar.MINUTE, mins);
				triggerCal.set(Calendar.SECOND, 0);
				
				//FIXME probable issue on final day of year
				// if today's time has passed add a day to the trigger to set off tomorrow instead
				if(triggerCal.before(nowCal)){
					triggerCal.add(Calendar.MILLISECOND, (int) millisPerDay);
				}
				
				// set nextRun
				nextRunMillis = triggerCal.getTimeInMillis();
			}
			else
			{
				Log.d(TAG, "Interval set to periodic");
				
				// get min spacing and set repeat
				int repeatMins = Integer.parseInt(prefs.getString(getString(R.string.pref_key_periodic_at), "60"));
				Log.d(TAG, "Interval set to (mins): " +repeatMins);
				repeatMillis = repeatMins * millisPerMinute;
				
				nextRunMillis = System.currentTimeMillis() + minsToWaitForFirstRun * millisPerMinute;
			}
			int nextRunInMins = (int) ((nextRunMillis - System.currentTimeMillis()) / 60000);
			Log.d(TAG, "Next run in " +nextRunInMins+ " - " +(nextRunInMins+1) +" mins");
			
			// prepare alarm intent
			Intent deleteIntent = new Intent(getApplicationContext(), DeleteService.class);
			PendingIntent clearMeOutIntent = PendingIntent.getService(
					getApplicationContext(), alarmId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			boolean useStrictRepeat = prefs.getBoolean(getString(R.string.pref_key_use_strict_alarms), false);
			if (useStrictRepeat)
			{
				Log.d(TAG, "Using strict alarms");
				am.setRepeating(AlarmManager.RTC_WAKEUP, nextRunMillis, repeatMillis, clearMeOutIntent);
			}
			else
			{
				Log.d(TAG, "Using non strict alarms");
				am.setInexactRepeating(AlarmManager.RTC, nextRunMillis, repeatMillis, clearMeOutIntent);
			}
			
			
			// stop service
			stopSelf();
			
			
			// old only periodic functionality
//			// get the bits we need for setting the appropriate alarm based on prefs
//			int intervalPrefMinutes = Integer.parseInt(prefs.getString("clear_interval", defaultInterval));
//			long intervalPrefMillis = intervalPrefMinutes * 60000;
//			boolean strictInterval = prefs.getBoolean("strict_interval", false);
//			Calendar firstTrigger = Calendar.getInstance();
//			firstTrigger.add(Calendar.MINUTE, minsToWaitAfterBoot);
//			
//			// set up the activity calls
//			Intent deleteIntent = new Intent(this, DeleteService.class);
//			PendingIntent clearMeOutIntent = PendingIntent.getService(this, alarmId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//	
//			// set repeating alarm
//			if (strictInterval)
//			{
//				Log.d(TAG, "Setting strict repeating alarm. Mins apart: " +intervalPrefMinutes);
//				am.setRepeating(AlarmManager.RTC_WAKEUP, firstTrigger.getTimeInMillis(), intervalPrefMillis, clearMeOutIntent);
//			}
//			else
//			{
//				Log.d(TAG, "Setting loose repeating alarm. Mins apart: " +intervalPrefMinutes);
//				am.setRepeating(AlarmManager.RTC, firstTrigger.getTimeInMillis(), intervalPrefMillis, clearMeOutIntent);
//			}
//			
//			// stop service
//			stopSelf();
		}
		
	}

	//// unused Binder
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
