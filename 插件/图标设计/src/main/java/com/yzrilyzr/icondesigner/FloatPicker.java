package com.yzrilyzr.icondesigner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;

public class FloatPicker extends MView
{
	private double degree=-Math.PI/2;
	private double ot;
	private Paint paint;
	private double value=1,tvalue=1;
	private int mode=0;
	FloatPickerEvent ifc=null;
	private float width;
	public FloatPicker(float x,float y,float width,FloatPickerEvent ev)
	{
		super(x,y,width,width);
		this.ifc=ev;
		this.width=px(width);
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface(Typeface.SANS_SERIF);
	}
	@Override
	public void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		float r=width/6;
		float cx=width/2+left,cy=width/2+top;
		paint.setTextSize(r);
		paint.setColor(menucolor);
		canvas.drawCircle(cx,cy,r,paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(r);
		paint.setColor(buttoncolor);
		canvas.drawCircle(cx,cy,r*1.5f,paint);
		paint.setStyle(Paint.Style.FILL);

		float px=(float)(2.0*r*Math.cos(degree));
		float py=(float)(2.0*r*Math.sin(degree));
		float fl=(float)(2.0*r/Math.sqrt(3.0));
		float pi=(float)(Math.PI/6.0);
		Path pa=new Path();
		pa.moveTo(px+cx,py+cy);
		pa.lineTo((float)(px+fl*Math.cos(pi+degree))+cx,(float)(py+fl*Math.sin(pi+degree))+cy);
		pa.lineTo((float)(px+fl*Math.cos(degree-pi))+cx,(float)(py+fl*Math.sin(degree-pi))+cy);
		pa.close();
		canvas.drawPath(pa,paint);
		Paint.FontMetrics met=paint.getFontMetrics();
		/*float c1x=(float)(2.5*r*Math.cos(degree+2.0/9.0*Math.PI));
		float c1y=(float)(2.5*r*Math.sin(degree+2.0/9.0*Math.PI));
		canvas.drawCircle(cx+c1x,cy+c1y,r/2,paint);
		float c2x=(float)(2.5*r*Math.cos(degree-2.0/9.0*Math.PI));
		float c2y=(float)(2.5*r*Math.sin(degree-2.0/9.0*Math.PI));
		canvas.drawCircle(cx+c2x,cy+c2y,r/2,paint);
		*/
		paint.setColor(textcolor);
		//canvas.drawText("+",cx+c1x,cy+c1y+met.bottom*1.2f,paint);
		//canvas.drawText("-",cx+c2x,cy+c2y+met.bottom*1.2f,paint);
		for(int i=1;i<10;i++)
		{
			float tx=(float)(1.5*r*Math.cos((i*4-13.0)*Math.PI/18.0));
			float ty=(float)(1.5*r*Math.sin((i*4-13.0)*Math.PI/18.0));
			canvas.drawText(Integer.toString(i),cx+tx,cy+ty+met.bottom*1.2f,paint);
		}
		float v=getValue();
		if(v==0)canvas.drawText("/",cx,cy+met.bottom*1.2f,paint);
		else canvas.drawText(Float.toString(v),cx,cy+met.bottom*1.2f,paint);
	}
	@Override
	public void onTouchEvent(MotionEvent event)
	{
		// TODO: Implement this method
		float c=width/2;
		float r=width/6;
		float x=event.getX()-left,y=event.getY()-top;
		ot=Math.sqrt(Math.pow(x-c,2)+Math.pow(y-c,2));
		double ld=degree;
		if(ot<r&&mode==0)
		{
			mode=1;
			final EditText edit=new EditText(render.ctx);
			edit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			edit.setText(Float.toString(getValue()));
			new AlertDialog.Builder(render.ctx)
				.setTitle("设置数值(0~+∞)")
				.setView(edit)
				.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						try
						{
							setValue(Float.parseFloat(edit.getText().toString()));
							if(ifc!=null)ifc.onChange(FloatPicker.this,getValue());
						}
						catch(Throwable e)
						{}
					}
				})
				.setNegativeButton("取消",null)
				.show();
		}
		if(r<ot&&ot<2*r&&mode==0)mode=3;
		else if(2*r<ot&&ot<3*r&&mode==0)
		{
			mode=2;
		}
		if(mode!=1)
		{
			degree=Math.asin((y-c)/ot);
			if(x-c<0)degree=(Math.PI-degree);
		}
		if(ld>4&&degree<-1)tvalue*=10;
		if(ld<-1&&degree>4)tvalue/=10;
		double aa=40.0/180.0*Math.PI;
		if(mode==3)degree=Math.round((degree+Math.PI/2.0)/aa)*aa-Math.PI/2.0;
		value=(degree+Math.PI/2.0)*9*tvalue/(Math.PI*2.0)+tvalue;
		if(event.getAction()==MotionEvent.ACTION_UP)mode=0;
		if(ifc!=null)ifc.onChange(this,getValue());
	}
	public float getValue()
	{
		return Math.round(value*100)/100f;
	}
	public void setValue(float v)
	{
		double v2=v;
		int i=0;
		while(v>0.1)
		{v/=10;i++;}
		tvalue=Math.pow(10,i-2);
		degree=(v2-tvalue)/9/tvalue*(Math.PI*2.0)-Math.PI/2.0;
		value=v2;
	}
	public interface FloatPickerEvent
	{
		public abstract void onChange(FloatPicker p,float f);
	}
}
	
