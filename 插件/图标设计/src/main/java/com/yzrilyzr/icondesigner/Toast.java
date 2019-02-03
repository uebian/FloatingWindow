package com.yzrilyzr.icondesigner;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Toast extends Menu
{
	private int time=60;
	private String txt="";
	private Paint paint;
	private float ypos,height;
	public Toast(float x,float y,float w,float h)
	{
		super(x,y,w,h);
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(px(18));
		ypos=top;
		height=height();
	}

	public void show(String s)
	{
		txt=s;
		time=0;
		show=true;
	}

	@Override
	public void onDraw(Canvas c)
	{
		if(time>60)show=false;
		else{
		float pos=0;
		if(time<20)pos=ypos-time*height/20f;
		else if(time>=20&&time<=40)pos=ypos-height;
		else pos=ypos-height+(time-40)*height/20f;
		top=pos;
		bottom=pos+height;
		paint.setColor(toastcolor);
		c.drawRect(this,paint);
		paint.setColor(textcolor);
		c.drawText(txt,centerX(),centerY()+paint.getTextSize()/2,paint);
		time++;
		}
	}
	
}
