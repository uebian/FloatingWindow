package com.yzrilyzr.ui;
import android.graphics.*;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import com.yzrilyzr.myclass.util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class myCanvas extends SurfaceView implements SurfaceHolder.Callback,View.OnTouchListener
{
	private boolean Rdraw=false;
	private SurfaceHolder holder = null;
	private Canvas canvas = null;
	private Canvas canvasTemp = null;
	private Bitmap bi = null;
	private Paint p = new Paint();
	private Path path = new Path();
	private ArrayList<PointF> points=new ArrayList<PointF>();
	public myCanvas(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);
		p.setColor(Color.RED);
		p.setTextSize(40);
		p.setStrokeWidth(5);
		p.setAntiAlias(true);
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeJoin(Paint.Join.ROUND);
		setOnTouchListener(this);
	}
	float rdx=0,rdy=0,rvx=0,rvy=0,rax=0,ray=0;
	public void startRDraw()
	{
		Rdraw=true;
		rdx=(float)getWidth()/2;rdy=(float)getHeight()/2;
		path.moveTo(rdx,rdy);
		new Thread(new Runnable(){@Override public void run()
				{
					while(Rdraw)
					{
						try
						{
							rax=(float)util.random(-100,100)/50f;
							ray=(float)util.random(-100,100)/50f;
							rvx+=rax;rvy+=ray;
							rvx=util.limit(rvx,-50,50);
							rvy=util.limit(rvy,-50,50);
							rdx+=rvx;rdy+=rvy;
							if((rdx<0||rdx>getWidth()||rdy<0||rdy>getHeight()))
							{
								rdx=getWidth()/2;
								rdy=getHeight()/2;
								rax=0;ray=0;
								rvx=0;rvy=0;
								path.moveTo(rdx,rdy);
							}
							path.lineTo(rdx,rdy);
							draw();
							Thread.sleep(10);
						}
						catch(Exception e)
						{
						}
					}
				}}).start();
	}
	public void stopRDraw()
	{
		Rdraw=false;
	}
	private final void draw()
	{
		try
		{
			canvas = holder.lockCanvas();
			if (holder != null)
			{
				canvasTemp.drawColor(Color.WHITE);
				canvasTemp.drawPath(path, p);
				canvas.drawBitmap(bi, 0, 0, null);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (holder != null)
			{
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
	private String getTime()
	{
		return new SimpleDateFormat("HHmmssSSS").format(new Date(System.currentTimeMillis()));
	}
	public void saveCanvas()
	{
		FileOutputStream fos = null;
		try
		{
			String fileName = getTime();
			String filePath = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
				+"/PIC"+ fileName + ".png";
			fos = new FileOutputStream(new File(filePath));
			bi.compress(Bitmap.CompressFormat.PNG, 100, fos);
			util.scanMedia(filePath);
			Toast.makeText(getContext(), "保存成功,文件名:" + fileName + ".png",
						   Toast.LENGTH_LONG).show();
			//	clear();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void setColor(int color)
	{
		p.setColor(color);
	}
	public void clear()
	{
		path.reset();
		draw();
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{

	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
	{
		if(bi!=null)bi.recycle();
		bi = Bitmap.createBitmap(getWidth(), getHeight(),Bitmap.Config.ARGB_8888);
		canvasTemp = new Canvas(bi);
		draw();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		bi.recycle();
		bi=null;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		getParent().requestDisallowInterceptTouchEvent(true);
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				path.moveTo(event.getX(), event.getY());
				//points.add(new PointF(event.getX(), event.getY()));
				draw();
				break;
			case MotionEvent.ACTION_MOVE: // 画出每次移动的轨迹
				path.lineTo(event.getX(), event.getY());
				//points.add(new PointF(event.getX(), event.getY()));
				draw();
				break;
			case MotionEvent.ACTION_UP:
				path.lineTo(event.getX(), event.getY());
				//com.yzrilyzr.icondesigner.Shape.Catmull_Rom(points,100,path);
				draw();
				break;
		}
		return true;
	}
}
