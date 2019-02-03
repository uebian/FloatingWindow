package com.yzrilyzr.ui;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.yzrilyzr.myclass.*;

public class myColorView extends ImageView
{
	public myColorView(Context c,AttributeSet a)
	{
		super(c,a);
		draw=new drawable();
		setBackgroundDrawable(draw);
	}
	public myColorView(Context c)
	{
		this(c,null);
	}
	public void setColorView(int i)
	{draw.refresh(i);}
	private drawable draw;
	private class drawable extends Drawable
	{
		private int color=0x00000000,w,h;
		private Bitmap bitmap;
		public void refresh(int i)
		{color=i;invalidateSelf();}
		@Override
		public void draw(Canvas canvas)
		{
			// TODO: Implement this method
			Paint p=new Paint();
			canvas.drawColor(0xff888888);
			canvas.drawBitmap(bitmap,1,1,p);
			p.setColor(color);
			canvas.drawRect(1,1,w-1,h-1,p);

		}

		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			// TODO: Implement this method
			super.setBounds(left, top, right, bottom);
			w=right;h=bottom;int s=util.px(5);
			bitmap=Bitmap.createBitmap(w-2,h-2,Bitmap.Config.ARGB_8888);
			Canvas c=new Canvas(bitmap);
			boolean isWhite=false;
			Paint pp=new Paint();
			for(int ww=0;ww<w;ww+=s)
			{
				for(int hh=0;hh<h;hh+=s)
				{
					pp.setColor(isWhite?0xffffffff:0xffaaaaaa);
					c.drawRect(ww,hh,ww+s,hh+s,pp);
					isWhite=!isWhite;
				}
				isWhite=!isWhite;
			}

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


}
