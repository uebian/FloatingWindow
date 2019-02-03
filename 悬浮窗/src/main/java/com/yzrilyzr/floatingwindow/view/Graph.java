package com.yzrilyzr.floatingwindow.view;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.WidgetUtils;
import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Path;
import com.yzrilyzr.ui.uidata;

public class Graph extends View
{
	private Paint pa=new Paint(Paint.ANTI_ALIAS_FLAG);
	private ArrayList<Integer> ya=new ArrayList<Integer>();
	private Path path=new Path();
	private int max;
	public Graph(Context c,AttributeSet a)
	{
		super(c,a);

	}
	public Graph(Context c)
	{
		this(c,null);
	}
	public void setMax(int i)
	{
		max=i;
	}
	public void addPoint(int i)
	{
		ya.add(i);
		invalidate();
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(0);
		pa.setStyle(Paint.Style.STROKE);
		pa.setStrokeWidth(util.px(2));
		pa.setColor(uidata.ACCENT);
		int k=getWidth();
		path.reset();
		for(int i=ya.size()-1;i>=0;i--)
		{
			if(i==ya.size()-1)path.moveTo(k,getHeight()-(float)ya.get(i)*(float)getHeight()/(float)max);
			else path.lineTo(k,getHeight()-(float)ya.get(i)*(float)getHeight()/(float)max);
			k-=getWidth()/50;
		}
		canvas.drawPath(path,pa);
		canvas.drawRect(0,0,getWidth(),getHeight(),pa);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,-1),WidgetUtils.measure(heightMeasureSpec,util.px(100)));
	}

}
