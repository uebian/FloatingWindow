package com.yzrilyzr.ui;
import android.content.*;
import android.util.*;
import android.widget.*;

public class myLinearLayoutBack extends myLinearLayout
{
	public myLinearLayoutBack(Context c,AttributeSet a)
	{
		super(c,a);
		setBackgroundColor(uidata.BACK);
	}
	public myLinearLayoutBack(Context c)
	{
		this(c,null);
	}
}
