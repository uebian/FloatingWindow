package com.yzrilyzr.ui;

import android.content.*;
import android.util.*;

public class myTextViewBack extends myTextView
{
	public myTextViewBack(Context c,AttributeSet a)
	{
		super(c,a);
		init(BACK);
	}
	public myTextViewBack(Context c)
	{
		this(c,null);
	}				
}
