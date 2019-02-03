package com.yzrilyzr.floatingwindow.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Matrix;

public class mImageView extends View
{
	private Bitmap img;
	private Paint pa=new Paint();
	public float deltax=0,deltay=0,scale=1,lscale=1,lpointLen;
	private boolean moved=false;
	public float ddx,ddy;
	public mImageView(Context c)
	{
		super(c);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(img!=null&&!img.isRecycled())
		{
			pa.setColor(0xff000000);
			Matrix m=new Matrix();
			m.postTranslate(deltax,deltay);
			m.postScale(scale,scale);
			canvas.drawBitmap(img,m,pa);
		}
	}
	public void setImage(Bitmap b)
	{
		img=b;
		invalidate();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int a=event.getAction();
		if(event.getPointerCount()==1)
		{
			if(a==MotionEvent.ACTION_DOWN)moved=false;
		}
		else if(event.getPointerCount()==2)
		{
			float x1=event.getX(1),y1=event.getY(1);
			float x=event.getX(0),y=event.getY(0);
			if(!moved)
			{
				ddx=(x+x1)/2;
				ddy=(y+y1)/2;
				lpointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
				lscale=scale;
				moved=true;
			}
			else
			{
				float pointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
				float llsc=scale;
				scale=lscale*pointLen/lpointLen;
				float cx=(x+x1)/2f,cy=(y+y1)/2f;
				deltax=(deltax-cx/llsc)+cx/scale;
				deltay=(deltay-cy/llsc)+cy/scale;
				deltax-=(ddx-(x+x1)/2)/scale;
				deltay-=(ddy-(y+y1)/2)/scale;
				ddx=(x+x1)/2;
				ddy=(y+y1)/2;
			}
		}
		invalidate();
		return true;
	}
}
