package com.yzrilyzr.floatingwindow.view;

import android.content.Context;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.yzrilyzr.ui.uidata;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.background.Shapes;
import android.view.MotionEvent;

public class PerfTestView extends View
{
	boolean start=false;
	long ns,cns;
	long times,starttime;
	Paint p;
	public Shapes sh=new Shapes(0);
	public PerfTestView(Context c){
		super(c);
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(0xffffffff);
		p.setTextSize(util.px(50));
		p.setTextAlign(Paint.Align.CENTER);
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		sh.onCompute();
		sh.onDraw(canvas);
		if(ns!=0)canvas.drawText("FPS:"+(1000000000l/ns),getWidth()/2,getHeight()/2,p);
		canvas.drawText(Long.toString(10l-(cns-starttime)/1000000000l),getWidth()/2,getHeight()/2+p.getTextSize()*1.5f,p);
		ns=System.nanoTime()-cns;
		cns=System.nanoTime();
		times++;
		if(start)invalidate();
		sh.add();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		sh.onTouchEvent(event);
		return true;
	}
	public boolean finish(){
		return ((cns-starttime)/1000000000l)==10l;
	}
	public void start(){
		sh.pos.clear();
		start=true;
		invalidate();
	}
	public void clear(){
		cns=System.nanoTime();
		starttime=cns;
		times=0;
	}
	public int getAverageFps(){
		start=false;
		return (int)(times*1000000000l/(System.nanoTime()-starttime));
	}
}
