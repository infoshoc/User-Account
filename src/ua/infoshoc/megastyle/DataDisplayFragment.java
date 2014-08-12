package ua.infoshoc.megastyle;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.htmlcleaner.TagNode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public abstract class DataDisplayFragment extends Fragment {
	public abstract DataDisplayFragment update() throws KeyManagementException,
			CertificateException, KeyStoreException, NoSuchAlgorithmException,
			IOException;

	protected abstract DataDisplayFragment saveCache();

	protected abstract DataDisplayFragment getCache();

	protected abstract DataDisplayFragment flush();

	protected abstract String getIndexValue();

	protected abstract String getSharedPreferencesName();

	protected String login;
	protected String password;
	protected String sid;
	protected Context context;
	protected SharedPreferences sharedPreferences;
	protected SharedPreferences.Editor sharedPreferencesEditor;

	protected final static String INDEX_NAME = "index";
	protected final static String SID_NAME = "sid";
	protected final static String SID_KEY = "sid";
	protected final static String URL = "https://bills.megastyle.com:9443/index.cgi";

	public final DataDisplayFragment setLogin(String login) {
		this.login = login;
		return this;
	}

	public final DataDisplayFragment setPassword(String password) {
		this.password = password;
		return this;
	}

	public final DataDisplayFragment setContext(Context context) {
		this.context = context;
		return this;
	}

	public final Context getContext() {
		return context;
	}

	public final TagNode update(Request.ParameterValue params[])
			throws KeyManagementException, CertificateException,
			KeyStoreException, NoSuchAlgorithmException, IOException {
		Request request = new Request(URL, params).addParam(SID_KEY, sid)
				.addParam(INDEX_NAME, getIndexValue()).addCookie(SID_NAME, sid);
		TagNode root = request.send(getContext().getResources());
		TagNode result = root.findElementByAttValue("class",
				"kabinet-center_col", true, true);
		if (result == null) {
			try {
				sid = LoginActivity.login(getContext(), login, password);
				result = update(params);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getContext(), e.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
				// Think what to do here
			}
		}
		return result;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private Update updateAsyncTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FragmentActivity fragmentActivity = getActivity();
		setContext(fragmentActivity.getApplicationContext());

		sharedPreferences = fragmentActivity.getSharedPreferences(
				getSharedPreferencesName(), 0);
		SharedPreferences loginActivitySharedPreferences = fragmentActivity
				.getSharedPreferences(LoginActivity.SHARED_PREFERENCES_NAME, 0);
		setLogin(loginActivitySharedPreferences.getString(
				LoginActivity.LOGIN_KEY, null));
		setPassword(loginActivitySharedPreferences.getString(
				LoginActivity.PASSWORD_KEY, null));

		sharedPreferencesEditor = sharedPreferences.edit();
		updateAsyncTask = new Update();
		updateAsyncTask.execute();

		getCache();
		flush();

		return container;
	}

	@Override
	public void onPause() {
		updateAsyncTask.cancel(true);
		super.onPause();
	}

	@Override
	public void onStop() {
		updateAsyncTask.cancel(true);
		super.onStop();
	}

	private class Update extends AsyncTask<Void, Integer, Void> {
		protected void onPreExecute() {
			MainActivity mainActivity = (MainActivity) getActivity();
			if (mainActivity != null) {
				mainActivity.setStartedUpdate();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				update();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			MainActivity mainActivity = (MainActivity) getActivity();
			if (mainActivity != null) {
				mainActivity.setFinishedUpdate();
			}
			flush();
			saveCache();
			cancel(false);
		}
	}

	protected TextView makeTextView(String text, Boolean isPadding) {
		TextView result = new TextView(getContext());
		result.setText(text);
		if (isPadding) {
			result.setPadding(
					0,
					0,
					Math.round(getContext().getResources().getDimension(
							R.dimen.textview_right_padding)), 0);
		}
		result.setGravity(Gravity.CENTER);
		result.setTextColor(getContext().getResources().getColor(
				R.color.default_text_color));
		return result;

	}

	protected TextView makeTextView(int textId, Boolean isPadding) {
		return makeTextView(getContext().getString(textId), isPadding);
	}

	protected TextView makeTextView(int textId) {
		return makeTextView(textId, true);
	}

	protected TextView makeTextView(String text) {
		return makeTextView(text, true);
	}
}
