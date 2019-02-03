package com.yzrilyzr.floatingwindow.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import com.yzrilyzr.icondesigner.Shape;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.WidgetUtils;
import com.yzrilyzr.ui.uidata;
import java.util.ArrayList;

public class WaveDraw extends View
{
	Path p=new Path();
	Paint pt;
	int ww=0;
	int[] data;
	static ArrayList<PointF> ps=new ArrayList<PointF>();
	ArrayList<PointF> 
	ps2=new ArrayList<PointF>(),
	ps3=new ArrayList<PointF>();
	public WaveDraw(Context c)
	{
		super(c);
		pt=new Paint(Paint.ANTI_ALIAS_FLAG);
		if(ps.size()==0)for(int i=0;i<15;i++)ps.add(new PointF(0,0));
	}
	public void setData(int[] data)
	{
		this.data=data;
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		p.reset();
		float cy=getHeight()/2f;
		canvas.drawColor(uidata.BUTTON);

		pt.setColor(uidata.TEXTMAIN);
		pt.setStyle(Paint.Style.STROKE);
		pt.setStrokeWidth(util.px(2));
		p.moveTo(0,cy);
		p.lineTo(getWidth(),cy);
		canvas.drawPath(p,pt);
		p.reset();
		if(ps2.size()!=ps.size())
		{
			ps2.clear();
			for(PointF p:ps)ps2.add(new PointF(p.x,p.y));
		}
		for(int o=0;o<ps.size();o++)
		{
			PointF l=ps2.get(o);
			l.x=o*getWidth()/(ps.size()-1);
			l.y=ps.get(o).y*getHeight()/32767+cy;
			canvas.drawCircle(l.x,l.y,util.px(4),pt);
		}
		p.moveTo(0,cy);
		ps3.clear();
		Catmull_Rom(ps2,150,p,ps3);
		if(data!=null)
			for(int i=0;i<data.length;i++)
			{
				data[i]=(int)(ps3.get((int)util.limit(i*ps3.size()/data.length,0,ps3.size()-1)).y-cy)*32767/getHeight();
			}
		pt.setColor(uidata.ACCENT);
		canvas.drawPath(p,pt);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		getParent().requestDisallowInterceptTouchEvent(true);
		int a=event.getAction();
		if(a==MotionEvent.ACTION_DOWN)
			ww=(int)util.limit((int)Math.round(event.getX()*(ps.size()-1)/getWidth()),0,ps.size()-1);
		else ps.get(ww).y=(Math.round((event.getY()-getHeight()/2)*32767/getHeight()/409.6f))*409.6f;
		invalidate();
		return true;
	}
	public static final void Catmull_Rom(ArrayList<PointF> point, int cha,Path path,ArrayList<PointF> filled)
	{
		if(cha==0)for(PointF p:point)path.lineTo(p.x,p.y);
		else if (point.size()>1&&cha<10000)
		{
			point.add(0,point.get(0));
			point.add(point.get(point.size()-1));
			for (int index = 1; index < point.size() - 2; index++)
			{
				PointF p0 = point.get(index - 1);
				PointF p1 = point.get(index);
				PointF p2 = point.get(index + 1);
				PointF p3 = point.get(index + 2);
				for (int i = 1; i <= cha; i++)
				{
					float t = i * (1.00f / cha);
					float tt = t * t;
					float ttt = tt * t;
					float x = (float) (0.5 * (2 * p1.x + (p2.x - p0.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * tt + (3 * p1.x - p0.x - 3 * p2.x + p3.x)* ttt));
					float y = (float) (0.5 * (2 * p1.y + (p2.y - p0.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * tt + (3 * p1.y - p0.y - 3 * p2.y + p3.y)* ttt));
					path.lineTo(x,y);
					if(filled!=null)filled.add(new PointF(x,y));
				}
			}
			path.lineTo(point.get(point.size() - 1).x, point.get(point.size() - 1).y);
			if(filled!=null)filled.add(new PointF(point.get(point.size() - 1).x, point.get(point.size() - 1).y));
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,util.px(-1)),WidgetUtils.measure(heightMeasureSpec,util.px(100)));
	}

}
