package com.yzrilyzr.icondesigner;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;

public class VecView extends ImageView
{
	private VECfile vec;
	public VecView(Context c,AttributeSet a)
	{
		super(c,a);
		try
		{
			String d=a.getAttributeValue(null,"vec")+".vec";
			if(d!=null)
			{
				InputStream i=c.getAssets().open(d);
				vec=VECfile.readFileFromIs(i);
				vec.recycle();
			}
		}
		catch(Throwable e)
		{}
		setLayerType(LAYER_TYPE_SOFTWARE,null);
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		if(vec!=null)
		{
			canvas.drawColor(vec.backgcolor);
			for(Shape s:vec.shapes)
				s.onDraw(canvas,vec.MD,vec.antialias,vec.dither,0,0,(float)getWidth()/(float)vec.width,vec.dp,vec.sp);
		}
		else super.onDraw(canvas);
	}
	public VecView(Context c)
	{
		this(c,null);
	}
	public void setImageVecFile(VECfile f){
		vec=f;
		invalidate();
	}
	public void setImageVec(String assetPath)
	{
		if(assetPath==null){
			vec=null;
		}
		else
			try
			{
				InputStream is=getContext().getAssets().open(assetPath+".vec");
				vec=VECfile.readFileFromIs(is);
				vec.recycle();
			}
			catch (IOException e)
			{
			}
		invalidate();
	}
}
