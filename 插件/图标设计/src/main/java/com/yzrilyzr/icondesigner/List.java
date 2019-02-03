package com.yzrilyzr.icondesigner;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.view.MotionEvent;

public class List extends Menu
{
	private Paint p;
	private float yy=0,dy=0,vy=0,ht=0,lyy;
	private boolean touch=false,isScroll=false,onPicker=false;
	private int scralpha=0;
	public List(float x,float y,float w,float h,MView... m)
	{
		super(x,y,w,h,m);
		yy=top;
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	public List(float x,float y,float w,float h)
	{
		this(x,y,w,h,null);
	}
	public void measure()
	{
		float yyy=0;
		for(MView v:views)yyy+=v.height()+1;
		ht=yyy;
	}
	@Override
	public void onDraw(Canvas c)
	{
		if(!show)
		{
			vy=0;
			return;
		}
		p.setColor(menucolor);
		c.drawRect(this,p);
		int l=c.save();
		c.clipRect(this);
		if(vy!=0&&!touch)
		{
			yy+=(vy*=0.95)*5f;
			if(vy>-0.1&&vy<0.1)
			{vy=0;}
		}
		if(yy>top||ht<height())
		{yy=top;vy=0;}
		else if(ht>height()&&yy+ht<bottom)
		{yy=bottom-ht;vy=0;}
		float y=yy;
		p.setColor(buttoncolor);
		for(MView v:views)
		{
			float h=v.height();
			if(y+h>top&&y<bottom)
			{
				v.top=y;
				v.bottom=y+h;
				v.onDraw(c);
				c.drawLine(left,v.bottom,right,v.bottom,p);
			}
			else if(y>bottom)break;
			y+=h+1;
		}
		if(ht!=0)
		{
			if(scralpha<255&&vy==0)scralpha+=51;
			p.setColor(seekbarcolor-scralpha*0x1000000);
			float sy=top-(yy-top)/ht*height();
			c.drawRect(right-10,sy,right,sy+height()*height()/ht,p);
		}
		c.restoreToCount(l);
	}
	@Override
	public void onTouchEvent(MotionEvent e)
	{
		scralpha=0;
		float y=e.getY();
		int a=e.getAction();
		if(!isScroll||onPicker){
			super.onTouchEvent(e);
			if(onPicker)return;
		}
		if(a==MotionEvent.ACTION_DOWN)
		{
			touch=true;
			lyy=yy;
			dy=y;
			for(MView v:views)
				if(v instanceof FloatPicker&&v.contains(e.getX(),y))onPicker=true;
		}
		else if(a==MotionEvent.ACTION_MOVE)
		{
			vy=y-dy;
			yy+=vy;
			dy=y;
			if(lyy>yy+5||lyy<yy-5)isScroll=true;
		}
		else if(a==MotionEvent.ACTION_UP)
		{
			touch=false;
			isScroll=false;
			onPicker=false;
		}
	}
}
