package ua.infoshoc.megastyle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


public class NotificationService extends Service{
	private final static Integer TIMEOUT = 60*60*1000;
	private final static Double EPS = 1e-4;
	private final static Integer NOTIFY_ID = 101;

	public final static String LOGIN_NAME = "login";
	//private final static String START_ID_NAME = "startId";
	public final static String PASSWORD_NAME = "password";
	
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	public Boolean working;
	
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
			working = true;
		}
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String login = bundle.getString(LOGIN_NAME);
			String password = bundle.getString(PASSWORD_NAME);					
			Double previousDeposit = -1.0;
			UserInfoFragment userInfo = new UserInfoFragment();
			userInfo
				.setLogin(login)
				.setPassword(password);
			String sid = null;                                        			
			while (working) {
				synchronized (this) {         
					Context context = getApplicationContext();
					userInfo.setContext(context);
					try {                          
						userInfo.update(sid);
					} catch (Exception e) {                
						e.printStackTrace();
					} finally {
						if ( userInfo == null ){                         
						}
						Double deposit = userInfo.getDeposit();            		
						Intent notificationIntent = new Intent(getApplicationContext(), NotificationService.class);
						PendingIntent contentIntent = PendingIntent.getActivity(context,
						        0, notificationIntent,
						        PendingIntent.FLAG_CANCEL_CURRENT);        		
						NotificationManager notificationManager = (NotificationManager) context 
						        .getSystemService(Context.NOTIFICATION_SERVICE);
		
						Float criticalDeposit = getSharedPreferences(SettingsFragment.SHARED_PREFERENCES_NAME, 0).getFloat(SettingsFragment.CRITICAL_DEPOSIT_NAME, 0.0f);
						if ( deposit <= criticalDeposit ){
							if ( Math.abs( deposit - previousDeposit ) > EPS ){
								Resources res = context.getResources();
								NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			
								builder.setContentIntent(contentIntent)
								            .setSmallIcon(android.R.drawable.ic_dialog_alert)
								            .setTicker(res.getString(R.string.low_deposit_ticker))
								            .setWhen(System.currentTimeMillis()) // java.lang.System.currentTimeMillis()
								            .setAutoCancel(true)
								            .setContentTitle(res.getString(R.string.warning_title))
								            .setContentText(res.getString(R.string.deposit_key_text_view) + deposit);
								
								Notification notification = builder.build();
			
								if ( working ){
									notificationManager.notify(NOTIFY_ID, notification);
								}
								
								// previousDeposit = deposit;
							}
						} else {
							notificationManager.cancel(NOTIFY_ID);
						}
					}	
					try {
						wait(TIMEOUT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	HandlerThread handlerThread;
	
	@Override
	public void onCreate() {
		handlerThread = new HandlerThread(
				"NotificationService",
				Process.THREAD_PRIORITY_BACKGROUND);
		handlerThread.start();
		
		mServiceLooper = handlerThread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		handlerThread.quit();
		mServiceLooper.quit();
		working = false;
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		Message msg = mServiceHandler.obtainMessage();
		Bundle bundle = new Bundle();

		SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFERENCES_NAME, 0);
		String login = sharedPreferences.getString(LoginActivity.LOGIN_KEY, null);
		String password = sharedPreferences.getString(LoginActivity.PASSWORD_KEY, null);
		if ( login == null || password == null ){
			return START_NOT_STICKY;
		}{	
			bundle.putString(LOGIN_NAME, login);
			bundle.putString(PASSWORD_NAME, password);
			//bundle.putInt(START_ID_NAME, startId);
			msg.setData(bundle);
			mServiceHandler.sendMessage(msg);
			return START_STICKY;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}	
}



/*public class NotificationSercive extends IntentService {
	
	public NotificationSercive() {
		super("NotificationSercive");
	}
	

    @Override
    public void onDestroy(){
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

	
	@Override
	protected void onHandleIntent(Intent intent) {
		String login = intent.getStringExtra(LOGIN_NAME);
		String password = intent.getStringExtra(PASSWORD_NAME);
		Context context = getApplicationContext();
				
		Double previousDeposit = -1.0;
		UserInfoFragment userInfo = new UserInfoFragment();
		userInfo
			.setLogin(login)
			.setPassword(password);
		String sid = null;
		
		while (true) {
			synchronized (this) {
				try {
					userInfo.update(sid, context);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Double deposit = userInfo.getDeposit();
	
					Intent notificationIntent = new Intent(context, NotificationSercive.class);
					PendingIntent contentIntent = PendingIntent.getActivity(context,
					        0, notificationIntent,
					        PendingIntent.FLAG_CANCEL_CURRENT);
	
					NotificationManager notificationManager = (NotificationManager) context 
					        .getSystemService(Context.NOTIFICATION_SERVICE);
	
					if ( deposit <= depositCritical ){
						if ( Math.abs( deposit - previousDeposit ) > EPS ){
							Resources res = context.getResources();
							NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		
							builder.setContentIntent(contentIntent)
							            .setSmallIcon(R.drawable.ic_launcher)
							            .setTicker(res.getString(R.string.low_deposit_ticker))
							            .setWhen(System.currentTimeMillis()) // java.lang.System.currentTimeMillis()
							            .setAutoCancel(true)
							            .setContentTitle(res.getString(R.string.warning_title))
							            //.setContentText(res.getString(R.string.notifytext))
							            .setContentText(res.getString(R.string.deposit_key_text_view) + deposit); // Текст уведомленимя
							
							Notification notification = builder.build();
		
							notificationManager.notify(NOTIFY_ID, notification);
							
							//previousDeposit = deposit;
						}
					} else {
						notificationManager.cancel(NOTIFY_ID);
					}
				}	
				try {
					wait(TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
		}
	}
}*/