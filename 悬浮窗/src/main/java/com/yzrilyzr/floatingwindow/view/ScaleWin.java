package com.yzrilyzr.floatingwindow.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class ScaleWin extends View
{
	Paint p;
	int l,t,r,b;
	public ScaleWin(Context c){
		super(c);
		p=new Paint();
		p.setColor(uidata.ACCENT);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(util.px(3));
	}
	public void set(int x,int y,int w,int h){
		l=x;
		t=y;
		r=x+w;
		b=y+h;
		invalidate();
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawRect(l,t,r,b,p);
	}
}
