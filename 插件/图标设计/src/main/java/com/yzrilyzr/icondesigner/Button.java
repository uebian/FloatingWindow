package com.yzrilyzr.icondesigner;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Button extends MView
{
	public String txt;
	public Event e;
	public boolean touch=false;
	public Paint pa;
	public int color=buttoncolor;
	//public boolean selected=false;
	public float rad=px(3);
	private CopyOnWriteArrayList<Ripple> ripples=new CopyOnWriteArrayList<Ripple>();
	Bitmap icon;
	public Button(float x,float y,float w,float h,String txt,String vecpath,Event e)
	{
		super(x+3,y+3,w-6,h-6);
		this.txt=txt;
		this.e=e;
		pa=new Paint(Paint.ANTI_ALIAS_FLAG);
		pa.setTextAlign(Paint.Align.CENTER);
		pa.setTextSize(px(12));
		pa.setStrokeWidth(px(2));
		setImageVec(vecpath);
	}
	public Button(float x,float y,float w,float h,String txt,Event e)
	{
		this(x,y,w,h,txt,null,e);
	}
	public void setImageVec(String assetPath)
	{
		if(assetPath==null){
			icon=null;
		}
		else
			try
			{
				int k=(int) Math.min(width(),height());
				icon=VECfile.createBitmap(render.ctx,assetPath,k,k);
			}
			catch (Exception e)
			{
			}
	}
	@Override
	public void onDraw(Canvas c)
	{
		/*if(selected)
		{
			pa.setStyle(Paint.Style.STROKE);
			pa.setColor(buttonselectedcolor);
			c.drawRect(this,pa);
		}*/
		pa.setColor(color);
		pa.setStyle(Paint.Style.FILL);
		c.drawRect(this,pa);
		float al=0;
		int l=c.save();
		c.clipRect(this);
		try{
		for(int i=0;i<ripples.size();i++)
		{
			Ripple ri=ripples.get(i);
			ri.r+=ri.s;
			al=200f-(ri.r/px(100))*200f;
			if(al<0)ripples.remove(i--);
			else
			{
				pa.setColor(getAlphacolor(0xffffffff,(int)al));
				c.drawCircle(ri.x,ri.y,ri.r,pa);
			}
		}
		}
		catch(Throwable e){}
		c.restoreToCount(l);
		pa.setColor(textcolor);
		if(icon!=null)c.drawBitmap(icon,centerX()-icon.getWidth()/2,centerY()-icon.getHeight()/2,pa);
		else c.drawText(txt,centerX(),centerY()+pa.getTextSize()/2,pa);
	}
	public static int getAlphacolor(int c,int alpha){
		return (c|0xff000000)-(0x01000000*(0xff-alpha));
	}
	@Override
	public void onTouchEvent(MotionEvent k)
	{
		int a=k.getAction();float x=k.getX(),y=k.getY();
		render.setInfo(txt);
		if(a==MotionEvent.ACTION_DOWN&&contains(x,y))
		{
			touch=true;
			ripples.add(new Ripple(x,y,0,dip(50)));
		}
		if(a==MotionEvent.ACTION_UP)
		{
			if(contains(x,y)&&touch&&e!=null)e.e(this);
			touch=false;
			render.setInfo("");
		}
	}
	static final class Ripple
	{
		float x,y,r,s;
		public Ripple(float x, float y, float r, float s)
		{
			this.x = x;
			this.y = y;
			this.r = r;
			this.s = s;
		}
	}
	public interface Event
	{
		public abstract void e(Button b);
	}
}
