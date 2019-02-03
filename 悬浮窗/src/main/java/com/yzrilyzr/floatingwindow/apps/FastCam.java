package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.viewholder.HolderList;
import com.yzrilyzr.myclass.util;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.yzrilyzr.ui.myFab;
import com.yzrilyzr.icondesigner.VECfile;

public class FastCam implements Camera.PictureCallback,Camera.AutoFocusCallback,SurfaceHolder.Callback,Window.OnButtonDown,OnClickListener,Window.OnSizeChanged
{
	SurfaceView sv;
	private Window w;
	Camera cam;
	int camid=0;
	View take,settingsButton,setflash;
	ViewGroup flash,settingsLay;
	ExpandableListView settings;
	String[] settingItem=new String[]{"特效","场景","白平衡","防闪烁","对焦模式","照片大小"};
	String[] settingItemIcon=new String[]{"effect","image","whitebalance","antiband","unfocusedwin","size"};
	public FastCam(final Context c,Intent e)
	{
		w=new Window(c,util.px(225),util.px(400))
		.setTitle("速拍")
		.setIcon("fastcam")
		.setOnButtonDown(this)
		.setOnSizeChanged(this)
		.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.window_fastcam);
		settingsButton=vg.findViewById(R.id.windowfastcammyFab2);
		flash=(ViewGroup) vg.findViewById(R.id.windowfastcamLinearLayout2);
		setflash=vg.findViewById(R.id.windowfastcammyFab3);
		take=vg.findViewById(R.id.windowfastcammyFab1);
		take.setOnClickListener(this);
		settings=(ExpandableListView) vg.findViewById(R.id.windowfastcamExpandableListView1);
		settingsButton.setOnClickListener(this);
		settingsLay=(ViewGroup)settings.getParent();
		settingsLay.setOnClickListener(this);
		setflash.setOnClickListener(this);
		sv=(SurfaceView) vg.findViewById(R.id.windowfastcamSurfaceView1);
		SurfaceHolder h=sv.getHolder();
		h.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		init();
		h.addCallback(this);
		settings.setAdapter(new BaseExpandableListAdapter(){

			@Override
			public int getGroupCount()
			{
				// TODO: Implement this method
				return settingItem.length;
			}

			@Override
			public int getChildrenCount(int p1)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public Object getGroup(int p1)
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public Object getChild(int p1, int p2)
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public long getGroupId(int p1)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public long getChildId(int p1, int p2)
			{
				// TODO: Implement this method
				return 0;
			}

			@Override
			public boolean hasStableIds()
			{
				// TODO: Implement this method
				return false;
			}

			@Override
			public View getGroupView(int position,boolean p2, View convertView, ViewGroup parent)
			{
				HolderList holder;
				if(convertView==null)
				{
					holder=new HolderList(c);
					convertView=holder.vg;
					convertView.setTag(holder);
					holder.v[1].setVisibility(8);
					holder.v[2].setVisibility(8);
					holder.v[3].setVisibility(8);

				}
				else holder=(HolderList) convertView.getTag();
				holder.v[0].setImageVec(settingItemIcon[position]);
				holder.text.setText(settingItem[position]);
				return convertView;
			}

			@Override
			public View getChildView(int p1, int p2, boolean p3, View p4, ViewGroup p5)
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public boolean isChildSelectable(int p1, int p2)
			{
				// TODO: Implement this method
				return false;
			}
		});
		for(int i=0;i<flash.getChildCount();i++)
		{
			final int l=i;
			flash.getChildAt(i).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					Camera.Parameters p=cam.getParameters();
					String s=new String[]{Camera.Parameters.FLASH_MODE_ON,
					Camera.Parameters.FLASH_MODE_AUTO,
					Camera.Parameters.FLASH_MODE_OFF}[l];
					p.setFlashMode(s);
					cam.setParameters(p);
					util.getSPWrite().putString("camflash",s).commit();
					try
					{
						((myFab)setflash).setImageBitmap(VECfile.createBitmap(c,new String[]{"flashlighton","flashlightauto","flashlightoff"}[l],util.px(50),util.px(50)));
					}
					catch (Exception e)
					{
					}
					FastCam.this.onClick(setflash);
				}
			});
		}
	}

	private void init()
	{
		try
		{
			cam=Camera.open(camid);
			cam.enableShutterSound(false);
		}
		catch(Throwable e)
		{
			util.toast("无法打开相机\n请检查权限或者相机是否被占用");
		}
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==take)
		{
			try
			{
				cam.takePicture(null,null,this);
			}
			catch(Throwable e)
			{}
		}
		else if(p1==settingsButton)
		{
			if(settingsLay.getVisibility()==8)
			{
				(settingsLay).setVisibility(0);
			}
			else
			{
				settingsLay.setVisibility(8);
			}
		}
		else if(p1==settingsLay)
		{
			settingsLay.setVisibility(8);
		}
		else if(p1==setflash)
		{
			if(flash.getChildAt(0).getVisibility()==8)
			{
				for(int i=0;i<flash.getChildCount();i++)
				{
					flash.getChildAt(i).setVisibility(0);
					Animation l=AnimationUtils.loadAnimation(util.ctx,R.anim.scale_alpha_in);
					l.setStartOffset(l.getDuration()*(flash.getChildCount()-1-i));
					flash.getChildAt(i).startAnimation(l);
				}
			}
			else
			{
				for(int i=0;i<flash.getChildCount();i++)
				{
					Animation l=AnimationUtils.loadAnimation(util.ctx,R.anim.scale_alpha_out);
					l.setStartOffset(l.getDuration()*i);
					final int k=i;
					l.setAnimationListener(new Animation.AnimationListener(){

						@Override
						public void onAnimationStart(Animation p1)
						{
							// TODO: Implement this method
						}

						@Override
						public void onAnimationEnd(Animation p1)
						{
							flash.getChildAt(k).setVisibility(8);
						}

						@Override
						public void onAnimationRepeat(Animation p1)
						{
							// TODO: Implement this method
						}
					});
					flash.getChildAt(i).startAnimation(l);
				}
			}

		}
	}
	@Override
	public void onAutoFocus(boolean p1, Camera p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onPictureTaken(final byte[] p1, Camera p2)
	{
		new Thread(){
			@Override public void run()
			{
				try
				{
					String path=util.sdcard+"/"+Environment.DIRECTORY_DCIM+"/FastCam";
					File f=new File(path);
					if(!f.exists())f.mkdirs();
					path+="/FASTCAM"+new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()))+".jpg";
					BufferedOutputStream o=new BufferedOutputStream(new FileOutputStream(path));
					o.write(p1);
					o.flush();
					o.close();
					util.scanMedia(path);
				}
				catch (IOException e)
				{
					util.toast("保存失败");
					e.printStackTrace();
				}

			}
		}.start();
		cam.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		if(cam==null)
		{w.dismiss();return;}
		Camera.Parameters p=cam.getParameters();
		List<int[]> fps=p.getSupportedPreviewFpsRange();
		int[] ff=fps.get(fps.size()-1);
		p.setPreviewFpsRange(ff[1],ff[1]);
		p.setPictureFormat(PixelFormat.JPEG);
		p.setJpegQuality(100);
		cam.setParameters(p);
		cam.setDisplayOrientation(90);
		setViewSize(sv.getWidth(),sv.getHeight());
		for(String k:p.getSupportedWhiteBalance())System.out.println(k);
		try
		{
			cam.setPreviewDisplay(sv.getHolder());
			cam.startPreview();
			cam.autoFocus(this);
		}
		catch (Exception e)
		{
			util.toast("无法打开相机");
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
		if(cam!=null)cam.stopPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	{

	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		setViewSize(w, h);
	}

	private void setViewSize(int w, int h)
	{
		try
		{
			Camera.Parameters p=cam.getParameters();
			List<Camera.Size> l=p.getSupportedPreviewSizes();
			float c=0,d=0;
			h-=util.px(30);
			int xw=0,xh=0;
			for(Camera.Size s:l)
			{
				float a=s.height/sv.getWidth();
				float b=s.width/sv.getHeight();
				if(a>=1&&b>=1)
				{
					c=Math.min(c,a);
					d=Math.min(d,a);
					xw=s.height;
					xh=s.width;
				}
			}
			if(xh!=0&&xw!=0)
			{
				System.out.println(xh+"="+xw);
				p.setPreviewSize(xh,xw);
				//p.setPictureSize(xh,xw);
				cam.setParameters(p);
			}
			FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams) sv.getLayoutParams();
			lp.width=(int) util.limit(w<h?w:h*xw/xh,0,w);
			lp.height=(int) util.limit(w<h?w*xh/xw:h,0,h);
			sv.setLayoutParams(lp);
		}
		catch(Throwable e)
		{e.printStackTrace();}
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			//cam.stopPreview();
			//cam.release();
		}
	}
}
