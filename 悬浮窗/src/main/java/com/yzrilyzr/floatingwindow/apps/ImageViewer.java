package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.view.mImageView;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import java.io.File;
import java.io.IOException;

public class ImageViewer implements Window.OnButtonDown
{
	Bitmap b;
	public ImageViewer(Context c,Intent e) throws IOException
	{
		int type=e.getIntExtra("type",0);
		File f=null;
		String ll=e.getStringExtra("path");
		if(ll!=null)f=new File(ll);
		mImageView iv=new mImageView(c);
		if(type==1)
		{
			b=BitmapFactory.decodeFile(f.getAbsolutePath());
		}
		else if(type==2)
		{
			VECfile v=VECfile.readFile(f.getAbsolutePath());
			b=VECfile.createBitmap(v,v.width,v.height);
		}
		else if(type==3)
		{
			byte[] h=e.getByteArrayExtra("data");
			b=BitmapFactory.decodeByteArray(h,0,h.length);
		}
		if(b==null){
			util.toast("无法打开图像");
			return;
		}
		new Window(c,util.px(300),util.px(300))
			.setTitle(f==null?"图片预览":f.getName())
			.setBar(0,0,0)
			.setCanFocus(false)
			.setOnButtonDown(this)
			.addView(iv)
			.setIcon(type==1||type==3?"image":(type==2?"class":"floatingwindow"))
			.show();
		iv.setImage(b);
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)b.recycle();
	}
}
