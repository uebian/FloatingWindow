package com.yzrilyzr.ui;
import android.graphics.*;

import android.graphics.drawable.Drawable;
import android.view.View;
import com.yzrilyzr.myclass.util;
import java.util.ArrayList;


public class myRippleDrawable extends Drawable
{
    protected Paint paint,paint2;
    protected RectF rectF;
    protected float radius;
    protected int margin=0,color;
    private ArrayList<Ripple> ripples=new ArrayList<Ripple>();
    public myRippleDrawable(int color,float radius)
    {
        this.radius=radius;
		this.color=color;
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint2=new Paint(Paint.ANTI_ALIAS_FLAG);
    }
	public myRippleDrawable(int color)
	{
		this(color,util.px(uidata.UI_RADIUS));
	}
    public myRippleDrawable()
    {
		this(uidata.MAIN,util.px(uidata.UI_RADIUS));
    }

	public void setMargin(int margin)
	{
		this.margin = margin;
	}

	public int getMargin()
	{
		return margin;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public int getColor()
	{
		return color;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public float getRadius()
	{
		return radius;
	}
    public void setLayer(View v)
    {
		if(v==null)paint.setShadowLayer(0,0,0,0);
		else
		{
			margin=util.px(4);
			paint.setShadowLayer(util.px(1.5f),0,util.px(2),0x50000000);
			v.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
		}
    }
    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);
        rectF = new RectF(margin,margin, right-margin, bottom-margin);
    }
    @Override
    public void draw(Canvas canvas)
    {
		paint.setColor(color);
        canvas.drawRoundRect(rectF,radius,radius,paint);
        int sc=canvas.saveLayer(rectF,paint2,Canvas.ALL_SAVE_FLAG);
		canvas.clipRect(rectF);
		paint2.setColor(color);
        canvas.drawRoundRect(rectF,radius,radius,paint2);
		paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		float al=0;
		for(int i=0;i<ripples.size();i++)
		{
			Ripple ri=ripples.get(i);
			ri.r+=ri.s;
			al=200f-(ri.r/util.px(100))*200f;
			if(al<0)ripples.remove(i--);
			else
			{
				paint2.setColor(uidata.getAlphacolor(0xffffffff,(int)al));
				canvas.drawCircle(ri.x,ri.y,ri.r,paint2);
			}
		}
		paint2.setXfermode(null);
		canvas.restoreToCount(sc);
        if(ripples.size()>0)invalidateSelf();
    }
	public void longRipple(float x,float y)
    {
        ripples.add(new Ripple(x,y,0,util.dip(25)));
		invalidateSelf();
    }
    public void shortRipple(float x,float y)
    {
		ripples.add(new Ripple(x,y,0,util.dip(50)));
		invalidateSelf();
    }
    public void selector(boolean focus)
    {
        if(focus)ripples.add(new Ripple(0,0,(int)Math.max((double)rectF.width(),(double)rectF.height()),0));
        else ripples.clear();
        invalidateSelf();
    }
    @Override
    public void setAlpha(int alpha)
    {
		color=(color|0xff000000)-0xff000000+alpha;
    }
    @Override
    public void setColorFilter(ColorFilter colorFilter)
    {
		paint.setColorFilter(colorFilter);
    }
    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
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
}
