package com.yzrilyzr.ui;
import android.content.*;
import android.util.*;

public class myTextViewTitle extends myTextView
{
	public myTextViewTitle(Context c,AttributeSet a)
	{
		super(c,a);
		init(TITLE);
	}
	public myTextViewTitle(Context c)
	{
		this(c,null);
	}				
}
