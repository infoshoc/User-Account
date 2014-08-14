package ua.infoshoc.megastyle;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.htmlcleaner.TagNode;

import android.os.Bundle;
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

public class StatisticsFragment extends DataDisplayFragment implements
		OnTouchListener {

	public final static String SHARED_PREFERENCES_NAME = "StatisticsFragmentCache";
	private final static String TABLE_LENGTH_KEY = "table_length";
	private final static String IP_KEY = "ip";
	private final static String CID_KEY = "cid";
	private final static String DURATION_KEY = "duration";
	private final static String RECEIVED_KEY = "received";
	private final static String SENT_KEY = "sent";

	/* for request */
	private final static String INDEX_VALUE = "44";

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

	private ScrollView vScroll;
	private HorizontalScrollView hScroll;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_statistics,
				container, false);
		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
		ipTextView = (TextView) rootView.findViewById(R.id.ipTextView);
		cidTextView = (TextView) rootView.findViewById(R.id.cidTextView);
		durationTextView = (TextView) rootView
				.findViewById(R.id.durationTextView);
		receivedTextView = (TextView) rootView
				.findViewById(R.id.receivedTextView);
		sentTextView = (TextView) rootView.findViewById(R.id.sentTextView);
		flush();

		vScroll = (ScrollView) rootView.findViewById(R.id.vScroll);
		hScroll = (HorizontalScrollView) rootView.findViewById(R.id.hScroll);
		vScroll.setOnTouchListener(this);

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

	@Override
	public DataDisplayFragment update() throws KeyManagementException,
			CertificateException, KeyStoreException, NoSuchAlgorithmException,
			IOException {
		TagNode div = update(null);
		TagNode tableTag = div.findElementByAttValue("id",
				"kabinet-filter_table", true, true).findElementByName("table",
				true);
		TagNode[] trs = tableTag.getElementsByName("tr", true);
		TagNode[] tds = trs[1].getChildTags();
		ip = tds[0].getText().toString();
		cid = tds[1].getText().toString();
		duration = tds[2].getText().toString();
		received = tds[3].getText().toString();
		sent = tds[4].getText().toString();

		div = div.getElementsByAttValue("class",
				"kabinet-styled_table-wrap box_shadow border_rad", true, true)[5];
		tableTag = div.getChildTags()[0].findElementByName("table", true);
		trs = tableTag.getElementsByName("tr", true);
		int trsLength = trs.length;
		table = new String[trsLength][COLUMNS_NUMBER];
		for (int trIdx = 0; trIdx < trsLength; trIdx++) {
			tds = trs[trIdx].getChildTags();
			for (int tdIdx = 0; tdIdx < COLUMNS_NUMBER; ++tdIdx) {
				table[trIdx][tdIdx] = tds[tdIdx].getText().toString();
			}
		}
		return this;
	}

	@Override
	protected DataDisplayFragment saveCache() {
		sharedPreferencesEditor.putInt(TABLE_LENGTH_KEY, table.length);
		for (int row = 0; row < table.length; row++) {
			for (int col = 0; col < COLUMNS_NUMBER; col++) {
				if ( table[row][col] != null ){
					sharedPreferencesEditor.putString(row + "_" + col,
						table[row][col]);
				}
			}
		}
		if ( ip != null ){
			sharedPreferencesEditor.putString(IP_KEY, ip);
		}
		if ( cid != null ){
			sharedPreferencesEditor.putString(CID_KEY, cid);
		}
		if ( duration != null ){
			sharedPreferencesEditor.putString(DURATION_KEY, duration);
		}
		if ( received != null ){
			sharedPreferencesEditor.putString(RECEIVED_KEY, received);
		}
		if ( sent != null ){
			sharedPreferencesEditor.putString(SENT_KEY, sent);
		}
		sharedPreferencesEditor.apply();
		return this;
	}

	@Override
	protected DataDisplayFragment getCache() {
		int tableLength = sharedPreferences.getInt(TABLE_LENGTH_KEY, 0);
		if (table == null) {
			table = new String[tableLength][COLUMNS_NUMBER];
		}
		for (int row = 0; row < tableLength; row++) {
			for (int col = 0; col < COLUMNS_NUMBER; col++) {
				if (table[row][col] == null) {
					table[row][col] = sharedPreferences.getString(row + "_"
							+ col, null);
				}
			}
		}
		if (ip == null) {
			ip = sharedPreferences.getString(IP_KEY, "");
		}
		if (cid == null) {
			cid = sharedPreferences.getString(CID_KEY, "");
		}
		if (duration == null) {
			duration = sharedPreferences.getString(DURATION_KEY, "");
		}
		if (received == null) {
			received = sharedPreferences.getString(RECEIVED_KEY, "");
		}
		if (sent == null) {
			sent = sharedPreferences.getString(SENT_KEY, "");
		}
		return this;
	}

	@Override
	protected DataDisplayFragment flush() {
		if (ipTextView != null && ip != null) {
			ipTextView.setText(ip);
		}
		if (cidTextView != null && cid != null) {
			cidTextView.setText(cid);
		}
		if (durationTextView != null && duration != null) {
			durationTextView.setText(duration);
		}
		if (receivedTextView != null && received != null) {
			receivedTextView.setText(received);
		}
		if (sentTextView != null && sent != null) {
			sentTextView.setText(sent);
		}

		if (tableLayout != null) {
			tableLayout.removeAllViews();
			int tableSize = table.length;
			for (int row = 0; row < tableSize; row++) {
				TableRow tableRow = new TableRow(context);
				for (int col = 0; col < COLUMNS_NUMBER; col++) {
					if (table[row][col] != null) {
						tableRow.addView(makeTextView(table[row][col],
								col + 1 != COLUMNS_NUMBER));
					}
				}
				tableLayout.addView(tableRow);
			}
		}
		return this;
	}

	@Override
	protected String getIndexValue() {
		return INDEX_VALUE;
	}

	@Override
	protected String getSharedPreferencesName() {
		return SHARED_PREFERENCES_NAME;
	}

}
