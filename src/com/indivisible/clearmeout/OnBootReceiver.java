package com.indivisible.clearmeout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver
{

	private static final String TAG = "CMO:OnBootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
//		Toast.makeText(context, "Received boot complete\n\n" +context.getPackageName(), Toast.LENGTH_SHORT).show();
		Log.d(TAG, "BOOT_COMPLETE received, triggering service...");
		Intent runClearMeOut = new Intent(context, UpdateAlarmsService.class);
		context.startService(runClearMeOut);
	}

}
