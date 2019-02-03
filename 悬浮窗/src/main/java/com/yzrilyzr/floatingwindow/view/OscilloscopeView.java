package com.yzrilyzr.floatingwindow.view;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import com.yzrilyzr.myclass.util;

public class OscilloscopeView extends View
{
	int[] data=new int[48000];
	float period=1,gain=1,sr=48000;
	Path path=new Path();
	Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	int avail=48;
	public OscilloscopeView(Context c)
	{
		this(c,null);
	}
	public OscilloscopeView(Context c,AttributeSet a)
	{
		super(c,a);
		p.setColor(0xff10ff86);
		p.setStrokeWidth(util.px(0.7f));
		p.setTextSize(util.px(12));
		p.setStrokeJoin(Paint.Join.ROUND);
	}
	public void append(int[] x)
	{
		int cl=Math.min(avail,x.length);
			System.arraycopy(x,0,data,0,cl);
		//cur+=avail;
		//cur%=x.length;
	}
	public void setSr(int x)
	{
		sr=x;
		data=new int[x];
		setAvail();
	}
	void setAvail()
	{
		avail=(int)Math.floor(sr*period);
	}
	public void setScan(float sc)
	{
		period=sc;
		setAvail();
	}
	public void setGain(float sc)
	{
		gain=sc;
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(0xff000000);
		path.reset();
		try
		{
			float c=getWidth();
			float gap=(float)avail/c;
			for(float i=0;i<c;i++)
			{
				int in=(int)util.limit(i*gap,0,avail);
				float y=util.limit(getHeight()/2+data[in]*gain/getHeight(),0,getHeight());
				if(i==0)path.moveTo(i,y);
				else path.lineTo(i,y);
			}
			p.setStyle(Paint.Style.STROKE);
			//canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,p);
			canvas.drawPath(path,p);
			p.setStyle(Paint.Style.FILL);
			canvas.drawText(String.format("%fHz",(float)data.length/(float)avail),0,getHeight()-p.getTextSize()*1.2f,p);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		invalidate();
	}
} 
