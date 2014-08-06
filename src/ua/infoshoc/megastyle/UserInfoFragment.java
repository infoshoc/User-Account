package ua.infoshoc.megastyle;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.htmlcleaner.TagNode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserInfoFragment extends Fragment {
	
	/*cache*/
	public static final String SHARED_PREFERENCES_NAME = "UserInfoFragmentCache";
	private SharedPreferences.Editor sharedPreferencesEditor;
	
	/*for request*/
	private static final String URL = "https://bills.megastyle.com:9443/index.cgi";
	private static final String INDEX_NAME = "index";
	private static final String INDEX_VALUE = "10";
	private static final String SID_NAME = "sid";

	/*Positions in html code*/
	private static final int LOGIN_TR_IDX = 0;
	private static final int DEPOSIT_TR_IDX = 1;
	private static final int ADDITIONAL_DEPOSIT_TR_IDX = 2;
	private static final int CREDIT_TR_IDX = 3;
	private static final int DISCOUNT_TR_IDX = 4;
	private static final int NAME_TR_IDX = 5;
	private static final int PHONE_TR_IDX = 6;
	private static final int ADDRESS_TR_IDX = 7;
	private static final int EMAIL_TR_IDX = 8;
	private static final int CONTRACT_TR_IDX = 9;
	private static final int CONTRACT_DATE_TR_IDX = 10;
	private static final int STATUS_TR_IDX = 11;
	private static final int SERVICE_NAME_TR_IDX = 0;
	private static final int TARIFF_TR_IDX = 1;
	private static final int SIMULTANEOUSLY_TR_IDX = 2;
	private static final int IP_TR_IDX = 3;
	private static final int NETMASK_TR_IDX = 4;
	private static final int SPEED_TR_IDX = 5;
	private static final int CID_TR_IDX = 6;
	private static final int SERVICE_STATUS_TR_IDX = 7;
	private static final int FINISH_TR_IDX= 8;	

	/*Positions in array*/
	private static final int LOGIN_IDX = 0;
	private static final int DEPOSIT_IDX = 1;
	private static final int ADDITIONAL_DEPOSIT_IDX = 2;
	private static final int CREDIT_IDX = 3;
	private static final int CREDIT_DATE_IDX = 4;
	private static final int DISCOUNT_IDX = 5;
	private static final int DISCOUNT_DATE_IDX = 6;
	private static final int NAME_IDX = 7;
	private static final int PHONE_IDX = 8;
	private static final int ADDRESS_IDX = 9;
	private static final int EMAIL_IDX = 10;
	private static final int CONTRACT_IDX = 11;
	private static final int CONTRACT_DATE_IDX = 12;
	private static final int STATUS_IDX = 13;
	private static final int SERVICE_NAME_IDX = 14;
	private static final int TARIFF_IDX = 15;
	private static final int SIMULTANEOUSLY_IDX = 16;
	private static final int IP_IDX = 17;
	private static final int NETMASK_IDX = 18;
	private static final int SPEED_IDX = 19;
	private static final int CID_IDX = 20;
	private static final int SERVICE_STATUS_IDX = 21;
	private static final int FINISH_IDX = 22;
	private static final int FIELDS_SIZE = 23;
	
	/*Storing data*/
	private CharSequence values[];
	private TextView textViews[];
		
	/*variables for requests*/
	private Context context;
	private String login;
	private String password;
	
	/*Constructors*/
	public UserInfoFragment(){
		textViews = new TextView[FIELDS_SIZE];
		values = new CharSequence[FIELDS_SIZE];		
	}

	/*set fields*/
	public UserInfoFragment setLogin(String login){
		this.login = login;
		return this;
	}
	public UserInfoFragment setPassword(String password){
		this.password = password;
		return this;
	}
	public UserInfoFragment setContext(Context context){
		this.context = context;
		return this;
	}
	
	/*get fields*/
	public Double getDeposit(){                              		
		return Double.parseDouble(values[DEPOSIT_IDX].toString());
	}
	
	/*update fields*/
	public void update(String sid) throws Exception{
		Request request = new Request(URL);
		request.addParam(INDEX_NAME, INDEX_VALUE);
		request.addParam(SID_NAME, sid);
		request.addCookie(SID_NAME, sid);
		TagNode result = null;
		try {
			result = request.send();
		} catch (KeyManagementException | CertificateException
				| KeyStoreException | NoSuchAlgorithmException
				| IOException e) {
			e.printStackTrace();
		}finally{                         
			TagNode div = result.findElementByAttValue("class", "kabinet-center_col", true, true);
			if ( div == null ){
				sid = LoginActivity.login(context, login, password);
				update(sid);
			} else {
				TagNode[] tables = div.getChildTags()[0].getChildTags(); 
				TagNode table = tables[0];
				TagNode[] trs = table.getChildTags()[0].getChildTags();
				int trsLength = trs.length;
				for ( int trsIdx = 0; trsIdx < trsLength; ++trsIdx ){
					TagNode[] tds = trs[trsIdx].getChildTags();
					switch(trsIdx){
					case LOGIN_TR_IDX:
						values[LOGIN_IDX] = tds[1].getText();
						break;
					case DEPOSIT_TR_IDX:
						String deposit = tds[1].getText().toString();
						int spaceIdx = deposit.indexOf(' ');
						values[DEPOSIT_IDX] = deposit.substring(0, spaceIdx);
						break;
					case ADDITIONAL_DEPOSIT_TR_IDX:
						values[ADDITIONAL_DEPOSIT_IDX] = tds[1].getText();
						break;
					case CREDIT_TR_IDX:
						values[CREDIT_IDX] = tds[1].getText();
						String creditDate = tds[0].getText().toString();
						int creditDateLength = creditDate.length();
						values[CREDIT_DATE_IDX] = creditDate.substring(creditDateLength-10, creditDateLength);						
						break;
					case DISCOUNT_TR_IDX:
						String discount = tds[1].getText().toString();
						int percentIdx = discount.indexOf('%');
						values[DISCOUNT_IDX] = discount.substring(0, percentIdx+1);
						int discountLength = discount.length();
						values[DISCOUNT_DATE_IDX] = discount.substring(discountLength-10, discountLength);
						break;
					case NAME_TR_IDX:
						values[NAME_IDX] = tds[1].getText().toString();
						break;
					case PHONE_TR_IDX:
						values[PHONE_IDX] = tds[1].getText().toString();
						break;
					case ADDRESS_TR_IDX:
						values[ADDRESS_IDX] = tds[1].getText().toString();
						break;
					case EMAIL_TR_IDX:
						values[EMAIL_IDX] = tds[1].getText().toString();
						break;
					case CONTRACT_TR_IDX:
						String contract = tds[1].getText().toString();
						spaceIdx = contract.indexOf(' ');
						values[CONTRACT_IDX] = contract.substring(0, spaceIdx);
						break;
					case CONTRACT_DATE_TR_IDX:
						values[CONTRACT_DATE_IDX] = tds[1].getText().toString(); 
						break;
					case STATUS_TR_IDX:
						values[STATUS_IDX] = tds[1].getText().toString();
						break;
					}	
				}
		
				tables = div.getChildTags(); 
				table = tables[1].getElementsByName("table", true)[0];
				trs = table.getElementsByName("tr", true);
				trsLength = trs.length;				
				for ( int trsIdx = 0; trsIdx < trsLength; ++trsIdx ){
					TagNode[] tds = trs[trsIdx].getChildTags();
					switch ( trsIdx ){
					case SERVICE_NAME_TR_IDX:
						values[SERVICE_NAME_IDX] = trs[trsIdx].getText();
						break;
					case TARIFF_TR_IDX:
						values[TARIFF_IDX] = tds[1].getText(); 
						break;
					case SIMULTANEOUSLY_TR_IDX:
						values[SIMULTANEOUSLY_IDX] = tds[1].getText();
						break;
					case IP_TR_IDX:
						values[IP_IDX] = tds[1].getText();
						break;
					case NETMASK_TR_IDX:
						values[NETMASK_IDX] = tds[1].getText();
						break;
					case SPEED_TR_IDX:
						values[SPEED_IDX] = tds[1].getText();
						break;
					case CID_TR_IDX:
						values[CID_IDX] = tds[1].getText();
						break;
					case SERVICE_STATUS_TR_IDX:
						values[SERVICE_STATUS_IDX] = tds[1].getText();
						break;
					case FINISH_TR_IDX:
						values[FINISH_IDX] = tds[1].getText();
						break;
					}
				}
			}
		}
	}
		
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		FragmentActivity fragmentActivity = getActivity();
		context = fragmentActivity.getApplicationContext();
		Intent intent = fragmentActivity.getIntent();
		String sid = intent.getStringExtra(LoginActivity.SID_NAME);
		setLogin(intent.getStringExtra(LoginActivity.LOGIN_NAME));
		setPassword(intent.getStringExtra(LoginActivity.PASSWORD_NAME));
		
		/*Request new data*/
		new Update().execute(sid);
		
		View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
		
		/*Set TextViews*/
		textViews[LOGIN_IDX] = (TextView) rootView.findViewById(R.id.loginTextView);
		textViews[DEPOSIT_IDX] = (TextView) rootView.findViewById(R.id.depositTextView);
		textViews[ADDITIONAL_DEPOSIT_IDX] = (TextView) rootView.findViewById(R.id.additionalDepositTextView);
		textViews[CREDIT_IDX] = (TextView) rootView.findViewById(R.id.creditTextView);
		textViews[CREDIT_DATE_IDX] = (TextView) rootView.findViewById(R.id.creditDateTextView);
		textViews[DISCOUNT_IDX] = (TextView) rootView.findViewById(R.id.discountTextView);
		textViews[DISCOUNT_DATE_IDX] = (TextView) rootView.findViewById(R.id.discountDateTextView);
		textViews[NAME_IDX] = (TextView) rootView.findViewById(R.id.nameTextView);
		textViews[PHONE_IDX] = (TextView) rootView.findViewById(R.id.phoneTextView);
		textViews[ADDRESS_IDX] = (TextView) rootView.findViewById(R.id.addressTextView);
		textViews[EMAIL_IDX] = (TextView) rootView.findViewById(R.id.emailTextView);
		textViews[CONTRACT_IDX] = (TextView) rootView.findViewById(R.id.contractTextView);
		textViews[CONTRACT_DATE_IDX] = (TextView) rootView.findViewById(R.id.contractDateTextView);
		textViews[STATUS_IDX] = (TextView) rootView.findViewById(R.id.statusTextView);
		textViews[SERVICE_NAME_IDX] = (TextView) rootView.findViewById(R.id.serviceNameTextView);
		textViews[TARIFF_IDX] = (TextView) rootView.findViewById(R.id.tariffTextView);
		textViews[SIMULTANEOUSLY_IDX] = (TextView) rootView.findViewById(R.id.simultaneouslyTextView);
		textViews[IP_IDX] = (TextView) rootView.findViewById(R.id.ipTextView);
		textViews[NETMASK_IDX] = (TextView) rootView.findViewById(R.id.netmaskTextView);
		textViews[SPEED_IDX] = (TextView) rootView.findViewById(R.id.speedTextView);
		textViews[CID_IDX] = (TextView) rootView.findViewById(R.id.cidTextView);
		textViews[SERVICE_STATUS_IDX] = (TextView) rootView.findViewById(R.id.serviceStatusTextView);
		textViews[FINISH_IDX] = (TextView) rootView.findViewById(R.id.finishTextView);
		fill();
		
		/*Set Cached Date*/
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		sharedPreferencesEditor = sharedPreferences.edit();
		for (int fieldId = 0; fieldId < FIELDS_SIZE; ++fieldId){
			if ( values[fieldId] == null ){
				values[fieldId] = sharedPreferences.getString(Integer.toString(fieldId), null);
			}
		}
		fill();
		
		return rootView;
	}
		

	private void fill(){
		for ( int i = 0; i < FIELDS_SIZE; ++i ){
			if ( textViews[i] != null && values[i] != null ){
				textViews[i].setText(values[i]);
			}
		}
	}
	
	/*Async Update*/
	private class Update extends AsyncTask<String, Integer, Void>{
		protected void onPreExecute(){
			((MainActivity)getActivity()).progressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(String... params) {
			try {
				update(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Void v){
			fill();
			
			for (int fieldIdx = 0; fieldIdx < FIELDS_SIZE; fieldIdx++) {
				sharedPreferencesEditor.putString(Integer.toString(fieldIdx), values[fieldIdx].toString());
			}
			sharedPreferencesEditor.commit();

			MainActivity mainActivity = (MainActivity)getActivity();
			if ( mainActivity != null ){
				mainActivity.progressBar.setVisibility(View.INVISIBLE);
			}
			cancel(false);
		}
	}
}