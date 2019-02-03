package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import java.lang.reflect.Method;

public class myToolBar extends myLinearLayout implements OnClickListener
{
	private myTitleButton b1,b2,b3,b4,b0;
	private myTextViewTitle title;
	private String m0,m1,m2,m3,m4;
	private Context ctx;
	private int stH=0;
	public myToolBar(Context c,AttributeSet a)
	{
		super(c,a);
		ctx=c;
		try
		{
			minit(a);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
			stH=util.getStatusBarHeight();
		int margin=util.px(3);
		setPadding(0,stH,0,margin);

	}
	public void setContentSize()
	{
		FrameLayout f=(FrameLayout)getParent();
		View cc=f.getChildAt(0);
		FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)cc.getLayoutParams();
		int mm=util.px(55)+stH;
		if(lp.topMargin!=mm)lp.topMargin=mm;
		lp.height=ViewGroup.LayoutParams.MATCH_PARENT;
		cc.setLayoutParams(lp);
	}
	public myToolBar(Context c)
	{
		this(c,null);
	}
	private drawable draw;
	@Override
	public void onClick(View v)
	{
		if(v==b0)call(m0);
		else if(v==b1)call(m1);
		else if(v==b2)call(m2);
		else if(v==b3)call(m3);
		else if(v==b4)call(m4);
	}
	private void minit(AttributeSet a)
	{
		setBackgroundDrawable(draw=new drawable());
		draw.setLayer(this);
		b1=new myTitleButton(ctx);
		b2=new myTitleButton(ctx);
		b3=new myTitleButton(ctx);
		b4=new myTitleButton(ctx);
		b0=new myTitleButton(ctx);
		title=new myTextViewTitle(ctx);
		title.setLines(1);
		setOrientation(0);
		setGravity(Gravity.CENTER);
		if(a!=null)
		{
			if(setIcon(b0,a,"vec0"))
			{
				b0.setText(a.getAttributeValue(null,"tip0"));
				m0=a.getAttributeValue(null,"onClick0");
				addView(b0);
				if(m0!=null)b0.setOnClickListener(this);
			}
			title.setText(a.getAttributeValue(null,"title"));
			LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);
			lp.setMargins(util.px(10),0,0,0);
			lp.weight=1.0f;
			title.setLayoutParams(lp);
			addView(title);
			title.setGravity(a.getAttributeIntValue(null,"gravity",Gravity.LEFT));
			if(setIcon(b1,a,"vec1"))
			{
				b1.setText(a.getAttributeValue(null,"tip1"));
				m1=a.getAttributeValue(null,"onClick1");
				addView(b1);
				if(m1!=null)b1.setOnClickListener(this);
			}
			if(setIcon(b2,a,"vec2"))
			{
				b2.setText(a.getAttributeValue(null,"tip2"));
				m2=a.getAttributeValue(null,"onClick2");
				addView(b2);
				if(m2!=null)b2.setOnClickListener(this);
			}
			if(setIcon(b3,a,"vec3"))
			{
				b3.setText(a.getAttributeValue(null,"tip3"));
				m3=a.getAttributeValue(null,"onClick3");
				addView(b3);
				if(m3!=null)b3.setOnClickListener(this);
			}
			if(setIcon(b4,a,"vec4"))
			{
				b4.setText(a.getAttributeValue(null,"tip4"));
				m4=a.getAttributeValue(null,"onClick4");
				addView(b4);
				if(m4!=null)b4.setOnClickListener(this);
			}
		}
	}
	private void call(String mname)
	{
		try
		{
			Method c=getContext().getClass().getMethod(mname,View.class);
			c.invoke(getContext(),this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	public myTitleButton getButton(int i)
	{
		switch(i)
		{
			case 0:return b0;
			case 1:return b1;
			case 2:return b2;
			case 3:return b3;
			case 4:return b4;
			default:return null;
		}
	}
	public void setTitle(String s)
	{
		title.setText(s);
	}
	private class drawable extends Drawable
	{
		private int margin,w,h;
		private Paint paint;

		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			// TODO: Implement this method
			super.setBounds(left, top, right, bottom);
			w=right;h=bottom;
		}

		public drawable()
		{
			paint=new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(uidata.MAIN);
		}
		@Override
		public void draw(Canvas p1)
		{
			// TODO: Implement this method
			p1.drawRect(0,0,w,h-margin,paint);
		}
		public void setLayer(View v)
		{
			margin=util.px(3);
			paint.setShadowLayer(margin,0,margin/3,0x50000000);
			v.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
		}
		@Override
		public void setAlpha(int p1)
		{
			// TODO: Implement this method
		}

		@Override
		public void setColorFilter(ColorFilter p1)
		{
			// TODO: Implement this method
		}

		@Override
		public int getOpacity()
		{
			// TODO: Implement this method
			return 0;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO: Implement this method
		super.onSizeChanged(w, h, oldw, oldh);
		setContentSize();
	}
	public static boolean setIcon(ImageView v,AttributeSet a,String value)
	{
		String p=a.getAttributeValue(null,value);
		if(a==null||p==null)return false;
		try
		{
			v.setImageBitmap(VECfile.createBitmap(v.getContext(),p,util.px(50),util.px(50)));
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,getWidth()),WidgetUtils.measure(heightMeasureSpec,util.px(58)+stH));
	}
}
