package ua.infoshoc.megastyle;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	public void setStartedUpdate() {
		progressBar.setVisibility(View.VISIBLE);
	}

	public void setFinishedUpdate() {
		progressBar.setVisibility(View.INVISIBLE);
	}

	public ProgressBar progressBar;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	Intent service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getString(R.string.title_user_info);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		// Start Service
		service = new Intent(this, NotificationService.class);
		if (getSharedPreferences(SettingsFragment.SHARED_PREFERENCES_NAME, 0)
				.getBoolean(SettingsFragment.NOTIFICATION_SWITCH_NAME, false)) {
			startService(service);
		}

		// Set up settings fragment is it is
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(IS_SETTINGS_KEY)) {
			openSettings();
		}
	}

	private final static String IS_SETTINGS_KEY = "isSettings";
	Boolean isSettings = false;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(IS_SETTINGS_KEY, isSettings);
	}

	private static final int SECTION_NUMBER_USER_INFO = 0;
	private static final int SECTION_NUMBER_MONEY_OPERATIONS = 1;
	private static final int SECTION_NUMBER_INTERNET = 2;
	private static final int SECTION_NUMBER_SUPPORT = 3;
	private static final int SECTION_NUMBER_LOGOUT = 4;

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		isSettings = false;

		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		switch (position) {
		case SECTION_NUMBER_USER_INFO:
			mTitle = getString(R.string.title_user_info);
			fragmentManager.beginTransaction()
					.replace(R.id.container, new UserInfoFragment()).commit();
			break;
		case SECTION_NUMBER_MONEY_OPERATIONS:
			mTitle = getString(R.string.title_payments);
			fragmentManager.beginTransaction()
					.replace(R.id.container, new MoneyOperationsFragment())
					.commit();
			break;
		case SECTION_NUMBER_INTERNET:
			mTitle = getString(R.string.title_internet);
			fragmentManager.beginTransaction()
					.replace(R.id.container, new InternetFragment()).commit();
			break;
		case SECTION_NUMBER_SUPPORT:
			mTitle = getString(R.string.title_support);
			fragmentManager.beginTransaction()
					.replace(R.id.container, new SupportFragment()).commit();
			break;
		case SECTION_NUMBER_LOGOUT:
			new AlertDialog.Builder(this)
					.setTitle(R.string.title_logout)
					.setPositiveButton(R.string.logout_button,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String sharedPreferncesNames[] = {
											LoginActivity.SHARED_PREFERENCES_NAME,
											UserInfoFragment.SHARED_PREFERENCES_NAME,
											InternetServiceFragment.SHARED_PREFERENCES_NAME,
											PaymentsFragment.SHARED_PREFERENCES_NAME,
											SettingsFragment.SHARED_PREFERENCES_NAME,
											StatisticsFragment.SHARED_PREFERENCES_NAME,
											WithdrawalFragment.SHARED_PREFERENCES_NAME };

									for (int i = 0; i < sharedPreferncesNames.length; i++) {
										getSharedPreferences(
												sharedPreferncesNames[i], 0)
												.edit().clear().apply();
									}

									stopService(service);

									Intent intent = new Intent(
											getApplicationContext(),
											LoginActivity.class);
									startActivity(intent);
								}
							})
					.setNegativeButton(R.string.escape_button,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							}).setMessage(R.string.logout_warning).show();

			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void openSettings() {
		isSettings = true;
		mTitle = getString(R.string.action_settings);
		getActionBar().setTitle(mTitle);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.container, new SettingsFragment())
				.addToBackStack(null).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			openSettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.id.drawer_layout);
	}
}
