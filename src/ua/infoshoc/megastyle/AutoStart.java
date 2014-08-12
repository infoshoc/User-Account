package ua.infoshoc.megastyle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (context.getSharedPreferences(
				SettingsFragment.SHARED_PREFERENCES_NAME, 0).getBoolean(
				SettingsFragment.NOTIFICATION_SWITCH_NAME, false)) {
			Intent autoStartService = new Intent(context,
					NotificationService.class);
			context.startService(autoStartService);
		}
	}

}
