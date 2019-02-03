package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.myclass.util;

public class mySwitch extends Switch
{
	private float drawx=0;
	/*
    private Context ctx;
	  private String method;
	 private boolean isChecked=false,isOn=false,last=false;
	 private OnCheckedChangeListener occl=null;
	 */
    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);

	private boolean isOn;
    public mySwitch(Context c,AttributeSet a)
    {
        super(c,a);
       	setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }
    public mySwitch(Context c)
    {
		this(c,null);
	}
	
    @Override
    protected void onDraw(Canvas canvas)
    {
      	float hei=getHeight(),wid=getWidth();
		float r=hei/2f,r2=hei/6f,r3=hei/3f;
		if(!isOn)drawx=r+(wid-2f*r)*(isChecked()?1:0);
		paint.setColor(isEnabled()?0xffd0d0d0:uidata.getESColor());
		canvas.drawRoundRect(new RectF(r,r3,wid-r,hei-r3),r2,r2,paint);
		if(isEnabled())paint.setColor(uidata.getAFColor());
		canvas.drawRoundRect(new RectF(r,r3,drawx,hei-r3),r2,r2,paint);
		int margin=util.px(2);
		paint.setShadowLayer(margin,0,margin,0x50000000);
		paint.setColor(isEnabled()?(isChecked()?uidata.ACCENT:uidata.CONTROL):0xff555555);
		canvas.drawCircle(drawx,hei/2,r3,paint);
		paint.setShadowLayer(0,0,0,0);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(!isEnabled())return false;
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isOn=false;
                break;
            case MotionEvent.ACTION_MOVE:
				getParent().requestDisallowInterceptTouchEvent(true);
                isOn=true;
				float hei=getHeight(),wid=getWidth();
				float r=hei/2f;
				drawx=util.limit(event.getX(),r,wid-r);
                invalidate();
				return true;
			//case MotionEvent.ACTION_UP:
        }
        return super.onTouchEvent(event);
    }

	@Override
	public void setChecked(boolean checked)
	{
		super.setChecked(checked);
		isOn=false;
	}
	
	@Override
 	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,util.px(50)),WidgetUtils.measure(heightMeasureSpec,util.px(30)));
    }

}
