package ua.infoshoc.megastyle;

import org.htmlcleaner.TagNode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import ua.infoshoc.megastyle.Request;

public class LoginActivity extends Activity implements OnClickListener, OnEditorActionListener {
	
	static String login(Context context, String login, String password) throws Exception{
		final String URL = "https://bills.megastyle.com:9443/index.cgi";
		
		//Do request
		Request loginRequest = new Request( URL );
		loginRequest.addParam ( "passwd", password );
		loginRequest.addParam ( "user", login );
		
		TagNode result = loginRequest.send(context.getResources());
		
		//check errors
		String sid = null;
		if ( result == null ){
			throw new Exception("Can not login");
		} else {
			TagNode errorNode = result.findElementByAttValue("id", "info_message", true, true);
			if ( errorNode != null ){ 
				throw new Exception ( errorNode.getText().toString() );
			} else{
				TagNode[] sids = result.getElementsByAttValue("name", "sid", true, true);
				if ( sids.length == 0 ){
					throw new Exception ( "No SID is found" );
				} else {
					sid = sids[0].getAttributeByName("value");
				}
			}
		}
		
		
		return sid;
	}
	
	public final static String SHARED_PREFERENCES_NAME = "LoginActivityPreferences";
	public final static String LOGIN_KEY = "login";
	public final static String PASSWORD_KEY = "password";
	private SharedPreferences.Editor sharedPreferencesEditor;

	public final static String SID_NAME = "sid";
	public final static String LOGIN_NAME = "login";
	public final static String PASSWORD_NAME = "password";
	
	class LoginTask extends AsyncTask<String, Integer, String>{		
		@Override
		protected String doInBackground(String... params) {
			String sid = null;
			try {
				sid = login(getApplicationContext(), params[0], params[1]);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} finally{
				sharedPreferencesEditor
					.putString(LOGIN_KEY, params[0])
					.putString(PASSWORD_KEY, params[1])
					.apply();
			}
			return sid;
		}
		
		protected void onPostExecute ( String result ){
			if ( result != null ){
				//Start MainActivity
				Intent intent = new Intent ( getApplicationContext(), MainActivity.class );
				intent.putExtra(LoginActivity.SID_NAME, result );
				intent.putExtra(LoginActivity.LOGIN_NAME, user );
				intent.putExtra(LoginActivity.PASSWORD_NAME, password );
				startActivity(intent);
				cancel(false);
			}
			loginButton.setEnabled(true);
		}
	}
	
	private EditText loginEditText;
	private EditText passwordEditText;
	private Button loginButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);

		loginEditText = (EditText) findViewById(R.id.loginEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		passwordEditText.setOnEditorActionListener(this);
		
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		user = sharedPreferences.getString(LOGIN_KEY, null);
		password = sharedPreferences.getString(PASSWORD_KEY, null);
		
		if ( user != null && password != null ){
			loginEditText.setText(user);
			passwordEditText.setText(password);
			onClick(loginButton);
		}		
				
		sharedPreferencesEditor = sharedPreferences.edit();
	}

	private String user;
	private String password;
	
	ProgressDialog progressDialog;
	
	@Override
	public void onClick(View view) {
		int viewId = view.getId(); 
		switch ( viewId ){
		case R.id.loginButton:
			progressDialog = ProgressDialog.show(
					this, 
					getString(R.string.loading_title),
					getString(R.string.loading_title)
			);
			
			loginButton.setEnabled(false);
			user = loginEditText.getText().toString();
			password = passwordEditText.getText().toString();
			new LoginTask().execute(user, password);
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		int viewId = v.getId();
		switch ( viewId ){
		case R.id.passwordEditText:
			if ( actionId == EditorInfo.IME_ACTION_DONE ){
				onClick(loginButton);
			}
			break;
		}
		return false;
	}
}
