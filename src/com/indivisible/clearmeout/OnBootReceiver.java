package com.indivisible.clearmeout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OnBootReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Toast.makeText(context, "Received boot complete\n\n" +context.getPackageName(), Toast.LENGTH_SHORT).show();
		Intent runClearMeOut = new Intent(context, OnBootActivity.class);
		runClearMeOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(runClearMeOut);
	}

}
