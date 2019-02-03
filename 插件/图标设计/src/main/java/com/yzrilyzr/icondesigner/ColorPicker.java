package com.yzrilyzr.icondesigner;
import android.graphics.*;

import android.view.MotionEvent;

public class ColorPicker extends MView implements SeekBar.SeekBarEvent
{
	private Paint p;
	private int mColor=0xff000000,cColor=0xff000000;
	public SeekBar[] argb=new SeekBar[4];
	private RectF satu,value;
	private int touchw=-1;
	private float[] hsv=new float[]{0,0,0};
	private ColorView cv;
	private VECfile.Builder builder=new VECfile.Builder();
	private Point poi;
	RectF ar;
	public ColorPicker(float x,float y,float w,float h)
	{
		super(x,y,w,h);
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
		satu=new RectF(left+width()/20,top,left+width()/4,bottom);
		value=new RectF(right-width()/4,top,right-width()/20,bottom);
		float rr=height()/4;
		ar=new RectF(centerX()-rr,centerY()-rr,centerX()+rr,centerY()+rr);
	}
	public void setIColor(ColorView v)
	{
		cv=v;
		builder=null;
		poi=null;
		setColor(cColor=cv.color);
	}
	public void setIColor(VECfile.Builder b){
		this.builder=b;
		cv=null;
		poi=null;
		setColor(cColor=b.backgcolor);
	}
	public void setIColor(Point shader){
		builder=null;
		cv=null;
		poi=shader;
		setColor(cColor=poi.x);
	}
	private void setColor(int color)
	{
		mColor=color;
		Color.colorToHSV(mColor,hsv);
		argb[0].setProgress(Color.red(mColor));
		argb[1].setProgress(Color.green(mColor));
		argb[2].setProgress(Color.blue(mColor));
		argb[3].setProgress(Color.alpha(mColor));
	}
	public int getColor()
	{
		return mColor;
	}
	@Override
	public void onDraw(Canvas c)
	{
		float w=width();
		p.setShader(new LinearGradient(left,top,left,bottom,Color.HSVToColor(new float[]{hsv[0],1,hsv[2]}),Color.HSVToColor(new float[]{hsv[0],0,hsv[2]}),Shader.TileMode.CLAMP));
		c.drawRect(satu,p);
		p.setShader(new SweepGradient(centerX(),centerY(),new int[]{
										  0xffff0000,0xffff8000,0xffffff00,0xff80ff00,
										  0xff00ff00,0xff00ff80,0xff00ffff,0xff0080ff,
										  0xff0000ff,0xff8000ff,0xffff00ff,0xffff0080,
										  0xffff0000
									  },null));
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(height()/6);
		c.drawCircle(centerX(),centerY(),height()*5f/12f,p);
		p.setShader(null);
		p.setStyle(Paint.Style.FILL);
		p.setColor(0xff000000);
		float rr=height()/4;
		p.setColor(cColor);
		c.drawArc(ar,90,180,true,p);
		p.setColor(mColor);
		c.drawArc(ar,-90,180,true,p);
		p.setShader(new LinearGradient(left,top,left,bottom,Color.HSVToColor(new float[]{hsv[0],hsv[1],1}),Color.HSVToColor(new float[]{hsv[0],hsv[2],0}),Shader.TileMode.CLAMP));
		c.drawRect(value,p);
		p.setShader(null);
		p.setColor(0x80000000);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(rr=5);
		c.drawRect(left+w/20,bottom-height()*hsv[1]-rr,left+w/4,bottom-height()*hsv[1]+rr,p);
		c.drawRect(right-w/20,bottom-height()*hsv[2]-rr,right-w/4,bottom-height()*hsv[2]+rr,p);
		p.setStyle(Paint.Style.FILL);
		rr=height()*5f/12f;
		c.drawCircle(centerX()+rr*(float)Math.cos(hsv[0]*Math.PI/180f),centerY()+rr*(float)Math.sin(hsv[0]*Math.PI/180f),height()/10,p);
		p.setColor(mColor);
		c.drawCircle(centerX()+rr*(float)Math.cos(hsv[0]*Math.PI/180f),centerY()+rr*(float)Math.sin(hsv[0]*Math.PI/180f),height()/12,p);
	}
	@Override
	public void onTouchEvent(MotionEvent e)
	{
		float x=e.getX(),y=e.getY();
		if(e.getAction()==MotionEvent.ACTION_UP)touchw=-1;
		else if(touchw==-1)
		{
			if(satu.contains(x,y))touchw=1;
			else if(value.contains(x,y))touchw=2;
			else{
				float r=(float)Math.sqrt(Math.pow(x-centerX(),2)+Math.pow(y-centerY(),2));
				if(height()/3f<r&&height()/2f>r)touchw=3;
				else if(r<height()/4f){
					if(x-centerX()>0)
						if(cv!=null)cv.color=mColor;
						else if(builder!=null)builder.setBackgcolor(mColor);
						else if(poi!=null)poi.x=mColor;
					((Menu)parent).show=false;
				}
			}
		}
		else if(touchw==1)
		{
			int al=Color.alpha(mColor);
			hsv[1]=(satu.bottom-y)/satu.height();
			setColor(Color.HSVToColor(al,hsv));
		}
		else if(touchw==2)
		{
			int al=Color.alpha(mColor);
			hsv[2]=(value.bottom-y)/value.height();
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
	}
	@Override
	public void onChange(SeekBar s,int p)
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
}
