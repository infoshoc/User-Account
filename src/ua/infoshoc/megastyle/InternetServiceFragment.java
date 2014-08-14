package ua.infoshoc.megastyle;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.htmlcleaner.TagNode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InternetServiceFragment extends DataDisplayFragment {

	/* cache */
	public static final String SHARED_PREFERENCES_NAME = "InternetServiceFragment";

	/* for request */
	private static final String INDEX_VALUE = "43";

	/* Positions in html code */
	private static final int SERVICE_NAME_TR_IDX = 0;
	private static final int TARIFF_TR_IDX = 1;
	private static final int SIMULTANEOUSLY_TR_IDX = 2;
	private static final int IP_TR_IDX = 3;
	private static final int NETMASK_TR_IDX = 4;
	private static final int SPEED_TR_IDX = 5;
	private static final int CID_TR_IDX = 6;
	private static final int SERVICE_STATUS_TR_IDX = 7;
	private static final int FINISH_TR_IDX = 8;

	private static final int SERVICE_NAME_IDX = 0;
	private static final int TARIFF_IDX = 1;
	private static final int SIMULTANEOUSLY_IDX = 2;
	private static final int IP_IDX = 3;
	private static final int NETMASK_IDX = 4;
	private static final int SPEED_IDX = 5;
	private static final int CID_IDX = 6;
	private static final int SERVICE_STATUS_IDX = 7;
	private static final int FINISH_IDX = 8;
	private static final int FIELDS_SIZE = 9;

	private CharSequence values[];
	private TextView textViews[];

	public InternetServiceFragment() {
		textViews = new TextView[FIELDS_SIZE];
		values = new CharSequence[FIELDS_SIZE];
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_internet_service,
				container, false);

		/* Set TextViews */
		textViews[SERVICE_NAME_IDX] = (TextView) rootView
				.findViewById(R.id.serviceNameTextView);
		textViews[TARIFF_IDX] = (TextView) rootView
				.findViewById(R.id.tariffTextView);
		textViews[SIMULTANEOUSLY_IDX] = (TextView) rootView
				.findViewById(R.id.simultaneouslyTextView);
		textViews[IP_IDX] = (TextView) rootView.findViewById(R.id.ipTextView);
		textViews[NETMASK_IDX] = (TextView) rootView
				.findViewById(R.id.netmaskTextView);
		textViews[SPEED_IDX] = (TextView) rootView
				.findViewById(R.id.speedTextView);
		textViews[CID_IDX] = (TextView) rootView.findViewById(R.id.cidTextView);
		textViews[SERVICE_STATUS_IDX] = (TextView) rootView
				.findViewById(R.id.serviceStatusTextView);
		textViews[FINISH_IDX] = (TextView) rootView
				.findViewById(R.id.finishTextView);
		flush();

		return rootView;
	}

	@Override
	public DataDisplayFragment update() throws KeyManagementException,
			CertificateException, KeyStoreException, NoSuchAlgorithmException,
			IOException {
		TagNode div = update(null);
		div = div.findElementByAttValue("id", "dv_user_info", true, true);
		TagNode[] trs = div.getElementsByName("tr", true);
		int trsLength = trs.length;
		for (int trsIdx = 0; trsIdx < trsLength; ++trsIdx) {
			TagNode[] tds = trs[trsIdx].getChildTags();
			switch (trsIdx) {
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
		return this;
	}

	@Override
	protected DataDisplayFragment saveCache() {
		for (Integer i = 0; i < FIELDS_SIZE; i++) {
			if ( values[i] != null ){
				sharedPreferencesEditor.putString(i.toString(),
						values[i].toString());
			}
		}
		sharedPreferencesEditor.apply();
		return this;
	}

	@Override
	protected DataDisplayFragment getCache() {
		for (int fieldId = 0; fieldId < FIELDS_SIZE; ++fieldId) {
			if (values[fieldId] == null) {
				values[fieldId] = sharedPreferences.getString(
						Integer.toString(fieldId), null);
			}
		}
		return this;
	}

	@Override
	protected DataDisplayFragment flush() {
		for (int i = 0; i < FIELDS_SIZE; ++i) {
			if (textViews[i] != null && values[i] != null) {
				textViews[i].setText(values[i]);
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
