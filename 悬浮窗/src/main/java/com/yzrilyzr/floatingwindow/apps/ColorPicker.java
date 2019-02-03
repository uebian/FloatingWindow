package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myColorPicker;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.mySeekBar;
import com.yzrilyzr.ui.myTextViewBack;
import android.view.Gravity;

public class ColorPicker implements myColorPicker.OnPickListener
{

	private Context ctx;
	private myColorPicker p;
	private Window t;
	private int rescode;
	public ColorPicker(Context c,Intent e)
	{
		ctx=c;
		p=new myColorPicker(ctx);
		t=new Window(ctx,util.px(250),util.px(270))
			.setCanResize(false)
			.setTitle("调色盘")
			.setBar(0,8,0)
			.setCanFocus(false)
			.setIcon("colorpicker")
			.setParent(e)
			.addView(p);
		for(int i=0;i<4;i++)
		{
			p.argb[i]=new mySeekBar(ctx);
			p.argb[i].setMax(255);
			p.argb[i].setOnSeekBarChangeListener(p);
			t.addView(p.argb[i]);
		}
		p.tv=new myTextViewBack(ctx);
		t.addView(p.tv);
		util.setWeight(p.tv);
		p.tv.setGravity(Gravity.CENTER);
		rescode=e.getIntExtra("rescode",0);
		p.setColor(e.getIntExtra("color",0xffffffff));
		t.show();
		p.setOnPickListener(this);
	}
	@Override
	public void onPick(boolean cancel)
	{
		if(!cancel&&rescode!=0)
		{
			API.callBack(ctx,new Intent().putExtra("color",p.getColor()),rescode);
		}
		t.dismiss();
	}
}
