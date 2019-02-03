package com.yzrilyzr.ui;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.graphics.drawable.*;
import android.view.*;

public class myListView extends ListView
{
	public myListView(Context c,AttributeSet a)
	{
		super(c,a);
		init();
	}
	public myListView(Context c)
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
