package ua.infoshoc.megastyle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InternetFragment extends Fragment implements OnClickListener {

	private Button internetTabButton;
	private Button statisticsTabButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_internet, container, false);

		internetTabButton = (Button) rootView.findViewById(R.id.internet_tab_button);
		internetTabButton.setOnClickListener(this);
		statisticsTabButton = (Button) rootView.findViewById(R.id.statistics_tab_button);
		statisticsTabButton.setOnClickListener(this);
        
		onClick ( internetTabButton );
		
        return rootView;
	}
	
	@Override
	public void onClick(View v) {
		switch ( v.getId() ){
		case R.id.internet_tab_button:
			internetTabButton.setEnabled(false);
			statisticsTabButton.setEnabled(true);
    		getActivity().getActionBar().setTitle(R.string.title_internet);
    		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container1, new InternetServiceFragment())
				.commit();
			break;
		case R.id.statistics_tab_button:
			internetTabButton.setEnabled(true);
			statisticsTabButton.setEnabled(false);
    		getActivity().getActionBar().setTitle(R.string.title_statistics);
    		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container1, new StatisticsFragment())
				.commit();
			break;
		}
	}	
}
