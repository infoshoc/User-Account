package ua.infoshoc.megastyle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingsFragment extends Fragment implements
		OnCheckedChangeListener, OnEditorActionListener {

	public final static String SHARED_PREFERENCES_NAME = "SettingsFragmentPreferences";
	public final static String CRITICAL_DEPOSIT_NAME = "criticalDeposit";
	public final static String NOTIFICATION_SWITCH_NAME = "notificationSwitch";
	private SharedPreferences.Editor sharedPreferencesEditor;

	EditText criticalDepositEditText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);

		Switch notificationSwitch = (Switch) rootView
				.findViewById(R.id.notificationSwitch);
		notificationSwitch.setOnCheckedChangeListener(this);

		criticalDepositEditText = (EditText) rootView
				.findViewById(R.id.criticalDepositEditText);
		criticalDepositEditText.setOnEditorActionListener(this);

		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(SettingsFragment.SHARED_PREFERENCES_NAME,
						0);
		sharedPreferencesEditor = sharedPreferences.edit();
		notificationSwitch.setChecked(sharedPreferences.getBoolean(
				NOTIFICATION_SWITCH_NAME, false));
		criticalDepositEditText.setText(Float.toString(sharedPreferences
				.getFloat(CRITICAL_DEPOSIT_NAME, 0.0f)));

		return rootView;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int viewId = buttonView.getId();
		switch (viewId) {
		case R.id.notificationSwitch:
			if (isChecked) {
				criticalDepositEditText.setEnabled(true);
				getActivity().startService(
						((MainActivity) getActivity()).service);
			} else {
				criticalDepositEditText.setEnabled(false);
				getActivity().stopService(
						((MainActivity) getActivity()).service);
			}
			sharedPreferencesEditor.putBoolean(NOTIFICATION_SWITCH_NAME,
					isChecked).apply();
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.criticalDepositEditText:
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				Float newCriticalDepositValue = null;
				try {
					newCriticalDepositValue = Float
							.parseFloat(criticalDepositEditText.getText()
									.toString());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sharedPreferencesEditor.putFloat(CRITICAL_DEPOSIT_NAME,
							newCriticalDepositValue).apply();
				}
				getActivity().getApplicationContext();
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(),
						InputMethodManager.RESULT_UNCHANGED_SHOWN);
			}
			break;
		}
		return true;
	}
}
