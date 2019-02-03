package com.yzrilyzr.background;
import android.graphics.Canvas;
import com.yzrilyzr.ui.uidata;
import android.view.SurfaceHolder;
import java.util.ArrayList;
import android.graphics.PointF;
import java.util.Random;
import com.yzrilyzr.myclass.util;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Shapes extends BGdraw
{
	public ArrayList<PF> pos=new ArrayList<PF>();
	int ww,wh;
	float rad;
	float vel;
	PF cpf=null;
	public Shapes(int count)
	{
		super();
		p.setStrokeWidth(util.px(3));
		ww=util.getScreenWidth();
		wh=util.getScreenHeight();
		rad=util.px(15);
		p.setTextSize(200);
		vel=rad/16;
		Random r=new Random();
		for(int i=0;i<count;i++)pos.add(new PF(r.nextInt(ww),r.nextInt(wh)));
	}
public void add(){
	Random r=new Random();
	pos.add(new PF(r.nextInt(ww),r.nextInt(wh)));
}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		c.drawColor(uidata.BACK);
		for(PF pp:pos)
		{
			p.setColor(uidata.ACCENT);
			p.setStyle(Paint.Style.FILL);
			c.drawCircle(pp.x,pp.y,rad,p);
			p.setColor(0xff000000);
			p.setStyle(Paint.Style.STROKE);
			c.drawCircle(pp.x,pp.y,rad,p);
		}
		//c.drawText("FPS:"+(1000/dt),0,500,p);
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		pos.clear();
	}
	@Override
	public void onTouchEvent(MotionEvent e)
	{
		if(e.getAction()==MotionEvent.ACTION_DOWN)
		{
			cpf=null;
			for(PF pp:pos)
			{
				float r=(float)(Math.sqrt(Math.pow(e.getX()-pp.x,2)+Math.pow(e.getY()-pp.y,2)));
				if(r<=rad)
				{
					cpf=pp;
					break;
				}
			}
		}
		else if(e.getAction()==MotionEvent.ACTION_UP)cpf=null;
		if(cpf!=null)cpf.set(e.getX(),e.getY());
	}
	@Override
	public void onCompute()
	{
		// TODO: Implement this method
		super.onCompute();
		Random r=new Random();
		for(PF pp:pos)
		{
			if(pp==cpf)continue;
			pp.vx+=pp.ax;
			pp.vy+=pp.ay;
			pp.x+=pp.vx;
			pp.y+=pp.vy;
			if(pp.x<-rad)pp.x=ww+rad;
			if(pp.y<-rad)pp.y=wh+rad;
			if(pp.x>ww+rad)pp.x=-rad;
			if(pp.y>wh+rad)pp.y=-rad;
			if(pp.vx>vel)pp.vx=vel;
			if(pp.vx<-vel)pp.vx=-vel;
			if(pp.vy>vel)pp.vy=vel;
			if(pp.vy<-vel)pp.vy=-vel;
			pp.ax=r.nextBoolean()?0.2f:-0.2f;
			pp.ay=r.nextBoolean()?0.2f:-0.2f;
		}
	}
	public static class PF extends PointF
	{
		public float ax=0,ay=0,vx,vy;
		public PF(float x,float y)
		{
			this.x=x;
			this.y=y;
		}
	}
}
