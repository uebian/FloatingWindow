package com.yzrilyzr.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class myExpandableListView extends ExpandableListView
{
	public myExpandableListView(Context c,AttributeSet a)
	{
		super(c,a);
		init();
	}
	public myExpandableListView(Context c)
	{
		this(c,null);
	}
	private void init()
	{
		setDivider(new ColorDrawable(uidata.MAIN));
		setDividerHeight(2);
		//setScrollingCacheEnabled(false);
		//setAnimationCacheEnabled(false);
	}
	private boolean is=false;
	public void setScrollView(boolean b)
	{
		is=b;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if(is)heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
}
