package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myToast;

public class UnicodeConvert implements OnClickListener
{
	EditText e1,e2;
	View[] bs=new View[7];
	Context ctx;
	public UnicodeConvert(Context c,Intent e)
	{
		ctx=c;
		Window w=new Window(c,util.px(328),util.px(268))
			.setTitle("Unicode转换")
			.setIcon("unicode")
			.show();
		ViewGroup vg=(ViewGroup)w.addView(R.layout.window_unicode);
		e1=(EditText) ((ViewGroup)vg.getChildAt(0)).getChildAt(0);
		e2=(EditText)((ViewGroup)vg.getChildAt(1)).getChildAt(0);
		ViewGroup vg1=(ViewGroup) ((ViewGroup)vg.getChildAt(0)).getChildAt(1),
		vg2=(ViewGroup) ((ViewGroup)vg.getChildAt(1)).getChildAt(1);
		for(int i=0;i<5;i++)
		{
			bs[i]=vg1.getChildAt(i);
			bs[i].setOnClickListener(this);
		}
		for(int i=5;i<7;i++)
		{
			bs[i]=vg2.getChildAt(i-5);
			bs[i].setOnClickListener(this);
		}
	}
	@Override
	public void onClick(View p1)
	{
		try
		{
			if(p1==bs[0])
			{
				String s=e1.getText().toString();
				s=util.stringToUnicode(s);
				e2.setText(s);
			}
			else if(p1==bs[1])
			{
				String s=e1.getText().toString();
				s=util.unicodeToString(s);
				e2.setText(s);
			}
			else if(p1==bs[2])
			{
				String s=e1.getText().toString();
				util.copy(s);
			}
			else if(p1==bs[3])
			{
				String s=util.paste();
				e1.setText(s);
			}
			else if(p1==bs[4])
			{
				e1.setText("");
				e2.setText("");
			}
			else if(p1==bs[5])
			{
				String sa=e2.getText().toString();
				e1.setText(sa);
			}
			else if(p1==bs[6])
			{
				String s=e2.getText().toString();
				util.copy(s);
			}
		}
		catch(Exception e)
		{
			myToast.s(ctx,"转换失败");
		}
	}
}
