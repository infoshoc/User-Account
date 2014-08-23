package ua.infoshoc.megastyle;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.htmlcleaner.TagNode;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PaymentsFragment extends DataDisplayFragment {

	/* for cache */
	public final static String SHARED_PREFERENCES_NAME = "PaymentsFragmentCache";
	public final static String TABLE_LENGTH_KEY = "table_length";
	public final static String OVERALL_KEY = "overall";
	public final static String SUM_KEY = "sum";

	/* for request */
	private static final String INDEX_VALUE = "42";

	/* for storage */
	private static final int COLUMNS_NUMBER = 4;
	private String table[][];
	private String overall;
	private String sum;
	private TableLayout tableLayout;
	private TextView overallTextView;
	private TextView sumTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_payments, container,
				false);
		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
		overallTextView = (TextView) rootView
				.findViewById(R.id.overallTextView);
		sumTextView = (TextView) rootView.findViewById(R.id.sumTextView);

		/* Use cache */
		if (tableLayout.getChildCount() == 0) {
			flush();
		}

		return rootView;
	}

	@Override
	public DataDisplayFragment update() throws KeyManagementException,
			CertificateException, KeyStoreException, NoSuchAlgorithmException,
			IOException {
		TagNode div = update(null);
		TagNode[] divs = div.getElementsByAttValue("class",
				"kabinet-styled_table-wrap box_shadow border_rad", true, true);
		TagNode tbody = divs[0].findElementByName("tbody", true);
		tbody = tbody.findElementByName("tbody", true);
		TagNode trs[] = tbody.getChildTags();
		int trsLength = trs.length;
		table = new String[trsLength - 1][COLUMNS_NUMBER];

		for (int trsIdx = 1; trsIdx < trsLength; ++trsIdx) {
			TagNode[] tds = trs[trsIdx].getChildTags();
			for (int tdsIdx = 0; tdsIdx < COLUMNS_NUMBER; ++tdsIdx) {
				table[trsIdx - 1][tdsIdx] = tds[tdsIdx].getText().toString();
			}
		}

		tbody = divs[1].findElementByName("tbody", true);
		tbody = tbody.findElementByName("tbody", true);
		TagNode tds[] = tbody.getChildTags()[0].getChildTags();
		overall = tds[1].getText().toString();
		sum = tds[3].getText().toString();

		return this;
	}

	@Override
	protected DataDisplayFragment saveCache() {
		sharedPreferencesEditor.putInt(TABLE_LENGTH_KEY, table.length);
		for (int row = 0; row < table.length; row++) {
			for (int col = 0; col < COLUMNS_NUMBER; col++) {
				if (table[row][col] != null) {
					sharedPreferencesEditor.putString(row + "_" + col,
							table[row][col]);
				}
			}
		}
		if (overall != null) {
			sharedPreferencesEditor.putString(OVERALL_KEY, overall);
		}
		if (sum != null) {
			sharedPreferencesEditor.putString(SUM_KEY, sum);
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
		if (overall == null) {
			overall = sharedPreferences.getString(OVERALL_KEY, "");
		}
		if (sum == null) {
			sum = sharedPreferences.getString(SUM_KEY, "");
		}
		return this;
	}

	@Override
	protected DataDisplayFragment flush() {
		if (tableLayout != null) {
			tableLayout.removeAllViews();

			/* Add headers */
			TableRow tableHeaderRow = new TableRow(context);
			tableHeaderRow
					.addView(makeTextView(R.string.date_header_text_view));
			tableHeaderRow
					.addView(makeTextView(R.string.description_header_text_view));
			tableHeaderRow.addView(makeTextView(R.string.sum_header_text_view));
			tableHeaderRow.addView(makeTextView(
					R.string.deposit_header_text_view, false));
			tableLayout.addView(tableHeaderRow);

			/* Add table */
			int tableLength = table.length;
			for (int row = 0; row < tableLength; ++row) {
				TableRow tableRow = new TableRow(context);
				for (int col = 0; col < COLUMNS_NUMBER; col++) {
					if (table[row][col] != null) {
						tableRow.addView(makeTextView(table[row][col],
								col + 1 != COLUMNS_NUMBER));
					}
				}
				tableLayout.addView(tableRow);
			}

			/* Add Info */
			Resources resources = getContext().getResources();
			if (overallTextView != null) {
				overallTextView.setText(resources
						.getString(R.string.overall_key_text_view)
						+ " "
						+ overall);
			}
			if (sumTextView != null) {
				sumTextView.setText(resources
						.getString(R.string.sum_key_text_view) + " " + sum);
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
