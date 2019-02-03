package com.yzrilyzr.icondesigner;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap;

public class FileButton extends Button
{
	int type=0;
	//0:null,1:file,2:dir,3:vec,4:up,5:xml
	public FileButton(float x,float y,float w,float h,String s,boolean isFile,Event e)
	{
		super(x,y,w,h,s,e);
		type=isFile?1:2;
		if(s.toLowerCase().endsWith(".vec"))type=3;
		else if(s.toLowerCase().endsWith(".xml"))type=5;
		else if("...".equals(s))type=4;
	}
	@Override
	public void onDraw(Canvas c)
	{
		super.onDraw(c);
		Bitmap b=null;
		if(type==1)b=render.file;
		else if(type==2)b=render.folder;
		else if(type==3)b=render.vecfile;
		else if(type==4)b=render.upparent;
		else if(type==5)b=render.xml;
		if(b!=null)
		{
			Matrix m=new Matrix();
			m.postScale((float)height()/(float)b.getWidth(),(float)height()/(float)b.getHeight());
			m.postTranslate(left,top);
			c.drawBitmap(b,m,pa);
		}
	}
}
