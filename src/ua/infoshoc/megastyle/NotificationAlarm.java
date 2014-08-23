package ua.infoshoc.megastyle;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

public class NotificationAlarm extends BroadcastReceiver {
	private static final String SHARED_PREFERENCES_NAME = "NotificationAlarmPreferences";
	private static final String PREVIOUS_DEPOSIT_KEY = "previousDeposit";

	private static final long INTERVAL = AlarmManager.INTERVAL_HALF_HOUR;
	private static final int NOTIFY_ID = 101;

	private static AlarmManager alarmManager;
	private static PendingIntent pendingIntent;

	static class DepositCheckRunnable implements Runnable {
		private Float criticalDeposit;
		private Context context;
		private static Double previousDeposit = -1.0;
		private static UserInfoFragment userInfo;

		public DepositCheckRunnable(Context context, Intent intent) {
			// Log.d("DEBUG", "DepositCheckRunnable");
			this.context = context;
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					SettingsFragment.SHARED_PREFERENCES_NAME, 0);
			criticalDeposit = sharedPreferences.getFloat(
					SettingsFragment.CRITICAL_DEPOSIT_KEY, 0.0f);

			if (userInfo == null) {
				sharedPreferences = context.getSharedPreferences(
						LoginActivity.SHARED_PREFERENCES_NAME, 0);
				String login = sharedPreferences.getString(
						LoginActivity.LOGIN_KEY, null);
				String password = sharedPreferences.getString(
						LoginActivity.PASSWORD_KEY, null);
				userInfo = new UserInfoFragment();
				userInfo.setContext(context).setLogin(login)
						.setPassword(password);
			}
		}

		private static final double EPS = 1e-4;

		@Override
		public void run() {
			// Log.d("DEBUG", "run");
			try {
				userInfo.update();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			Double deposit = userInfo.getDeposit();
			previousDeposit = (double) context.getSharedPreferences(
					SHARED_PREFERENCES_NAME, 0).getFloat(PREVIOUS_DEPOSIT_KEY,
					-1.0f);
			if (deposit <= criticalDeposit && isAlarmSet(context)
					&& Math.abs(deposit - previousDeposit) > EPS) {
				Intent notificationIntent = new Intent(context,
						NotificationAlarm.class);
				PendingIntent contentIntent = PendingIntent.getActivity(
						context, 0, notificationIntent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Resources resources = context.getResources();
				NotificationCompat.Builder builder = new NotificationCompat.Builder(
						context)
						.setContentIntent(contentIntent)
						.setSmallIcon(android.R.drawable.ic_dialog_alert)
						.setLargeIcon(
								BitmapFactory.decodeResource(
										context.getResources(), R.drawable.mega))
						.setTicker(
								resources
										.getString(R.string.low_deposit_ticker))
						.setWhen(System.currentTimeMillis())
						// java.lang.System.currentTimeMillis()
						.setAutoCancel(true)
						.setContentTitle(
								resources.getString(R.string.warning_title))
						.setContentText(
								resources
										.getString(R.string.deposit_key_text_view)
										+ deposit);

				Notification notification = builder.build();
				notificationManager.notify(NOTIFY_ID, notification);
				context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0).edit()
						.putFloat(PREVIOUS_DEPOSIT_KEY, deposit.floatValue())
						.apply();
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log.d("DEBUG", "onReceive");
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()
				&& isAlarmSet(context)) {
			new Thread(new DepositCheckRunnable(context, intent)).start();
		}
	}

	public static boolean isAlarmSet(Context context) {
		return context.getSharedPreferences(
				SettingsFragment.SHARED_PREFERENCES_NAME, 0).getBoolean(
				SettingsFragment.NOTIFICATION_SWITCH_NAME, false);
	}

	public static void setAlarm(Context context) {
		if (alarmManager == null && !isAlarmSet(context)) {
			// Log.d("DEBUG", "setAlarm");
			alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, NotificationAlarm.class);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());

			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime(), INTERVAL, pendingIntent);

			context.getSharedPreferences(
					SettingsFragment.SHARED_PREFERENCES_NAME, 0)
					.edit()
					.putBoolean(SettingsFragment.NOTIFICATION_SWITCH_NAME, true)
					.apply();
		}
	}

	public static void stopAlarm(Context context) {
		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}
		context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0).edit()
				.putFloat(PREVIOUS_DEPOSIT_KEY, -1.0f).apply();
		context.getSharedPreferences(SettingsFragment.SHARED_PREFERENCES_NAME,
				0).edit()
				.putBoolean(SettingsFragment.NOTIFICATION_SWITCH_NAME, false)
				.apply();
	}

}