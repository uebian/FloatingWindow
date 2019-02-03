package com.yzrilyzr.icondesigner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import com.yzrilyzr.icondesigner.MView;
import java.util.ArrayList;
import java.util.UUID;

public class MView extends RectF
{
	public static int
	shadowcolor=0x55000000,
	buttoncolor=0xffff9800,
	menucolor=0xc0ffe0b2,
	editcolor=0xffffffff,
	buttonselectedcolor=0xff50ff50,
	seekbarcolor=0xffff9800,
	unavailablecolor=0xff909090,
	toastcolor=0xffff9800,
	textcolor=0xff000000;
	public static RenderThread render;
	public MView parent;
	public UUID uuid=null;
	public void onDraw(Canvas c)
	{}
	public void onTouchEvent(MotionEvent e)
	{}
	public MView(float x,float y,float w,float h)
	{
		super(px(x),px(y),px(x+w),px(y+h));
		uuid=UUID.randomUUID();
	}

	@Override
	public boolean equals(Object o)
	{
		if(o!=null&&o instanceof MView)
			return uuid.equals(((MView)o).uuid);
		return super.equals(o);
	}
	
	public static int px(float i)
	{
		return (int)(i*render.dpi);
	}
	public static int dip(float i)
	{
		return (int)(i/render.dpi);
	}
}
