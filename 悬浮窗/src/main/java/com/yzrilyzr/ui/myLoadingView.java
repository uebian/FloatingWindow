package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.yzrilyzr.myclass.util;
//import com.yzrilyzr.myclass.*;

public class myLoadingView extends View
{
//myLog ml;
    public myLoadingView(Context c)
    {
        this(c,null);
    }
    public myLoadingView(Context c,AttributeSet a)
    {
        super(c,a);
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(uidata.ACCENT);
        paint.setStyle(Paint.Style.STROKE);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        int w=getWidth(),h=getHeight();
        float r=(float)Math.sqrt((double)(w*h))/10;
        paint.setStrokeWidth(r);
		
		if(type==1)sf+=ac;
		else if(type==2)sf-=ac;
		else if(type==3)ss+=ac;
		else if(type==4)ss-=ac;
		first+=util.limit(sf,0,mv)+3;
		second+=util.limit(ss,0,mv)+3;
		float sweep=first-second;
		if(sweep>=180&&type==1)type=2;
		else if(sf<0&&type==2)type=3;
		else if(sweep<=180&&type==3)type=4;
		else if((ss<0)&&type==4)type=1;
		canvas.drawArc(new RectF(r,r,w-r,h-r),second,sweep,false,paint);
		invalidate();
    }
    public Paint paint;
    private int type=1;
    private float first=30,second=0,sf=0,ss=0,mv=20,ac=1f;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int i=util.px(60);
        setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,i),WidgetUtils.measure(heightMeasureSpec,i));
    }

}
