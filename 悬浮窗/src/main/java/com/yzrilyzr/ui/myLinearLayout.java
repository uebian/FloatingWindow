package com.yzrilyzr.ui;
import android.widget.*;
import android.content.*;
import android.util.*;

public class myLinearLayout extends LinearLayout
{
	public myLinearLayout(Context c, AttributeSet a)
	{
		super(c, a);
		setBackgroundColor(uidata.MAIN);
	}
	public myLinearLayout(Context c)
	{
		this(c,null);
	}
}
