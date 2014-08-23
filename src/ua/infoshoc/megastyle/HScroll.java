package ua.infoshoc.megastyle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HScroll extends HorizontalScrollView {

	public HScroll(Context context) {
		super(context);
	}

	public HScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}
}