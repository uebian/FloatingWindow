package com.yzrilyzr.ui;

import android.graphics.*;

import android.content.Context;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import com.yzrilyzr.myclass.util;
import android.text.Editable;
import android.widget.TextView;

public class myColorPicker extends View implements SeekBar.OnSeekBarChangeListener
{
	private Paint p;
	private int mColor=0xff000000,cColor=0xff000000;
	public SeekBar[] argb=new SeekBar[4];
	public TextView tv;
	private RectF satu,value;
	private int touchw=-1;
	private OnPickListener onPickListener;
	private float[] hsv=new float[]{0,0,0};
	public myColorPicker(Context c)
	{
		super(c);
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		setLayerType(LAYER_TYPE_SOFTWARE,null);
	}
	public void setOnPickListener(OnPickListener onPickListener)
	{
		this.onPickListener = onPickListener;
	}
	public OnPickListener getOnPickListener()
	{
		return onPickListener;
	}
	public void setColor(int color)
	{
		mColor=color;
		Color.colorToHSV(mColor,hsv);
		argb[0].setProgress(Color.red(mColor));
		argb[1].setProgress(Color.green(mColor));
		argb[2].setProgress(Color.blue(mColor));
		argb[3].setProgress(Color.alpha(mColor));
		tv.setText(Integer.toHexString(mColor));
		invalidate();
	}
	public int getColor()
	{
		return mColor;
	}
	@Override
	public void onDraw(Canvas c)
	{
		satu=new RectF(getWidth()/20,0,getWidth()/4,getHeight());
		value=new RectF(getWidth()*3/4,0,getWidth()*19/20,getHeight());
		float w=getWidth();
		p.setShader(new LinearGradient(0,0,0,getHeight(),Color.HSVToColor(new float[]{hsv[0],1,hsv[2]}),Color.HSVToColor(new float[]{hsv[0],0,hsv[2]}),Shader.TileMode.CLAMP));
		c.drawRect(satu,p);
		p.setShader(new SweepGradient(centerX(),centerY(),new int[]{
										  0xffff0000,0xffff8000,0xffffff00,0xff80ff00,
										  0xff00ff00,0xff00ff80,0xff00ffff,0xff0080ff,
										  0xff0000ff,0xff8000ff,0xffff00ff,0xffff0080,
										  0xffff0000
									  },null));
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(getHeight()/6);
		c.drawCircle(centerX(),centerY(),getHeight()*5f/12f,p);
		p.setShader(null);
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xff000000);
		float rr=getHeight()/4;
		RectF ar=new RectF(centerX()-rr,centerY()-rr,centerX()+rr,centerY()+rr);
		p.setColor(cColor);
		c.drawArc(ar,90,180,true,p);
		p.setColor(mColor);
		c.drawArc(ar,-90,180,true,p);
		p.setShader(new LinearGradient(0,0,0,getHeight(),Color.HSVToColor(new float[]{hsv[0],hsv[1],1}),Color.HSVToColor(new float[]{hsv[0],hsv[2],0}),Shader.TileMode.CLAMP));
		c.drawRect(value,p);
		p.setShader(null);
		p.setColor(0x80000000);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(rr=5);
		c.drawRect(w/20,getHeight()-getHeight()*hsv[1]-rr,w/4,getHeight()-getHeight()*hsv[1]+rr,p);
		c.drawRect(getWidth()-w/20,getHeight()-getHeight()*hsv[2]-rr,getWidth()-w/4,getHeight()-getHeight()*hsv[2]+rr,p);
		p.setStyle(Paint.Style.FILL);
		rr=getHeight()*5f/12f;
		c.drawCircle(centerX()+rr*(float)Math.cos(hsv[0]*Math.PI/180f),centerY()+rr*(float)Math.sin(hsv[0]*Math.PI/180f),getHeight()/10,p);
		p.setColor(mColor);
		c.drawCircle(centerX()+rr*(float)Math.cos(hsv[0]*Math.PI/180f),centerY()+rr*(float)Math.sin(hsv[0]*Math.PI/180f),getHeight()/12,p);
	}
	private int centerX(){
		return getWidth()/2;
	}
	private int centerY(){
		return getHeight()/2;
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		float x=e.getX(),y=e.getY();
		if(e.getAction()==MotionEvent.ACTION_UP)touchw=-1;
		else if(touchw==-1)
		{
			if(satu.contains(x,y))touchw=1;
			else if(value.contains(x,y))touchw=2;
			else{
				float r=(float)Math.sqrt(Math.pow(x-centerX(),2)+Math.pow(y-centerY(),2));
				if(getHeight()/3f<r&&getHeight()/2f>r)touchw=3;
				else if(r<getHeight()/4f&&onPickListener!=null){
					if(x-centerX()>0)onPickListener.onPick(false);
					else onPickListener.onPick(true);
				}
			}
		}
		else if(touchw==1)
		{
			int al=Color.alpha(mColor);
			hsv[1]=(satu.height()-y)/satu.height();
			setColor(Color.HSVToColor(al,hsv));
		}
		else if(touchw==2)
		{
			int al=Color.alpha(mColor);
			hsv[2]=(value.height()-y)/value.height();
			setColor(Color.HSVToColor(al,hsv));
		}
		else if(touchw==3){
			int al=Color.alpha(mColor);
			float xx=x-centerX(),yy=y-centerY();
			float rr=(float)Math.sqrt(Math.pow(xx,2)+Math.pow(yy,2));
			float cos=(float)Math.acos(yy/rr);
			hsv[0]=(float)(cos*180f/Math.PI);
			if(xx>0)hsv[0]=360f-hsv[0];
			hsv[0]+=90f;
			hsv[0]%=360f;
			setColor(Color.HSVToColor(al,hsv));
		}
		return true;
	}

	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onProgressChanged(SeekBar s, int p, boolean p3)
	{
		int a=Color.alpha(mColor);
		int r=Color.red(mColor);
		int g=Color.green(mColor);
		int b=Color.blue(mColor);
		if(s==argb[0])r=p;
		else if(s==argb[1])g=p;
		else if(s==argb[2])b=p;
		else if(s==argb[3])a=p;
		setColor(Color.argb(a,r,g,b));
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,util.px(250)), WidgetUtils.measure(heightMeasureSpec, util.px(100)));
	}
	public interface OnPickListener{
		public void onPick(boolean cancel);
	}
}
