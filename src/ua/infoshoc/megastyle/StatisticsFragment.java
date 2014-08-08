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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StatisticsFragment extends Fragment implements OnTouchListener {

	private SharedPreferences.Editor sharedPreferencesEditor;
	public final static String SHARED_PREFERENCES_NAME = "StatisticsFragmentCache";
	private final static String TABLE_LENGTH_KEY = "table_length";
	private final static String IP_KEY = "ip";
	private final static String CID_KEY = "cid";
	private final static String DURATION_KEY = "duration";
	private final static String RECEIVED_KEY = "received";
	private final static String SENT_KEY = "sent";
	
	
	/*for request*/
	private final static String URL = "https://bills.megastyle.com:9443/index.cgi";
	private final static String INDEX_NAME = "index";
	private final static String INDEX_VALUE = "44";
	private final static String SID_NAME = "sid";

	private final static int COLUMNS_NUMBER = 4;
	private String[][] table;

	private TableLayout tableLayout;
	private TextView ipTextView;
	private TextView cidTextView;
	private TextView durationTextView;
	private TextView receivedTextView;
	private TextView sentTextView;
	
	private String ip;
	private String cid;
	private String duration;
	private String received;
	private String sent;
	
	private Context context;
	private String sid;
	private String login;
	private String password;
	
	
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
				}finally{
					doInBackground(params);
				}
			} else {
				TagNode tableTag = div.findElementByAttValue("id", "kabinet-filter_table", true, true).findElementByName("table", true);
				TagNode[] trs = tableTag.getElementsByName("tr", true);
				TagNode[] tds = trs[1].getChildTags();
				ip = tds[0].getText().toString();
				cid = tds[1].getText().toString();
				duration = tds[2].getText().toString();
				received = tds[3].getText().toString();
				sent = tds[4].getText().toString();				
				
				div = div.getElementsByAttValue("class", "kabinet-styled_table-wrap box_shadow border_rad", true, true)[5];
				tableTag = div.getChildTags()[0].findElementByName("table", true);
				trs = tableTag.getElementsByName("tr", true);
				int trsLength = trs.length;
				sharedPreferencesEditor.putInt(TABLE_LENGTH_KEY, trsLength);
				if ( table == null || table.length < trsLength ) {
					table = new String[trsLength][COLUMNS_NUMBER];
				}
				for (int trIdx = 0; trIdx < trsLength; trIdx++) {
					tds = trs[trIdx].getChildTags();
					for (int tdIdx = 0; tdIdx < COLUMNS_NUMBER; ++tdIdx ){
						table[trIdx][tdIdx] = tds[tdIdx].getText().toString();
						sharedPreferencesEditor.putString(trIdx+"_"+tdIdx, table[trIdx][tdIdx]);
					}
				}
			}

			return null;
			
		}
		
		protected void onPostExecute ( Void result ){
			fill();
			sharedPreferencesEditor.putString(IP_KEY, ip)
				.putString(CID_KEY, cid)
				.putString(DURATION_KEY, duration)
				.putString(RECEIVED_KEY, received)
				.putString(SENT_KEY, sent)
				.commit();

			MainActivity mainActivity = (MainActivity)getActivity();
			if ( mainActivity != null ){
				mainActivity.progressBar.setVisibility(View.INVISIBLE);
			}
			cancel(false);
		}	
		
	}
	

    private ScrollView vScroll;
    private HorizontalScrollView hScroll;	
    	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
		ipTextView = (TextView) rootView.findViewById(R.id.ipTextView);
		cidTextView = (TextView) rootView.findViewById(R.id.cidTextView);
		durationTextView = (TextView) rootView.findViewById(R.id.durationTextView);
		receivedTextView = (TextView) rootView.findViewById(R.id.receivedTextView);
		sentTextView = (TextView) rootView.findViewById(R.id.sentTextView);

        vScroll = (ScrollView) rootView.findViewById(R.id.vScroll);
        hScroll = (HorizontalScrollView) rootView.findViewById(R.id.hScroll);
        vScroll.setOnTouchListener(this);
		
		FragmentActivity fragmentActivity = getActivity();
		context = fragmentActivity.getApplicationContext();
		Intent intent = fragmentActivity.getIntent();
		sid = intent.getStringExtra(LoginActivity.SID_NAME);
		login = intent.getStringExtra(LoginActivity.LOGIN_NAME);
		password = intent.getStringExtra(LoginActivity.PASSWORD_NAME);
		
		/*Request*/
		new Update().execute();
	
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		int tableLength = sharedPreferences.getInt(TABLE_LENGTH_KEY, 0);
		if ( table == null ){
			table = new String[tableLength][COLUMNS_NUMBER];
		}
		for (int row = 0; row < tableLength; row++) {
			for (int col = 0; col < COLUMNS_NUMBER; col++) {
				if ( table[row][col] == null ){
					table[row][col] = sharedPreferences.getString(row+"_"+col, null);
				}
			}
		}
		if ( ip == null ){
			ip = sharedPreferences.getString(IP_KEY, "");
		}
		if ( cid == null ){
			cid = sharedPreferences.getString(CID_KEY, "");
		}
		if ( duration == null ){
			duration = sharedPreferences.getString(DURATION_KEY, "");
		}
		if ( received == null ){
			received = sharedPreferences.getString(RECEIVED_KEY, "");
		}
		if ( sent == null ){
			sent = sharedPreferences.getString(SENT_KEY, "");
		}
		
		if ( tableLayout.getChildCount() == 0 ){
			fill();
		}
		
		sharedPreferencesEditor = sharedPreferences.edit();
		
		return rootView;
	}

    private float mx, my;
	@Override
	public boolean onTouch(View v, MotionEvent event) {

        float curX, curY;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                mx = curX;
                my = curY;
                break;
            case MotionEvent.ACTION_UP:
                curX = event.getX();
                curY = event.getY();
                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                break;
        }

        return true;
	}
	
	void fill(){
		ipTextView.setText(ip);
		cidTextView.setText(cid);
		durationTextView.setText(duration);
		receivedTextView.setText(received);
		sentTextView.setText(sent);
		
		tableLayout.removeAllViews();
		int tableSize = table.length;
		for (int row = 0; row < tableSize; row++) {
			TableRow tableRow = new TableRow(context);
			for (int col = 0; col < COLUMNS_NUMBER; col++) {
				TextView textView = new TextView(context);
				textView.setText(table[row][col]);
				textView.setPadding(0, 0, 10, 0);			
				tableRow.addView(textView);
			}
			tableLayout.addView(tableRow);
		}
	}
	
}
