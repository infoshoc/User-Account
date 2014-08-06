package ua.infoshoc.megastyle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/*import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.htmlcleaner.TagNode;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;*/

public class SupportFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_support, container, false);

		for (int childIdx = 0; childIdx < rootView.getChildCount(); childIdx++) {
			rootView.getChildAt(childIdx).setOnClickListener(this);
		}
		
		return rootView;
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ ((TextView)v).getText()));
		startActivity(intent);
	}
	
	/*for request*/
	/*private static final String URL = "https://bills.megastyle.com:9443/index.cgi";
	private static final String INDEX_NAME = "index";
	private static final String INDEX_VALUE = "48";
	private static final String SID_NAME = "sid";
	private static final String ID_NAME = "ID";
	private static final String ID_VALUE = "";
	private static final String SUBJECT_NAME = "SUBJECT";
	private static final String CHAPTER_NAME = "CHAPTER";
	private static final String MESSAGE_NAME = "MESSAGE";
	private static final String ATTACHMENT_NAME = "FILE_UPLOAD";
	private static final String STATE_NAME = "STATE";
	private static final String PRIORITY_NAME = "PRIORITY";

	private EditText subjectEditText;
	private Spinner chapterSpinner;
	private EditText messageEditText;
	private Spinner stateSpiner;
	private Spinner prioritySpiner;
	
	private String sid;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		sid = getActivity().getIntent().getStringExtra(LoginActivity.SID_NAME);
		
		View rootView = inflater.inflate(R.layout.fragment_support, container, false);

		subjectEditText = (EditText) rootView.findViewById(R.id.subjectEditText);
		chapterSpinner = (Spinner) rootView.findViewById(R.id.chapterSpinner);
		messageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
		stateSpiner = (Spinner) rootView.findViewById(R.id.stateSpinner);
		prioritySpiner = (Spinner) rootView.findViewById(R.id.prioritySpinner);
		
		Button sendButton = (Button)rootView.findViewById(R.id.sendButton);
		sendButton.setOnClickListener(this);
		
		return rootView;		
	}

	@Override
	public void onClick(View view) {
 		int viewId = view.getId();
		switch ( viewId ){
		case R.id.sendButton:
			
			CharSequence errorMessage = null;
			if ( subjectEditText.getText().length() == 0 ){
				errorMessage = "Enter subject";
			} else if ( chapterSpinner.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
				errorMessage = "Select chapter";
			} else if ( messageEditText.getText().length() == 0 ){
				errorMessage = "Enter message";
			} else if ( stateSpiner.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
				errorMessage = "Select state";
			} else if ( prioritySpiner.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
				errorMessage = "Select priority";
			}		
			
			Context context = getActivity().getApplicationContext();
			if ( errorMessage != null ){
				Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
			} else {
				new AsyncTask<Context, Long, TagNode>() {
					@Override
					protected TagNode doInBackground(Context... params) {
						try {
							return new MultipartFormData(params[0], URL, SID_NAME+"="+sid, "utf-8")
								.add(INDEX_NAME, INDEX_VALUE)
								.add(SID_NAME, sid)
								.add(ID_NAME, ID_VALUE)
								.add(SUBJECT_NAME, subjectEditText.getText().toString())
								.add(CHAPTER_NAME, Integer.toString(chapterSpinner.getSelectedItemPosition()+1))
								.add(MESSAGE_NAME, messageEditText.getText().toString())
								.add(ATTACHMENT_NAME, (File)null)
								.add(STATE_NAME, Integer.toString(stateSpiner.getSelectedItemPosition()))
								.add(PRIORITY_NAME, Integer.toString(prioritySpiner.getSelectedItemPosition()))
								.send();
						} catch (KeyManagementException | KeyStoreException
								| NoSuchAlgorithmException | CertificateException
								| IOException e) {      
							e.printStackTrace();
						}
						return null;
					}
					protected void onPostExecute(TagNode result) {
						TagNode div = result.findElementByAttValue("id", "info_message", true, true);
						if ( div == null ){
							Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getActivity().getApplicationContext(), div.getText(), Toast.LENGTH_LONG).show();
						}
					}
				}.execute(context);	
			}
		}
	}*/
	
}
