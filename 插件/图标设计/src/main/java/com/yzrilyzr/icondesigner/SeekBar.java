package com.yzrilyzr.icondesigner;

import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;

public class SeekBar extends MView
{
	private float padd;
	private int progress=0,max=100,lpro=0;
	private Paint p;
	public SeekBarEvent e;
	private float kx;
	private boolean tt;
	RectF r;
	
	public SeekBar(float x,float y,float w,float h,int pro,int mx){
		super(x,y,w,h);
		padd=px(4);
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		progress=pro;
		max=mx;
		r=new RectF(left+padd*2,centerY()-padd,right-padd*2,centerY()+padd);
	}
	public SeekBar(float x,float y,float w,float h,int pro,int mx,SeekBarEvent eve){
		this(x,y,w,h,pro,mx);
		e=eve;
	}
	public void setProgress(int progress)
	{
		this.progress = progress;
	}
	public int getProgress()
	{
		return progress;
	}
	public void setMax(int max)
	{
		this.max = max;
	}
	public int getMax()
	{
		return max;
	}
	@Override
	public void onDraw(Canvas c)
	{
		p.setColor(seekbarcolor);
		c.drawRect(r,p);
		p.setColor(0xffffffff);
		c.drawCircle(tt?kx:r.left+r.width()*(float)progress/(float)max,r.centerY(),padd*2,p);
	}
	@Override
	public void onTouchEvent(MotionEvent e)
	{
		if(e.getAction()==MotionEvent.ACTION_UP)tt=false;
		else tt=true;
		kx=e.getX();
		if(kx<r.left)kx=r.left;
		else if(kx>r.right)kx=r.right;
		int g=Math.round(max*(kx-r.left)/r.width());
		if(g<0)g=0;
		if(g>max)g=max;
		progress=g;
		if(this.e!=null&&lpro!=progress)this.e.onChange(this,progress);
		lpro=progress;
	}
	public interface SeekBarEvent{
		public abstract void onChange(SeekBar s,int p);
	}
}
