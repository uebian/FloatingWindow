package com.yzrilyzr.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.yzrilyzr.myclass.util;

public class myToast extends Toast
{
	myTextView tv;
	public myToast(Context c)
	{
		super(c);
		LinearLayout l=new myLinearLayoutRipple(c){
			@Override public void onDown(View v,MotionEvent e)
			{

			}
			@Override public boolean onLongClick(View v,MotionEvent e)
			{
				return true;
			}
		};
		int p=util.px(8);
		l.setPadding(p,p,p,p);
		tv=new myTextViewBack(c);
		l.addView(tv);
		setView(l);
	}
	public static void s(Context c,String s){
		myToast.makeText(c,s,0).show();
	}
	public static void s(Context c,Throwable t){
		s(c,util.getStackTrace(t));
	}
	public static myToast makeText(Context context,CharSequence text, int duration)
	{
		myToast to=new myToast(context);
		to.setText(text);
		to.setDuration(duration==1?5000:1000);
		return to;
	}
	public static myToast makeText(Context context,int resId,int duration) throws Resources.NotFoundException {
		return makeText(context,context.getResources().getString(resId),duration);
	}
	@Override
	public void setText(CharSequence s)
	{
		tv.setText(s);
	}
}
