package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class mySpinner extends Spinner
{
	Paint p;
	private myRippleDrawable mrd;
	public mySpinner(Context c,AttributeSet a)
	{
		super(c,a);
		init();
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		mrd=new myRippleDrawable(isEnabled()?uidata.BUTTON:uidata.getBFColor(),util.px(uidata.UI_RADIUS));
        mrd.setLayer(isEnabled()?this:null);
        setBackgroundDrawable(mrd);
	}
	@Override
	public void setEnabled(boolean enabled)
	{
		// TODO: Implement this method
		super.setEnabled(enabled);
		mrd.setLayer(enabled?this:null);
		mrd.setColor(enabled?uidata.BUTTON:uidata.getBFColor());
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		Path t=new Path();
		float dx=getWidth()-util.px(25);
		float w=util.px(20);
		float cy=getHeight()/2;
		t.moveTo(dx+w*3/20,cy-util.px(3));
		t.lineTo(dx+w*17/20,cy-util.px(3));
		t.lineTo(dx+w/2,cy+util.px(4));
		t.close();
		p.setColor(uidata.ACCENT);
		canvas.drawPath(t,p);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO: Implement this method
		return super.onTouchEvent(event);
	}
	private void init()
	{
        Class<?> myClass = Spinner.class;
        try
		{
            Class<?>[] params = new Class[1];
            params[0] = OnItemClickListener.class;
            Method m = myClass.getDeclaredMethod("setOnItemClickListenerInt", params);
            m.setAccessible(true);
            m.invoke(this, new OnItemClickListener() {@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						Class<?> myClass = AdapterView.class;
						try
						{
							Field field = myClass.getDeclaredField("mOldSelectedPosition");
							field.setAccessible(true);
							field.setInt(mySpinner.this, AdapterView.INVALID_POSITION);
						}
						catch (NoSuchFieldException | IllegalAccessException e)
						{
							e.printStackTrace();
						}
					}
				});
		}
		catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
    }
}
