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
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PaymentsFragment extends Fragment{ 

	/*for cache*/
	public final static String SHARED_PREFERENCES_NAME = "PaymentsFragmentCache";
	public final static String TABLE_LENGTH_KEY = "table_length";
	public final static String OVERALL_KEY = "overall";
	public final static String SUM_KEY = "sum";
	private SharedPreferences.Editor sharedPreferencesEditor;
	
	/*for request*/
	private static final String URL = "https://bills.megastyle.com:9443/index.cgi";
	private static final String INDEX_NAME = "index";
	private static final String INDEX_VALUE = "42";
	private static final String SID_NAME = "sid";
	
	/*for storage*/
	private static final int COLUMNS_NUMBER = 4;
	private String table[][];
	private String overall;
	private String sum;
	private TableLayout tableLayout;
	private TextView overallTextView;
	private TextView sumTextView;
	private Context context;
	private Resources resources;
	
	private String login;
	private String password;
	private String sid;
	
	class Update extends AsyncTask<Void, Integer, Void>{
		protected void onPreExecute(){
			((MainActivity)getActivity()).progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected Void doInBackground(Void... params) {
			Request request = new Request(URL);
			request.addParam(INDEX_NAME, INDEX_VALUE);
			request.addParam(SID_NAME, sid);
			request.addCookie(SID_NAME, sid);
			TagNode result = null;
			try {
				result = request.send(getActivity().getResources());
			} catch (KeyManagementException | CertificateException
					| KeyStoreException | NoSuchAlgorithmException
					| IOException e) {
				e.printStackTrace();
			}
			
			TagNode div = result.findElementByAttValue("class", "kabinet-center_col", true, true);
			if ( div == null ){
				try {
					sid = LoginActivity.login(context, login, password);
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					doInBackground(params);
				}
			} else {
				TagNode[] divs = div.getElementsByAttValue("class", "kabinet-styled_table-wrap box_shadow border_rad", true, true);
				TagNode tbody = divs[0].findElementByName("tbody", true);
				tbody = tbody.findElementByName("tbody", true);
				TagNode trs[] = tbody.getChildTags();
				int trsLength = trs.length;
				table = new String[trsLength-1][COLUMNS_NUMBER];
				sharedPreferencesEditor.putInt(TABLE_LENGTH_KEY, trsLength-1);
				
				for ( int trsIdx = 1; trsIdx < trsLength; ++trsIdx ){
					TagNode[] tds = trs[trsIdx].getChildTags();
					for ( int tdsIdx = 0; tdsIdx < COLUMNS_NUMBER; ++tdsIdx ){
						table[trsIdx-1][tdsIdx] = tds[tdsIdx].getText().toString();
						sharedPreferencesEditor.putString((trsIdx-1)+"_"+tdsIdx, table[trsIdx-1][tdsIdx]);
					}
				}
				
				tbody = divs[1].findElementByName("tbody", true);
				tbody = tbody.findElementByName("tbody", true);
				TagNode tds[] = tbody.getChildTags()[0].getChildTags();
				overall = tds[1].getText().toString();
				sum = tds[3].getText().toString();				
			}
			return null;
		}
		
		protected void onPostExecute ( Void v ){
			fill();
			
			sharedPreferencesEditor
				.putString(OVERALL_KEY, overall)
				.putString(SUM_KEY, sum)
				.apply();

			MainActivity mainActivity = (MainActivity)getActivity(); 
			if ( mainActivity != null ){
				mainActivity.progressBar.setVisibility(View.INVISIBLE);
			}
			cancel(false);
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_payments, container, false);		
		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
		overallTextView = (TextView) rootView.findViewById(R.id.overallTextView);
		sumTextView = (TextView) rootView.findViewById(R.id.sumTextView);
		resources = getResources();

		FragmentActivity fragmentActivity = getActivity();
		context = fragmentActivity.getApplicationContext();
		Intent intent = fragmentActivity.getIntent();
		sid = intent.getStringExtra(LoginActivity.SID_NAME);
		login = intent.getStringExtra(LoginActivity.LOGIN_NAME);
		password = intent.getStringExtra(LoginActivity.PASSWORD_NAME);
		
		/*Request*/
		new Update().execute();
		
		/*Use cache*/
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		int tableLength = sharedPreferences.getInt(TABLE_LENGTH_KEY, 0);
		if ( table == null ){
			table = new String[tableLength][COLUMNS_NUMBER];
			for (int row = 0; row < tableLength; row++) {
				for (int col = 0; col < COLUMNS_NUMBER; col++) {
					if ( table[row][col] == null ){
						table[row][col] = sharedPreferences.getString(row+"_"+col, null);
					}
				}
			}
		}
		if ( overall == null ){
			overall = sharedPreferences.getString(OVERALL_KEY, "");
		}
		if ( sum == null ){
			sum = sharedPreferences.getString(SUM_KEY, "");
		}
		if ( tableLayout.getChildCount() == 0 ){
			fill();
		}
		
		sharedPreferencesEditor = sharedPreferences.edit();
		
		return rootView;
	}

	void fill(){
		tableLayout.removeAllViews();
		
		/*Add headers*/
		TextView dateTextView = new TextView(context);
		dateTextView.setText(R.string.date_header_text_view);
		//dateTextView.setTextColor(android.R.attr.textColor);
		dateTextView.setPadding(0, 0, 10, 0);
		TextView descriptionTextView = new TextView(context);
		descriptionTextView.setText(R.string.description_header_text_view);
		//descriptionTextView.setTextColor(android.R.attr.textColor);
		descriptionTextView.setPadding(0, 0, 10, 0);
		TextView sumHeaderTextView = new TextView(context);
		sumHeaderTextView.setText(R.string.sum_header_text_view);
		//sumHeaderTextView.setTextColor(android.R.attr.textColor);
		sumHeaderTextView.setPadding(0, 0, 10, 0);
		TextView depositTextView = new TextView(context);
		depositTextView.setText(R.string.deposit_header_text_view);
		//depositTextView.setTextColor(android.R.attr.textColor);
		depositTextView.setPadding(0, 0, 10, 0);
		TableRow tableHeaderRow = new TableRow(context);
		tableHeaderRow.addView(dateTextView);
		tableHeaderRow.addView(descriptionTextView);
		tableHeaderRow.addView(sumHeaderTextView);
		tableHeaderRow.addView(depositTextView);
		tableLayout.addView(tableHeaderRow);
		
		/*Add table*/
		int tableLength = table.length;
		for ( int row = 0; row < tableLength; ++row ){
			TableRow tableRow = new TableRow(context);
			for (int col = 0; col < COLUMNS_NUMBER; col++) {
				TextView textView = new TextView(context);
				//textView.setTextColor(android.R.attr.textColor);
				textView.setText(table[row][col]);
				textView.setPadding(0, 0, 10, 0);
				tableRow.addView(textView);
			}			
			tableLayout.addView(tableRow);
		}
		
		/*Add Info*/
		overallTextView.setText( resources.getString(R.string.overall_key_text_view ) + " " + overall );
		sumTextView.setText( resources.getString(R.string.sum_key_text_view ) + " " + sum );		

	}

	
}
