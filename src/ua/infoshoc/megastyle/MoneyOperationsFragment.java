package ua.infoshoc.megastyle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;


public class MoneyOperationsFragment extends Fragment implements OnClickListener, OnTouchListener{
	
	private View rootView;

    private ScrollView vScroll;
    private HorizontalScrollView hScroll;	
    
    private Button paymentTabButton;
    private Button withdrawalTabButton;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_money_operations, container, false);
		
        vScroll = (ScrollView) rootView.findViewById(R.id.vScroll);
        hScroll = (HorizontalScrollView) rootView.findViewById(R.id.hScroll);
        vScroll.setOnTouchListener(this);
        
        paymentTabButton = (Button) rootView.findViewById(R.id.payment_tab_button);
        withdrawalTabButton = (Button) rootView.findViewById(R.id.withdrawal_tab_button);
        
        paymentTabButton.setOnClickListener(this);
        withdrawalTabButton.setOnClickListener(this);
		

		onClick(paymentTabButton);
		
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
	public void onClick(View v) {
		switch ( v.getId() ){
		case R.id.payment_tab_button:
			//paymentTabButton.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
			paymentTabButton.setEnabled(false);
			//withdrawalTabButton.setBackgroundResource(0);
			withdrawalTabButton.setEnabled(true);
    		getActivity().getActionBar().setTitle(R.string.title_payments);
    		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container1, new PaymentsFragment())
				.commit();
			break;
		case R.id.withdrawal_tab_button:
			//withdrawalTabButton.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
			withdrawalTabButton.setEnabled(false);
			//paymentTabButton.setBackgroundResource(0);
			paymentTabButton.setEnabled(true);
    		getActivity().getActionBar().setTitle(R.string.title_withdrawal);
    		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container1, new WithdrawalFragment())
				.commit();
			break;
		}
	}

}

