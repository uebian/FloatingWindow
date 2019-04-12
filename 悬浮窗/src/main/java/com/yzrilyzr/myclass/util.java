package com.yzrilyzr.myclass;
import android.content.*;
import java.io.*;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.Copyright;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myToast;
import com.yzrilyzr.ui.uidata;
import java.lang.reflect.Method;
import java.util.List;
import android.os.Build;
import com.yzrilyzr.floatingwindow.PluginService;

public final class util
{
    public static final String ERROR="ERROR;";
	public static final int VersionCode=2;
	public static final String sdcard=Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String info="yzr's tool v2.0.Don't delete this info";
	public static final String mainDir=sdcard+"/yzr的app/";
	public static Context ctx;
	private static SharedPreferences sp;
	public static boolean sup=Build.VERSION.SDK_INT<=Build.VERSION_CODES.M;
	static{
		File f=new File(mainDir);
		if(!f.exists())f.mkdirs();
		Copyright.a();
		new Thread(){
			@Override public void run()
			{
				try
				{
					Thread.sleep(5000);
					if(!PluginService.started)System.exit(0);
				}
				catch (InterruptedException e)
				{
					System.exit(0);
				}
			}
		}.start();
	}
	public static void toast(final Object s)
    {
		new Handler(ctx.getMainLooper()).post(new Runnable(){
			@Override
			public void run()
			{
				myToast.makeText(ctx,s+"",0).show();
			}
		});
    }
    public static void alert(final String s)
    {
		new Handler(ctx.getMainLooper()).post(new Runnable(){

			@Override
			public void run()
			{
				// TODO: Implement this method
				new myDialog.Builder(ctx)
				.setTitle("提示")
				.setMessage(s)
				.setPositiveButton("确定",null)
				.setNegativeButton("复制",new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface p,int p3)
					{
						copy(s);
					}
				})
				.show();
			}

		});
	}
	public static void hideIME(View v)
	{
		((InputMethodManager)ctx. getSystemService(Context.INPUT_METHOD_SERVICE)). 
		hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}
	public static void openIME(View v)
	{
		v.setFocusable(true);
		v.setFocusableInTouchMode(true);
		v.requestFocus();
		InputMethodManager ime=((InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE));
		ime.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
	}
	public static String getFileSizeStr(double size){
		String[] s="B KB MB GB TB".split(" ");
		int i=0;
		while(size>=1024){
			size/=1024;
			i++;
		}
		return Math.floor(size*100.0)/100.0+s[i];
	}
    public static void copy(String s)
    {
        ((ClipboardManager)ctx.getSystemService(ctx.CLIPBOARD_SERVICE)).setText(s);
    }
    public static String paste()
    {
        return ((ClipboardManager)ctx.getSystemService(ctx.CLIPBOARD_SERVICE)).getText().toString();
    }
    public static void writeBmp(Bitmap b,String path)
    {
        try
        {
            b.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(path));
        }
        catch (FileNotFoundException e)
        {}
    }
    public static boolean write(String f,String s)
    {
        try
        {
            BufferedWriter bw=new BufferedWriter(new FileWriter(f));
            bw.write(s);
            bw.close();
        }
        catch(IOException e)
        {
            return false;
        }
        return true;
    }
    public static void call(Class currentClass,String mname,Class<?>[] paramClasses,Object instance,Object[] callParams)
    {
        try
        {
            Method m=currentClass.getClass().getMethod(mname,paramClasses);
            m.invoke(instance,callParams);
        }
        catch(Throwable e)
        {}
    }

    public static String read(String f,boolean withN)
    {
        StringBuffer sb=new StringBuffer();
        try
        {
            String n=withN?"\n":"";
            BufferedReader br=new BufferedReader(new FileReader(f));
            String b="";
            while((b=br.readLine())!=null)sb.append(b).append(n);
        }
        catch(IOException e)
        {
            return ERROR;
        }
        return sb.toString();
    }
	public static double getArc(double x,double y,double r)
	{
		double a=Math.asin(y/r);
		if(x<0)a=Math.PI-a;
		if(x>0&&y<0)a+=2*Math.PI;
		return a;
	}
    public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try
        {
            t.printStackTrace(pw);
            return sw.toString();
        }
        finally
        {
            pw.close();
        }
    }
    public static String getAppVersion()
    {
        PackageManager pm = ctx.getPackageManager();
        try
        {
            PackageInfo info = pm.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return ERROR;
        }
    }
    public static String toNor(String keyword)
    {
        if (!keyword.equals(""))
        {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr)
            {
                if (keyword.contains(key))
                {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
	public static String readwithN(InputStream is)throws IOException
    {
        StringBuffer sb=new StringBuffer();
        String st="";
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        while((st=br.readLine())!=null)
        {
			sb.append(st).append("\n");
		}
        br.close();
        return sb.toString();
    }
    public static String readwithN(String path)throws IOException
    {
        StringBuffer sb=new StringBuffer();
        String st="";
        BufferedReader br=new BufferedReader(new FileReader(path));
        while((st=br.readLine())!=null)
        {
			sb.append(st).append("\n");
		}
        br.close();
        return sb.toString();
    }
    public static void hideSoftKeyboard(EditText editText)
    {
        if (editText != null)
        {
            InputMethodManager imm = (InputMethodManager) ctx
			.getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static void showSoftKeyboard(EditText editText)
    {
        if (editText != null)
        {
            InputMethodManager imm = (InputMethodManager)ctx
			.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        }
    }
    public static String stringToUnicode(String string)
    {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++)
        {
            String u=Integer.toHexString(string.charAt(i));
            String[] p=new String[]{"0000","000","00","0",""};
            // 转换为unicode
            unicode.append("\\u"+p[u.length()]+u);
        }
        return unicode.toString();
    }
	public static String unicodeToString(String unicode)
    {
        StringBuffer sb=new StringBuffer();
        String[] b=unicode.split("");
        for(int i=0;i<b.length;i++)
        {
            if(b[i].equals("\\")&&b[i+1].equals("u"))
            {
                int data = Integer.parseInt(b[i+2]+b[i+3]+b[i+4]+b[i+5],16);
                sb.append((char) data);
                i+=5;
            }
            else
            {sb.append(b[i]);}
        }
        return sb.toString();
    }
    public static SharedPreferences getSPRead()
    {
		if(sp==null)sp=ctx. getSharedPreferences("data",Activity.MODE_PRIVATE);
        return sp;
    } 
    public static SharedPreferences.Editor getSPWrite()
    {
		if(sp==null)sp=ctx. getSharedPreferences("data",Activity.MODE_PRIVATE);
        return sp.edit();
    } 
	public static SharedPreferences getSPRead(String file)
    {
		return ctx. getSharedPreferences(file,Activity.MODE_PRIVATE);
    } 
    public static SharedPreferences.Editor getSPWrite(String file)
    {
		return ctx. getSharedPreferences(file,Activity.MODE_PRIVATE).edit();
    } 
	
    public static Typeface getTypefaceFA(String path)
    {
        AssetManager mgr=ctx.getAssets();
        Typeface tf=Typeface.createFromAsset(mgr,path);
        return tf;
    }

    public static void runAppByPkgName(String packagename)
    {
        PackageInfo packageinfo = null;
        try
        {
            packageinfo = ctx.getPackageManager().getPackageInfo(packagename, 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            toast("找不到包");
        }
        if (packageinfo == null)
        {return;}
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        List<ResolveInfo> resolveinfoList = ctx.getPackageManager()
		.queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null)
        {
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            ctx.startActivity(intent);
        }
    }
    public static int getStatusBarHeight()
	{

		int statusHeight = -1;
		try
		{
			Class clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = ctx.getResources().getDimensionPixelSize(height);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}

    public static String[] concat(String[] a,String[] b)
    {
        String[] tmp=new String[a.length+b.length];
        for(int i=0;i<a.length;i++)
        {
            tmp[i]=a[i];
        }
        for(int i=0;i<b.length;i++)
        {
            tmp[i+a.length]=b[i];
        }
        return tmp;
    }

    public static int dip(int pxValue)
    {
        return (int) (pxValue / uidata.UI_DENSITY);
    }

	public static void setWeight(View v)
	{
		((LinearLayout.LayoutParams)v.getLayoutParams()).weight=1;
	}
    public static int px(float dipValue)
    {
        return  (int)(dipValue *uidata.UI_DENSITY);//*(getScreenHeight()>getScreenWidth()?1:0.5f));
	}
    public static Rect getScreenRect()
    {
        DisplayMetrics displayMetric = new DisplayMetrics();
        displayMetric = Resources.getSystem().getDisplayMetrics();
        Rect rect = new Rect(0, 0, displayMetric.widthPixels, displayMetric.heightPixels);
        return rect;
    }
	public static void scanMedia(String... paths)
	{
		MediaScannerConnection.scanFile(ctx,paths, null,new MediaScannerConnection.OnScanCompletedListener() {
			@Override
			public void onScanCompleted(String path, Uri uri)
			{
			}
		});
	}
    public static int getScreenWidth()
    {
        return getScreenRect().width();
    }

    public static int getScreenHeight()
    {
        return getScreenRect().height();
    }

    public static int random(int min,int max)
    {
        return (int)Math.floor(Math.random()*(max-min))+min;
    }
	public static float limit(float x,float min,float max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static int limit(int x,int min,int max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static final void openFile(File file)
	{  
		Intent intent = new Intent();  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		//设置intent的Action属性  
		intent.setAction(Intent.ACTION_VIEW);  
		//获取文件file的MIME类型  
		String type = getMIMEType(file);  
		//设置intent的data和Type属性。  
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type);  
		//跳转  
		ctx.startActivity(intent);    
	}
	public static final String getMIMEType(File file)
	{  
		String type="*/*";  
		String fName = file.getName();  
		int dotIndex = fName.lastIndexOf(".");  
		if(dotIndex!=-1)
		{  
			String end=fName.substring(dotIndex,fName.length()).toLowerCase();  
			for(int i=0;i<MIME_MapTable.length;i++)
			{
				if(end.equals(MIME_MapTable[i][0]))
				{
					type = MIME_MapTable[i][1];
					break;
				}
			}
		}
		else return type;
		if(type.equals("*/*"))
		{
			try
			{
				if(file.isDirectory()||file.getName().contains("cnt"))return "";
				FileInputStream is=new FileInputStream(file);
				byte[] bf=new byte[64];
				is.read(bf);
				is.close();
				String hewd=new String(bf).toLowerCase();
				for(int i=0;i<HEAD_MapTable.length;i++)
				{
					if(hewd.contains(HEAD_MapTable[i][0]))
					{
						type = HEAD_MapTable[i][1];
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return type;  
	} 
	private static final String[][] HEAD_MapTable={
	{"7z",  	"application/x-7z"},
	{"pk",    "application/x-zip-compressed"},  
	{"gif",	  "image/gif"},
	{"exif",   	"image/jpeg"},
	{"id3",		"audio/x-mpeg"},
	{"jfif",   "image/jpeg"},
	{"mp4",    "video/mp4"},
	{"flv",    "video/x-flv"},
	{"png",    "image/png"},
	{"vec",	"application/vec"},
	{"riff",    "audio/x-wav"},  
	};
	private static final String[][] MIME_MapTable={  
	//{后缀名， MIME类型}  
	{".3gp",    "video/3gpp"},  
	{".7z",  	"application/x-7z"},  
	{".apk",    "application/vnd.android.package-archive"},  
	{".asf",    "video/x-ms-asf"},  
	{".avi",    "video/x-msvideo"},  
	{".bin",    "application/octet-stream"},  
	{".bmp",    "image/bmp"},  
	{".c",  	"text/plain"},  
	{".class",  "application/octet-stream"},  
	{".conf",   "text/plain"},  
	{".cpp",    "text/plain"},  
	{".doc",    "application/msword"},  
	{".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},  
	{".xls",    "application/vnd.ms-excel"},   
	{".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},  
	{".exe",    "application/octet-stream"},  
	{".gif",    "image/gif"},  
	{".gtar",  	"application/x-gtar"},  
	{".gz",		"application/x-gzip"},  
	{".h", 		"text/plain"},  
	{".htm",    "text/html"},  
	{".html",   "text/html"},  
	{".jar",    "application/java-archive"},  
	{".java",   "text/plain"},  
	{".jpeg",   "image/jpeg"},  
	{".jpg",    "image/jpeg"},  
	{".js", 	"text/x-javascript"},  
	{".log",    "text/plain"},  
	{".m3u",    "audio/x-mpegurl"},  
	{".m4a",    "audio/mp4a-latm"},  
	{".m4b",    "audio/mp4a-latm"},  
	{".m4p",    "audio/mp4a-latm"},  
	{".m4u",    "video/vnd.mpegurl"},  
	{".m4v",    "video/x-m4v"},   
	{".mid",	"audio/midi"},
	{".mov",    "video/quicktime"},  
	{".mp2",    "audio/x-mpeg"},  
	{".mp3",    "audio/x-mpeg"},
	{".mp4",    "video/mp4"},  
	{".mpc",    "application/vnd.mpohun.certificate"},        
	{".mpe",    "video/mpeg"},    
	{".mpeg",   "video/mpeg"},    
	{".mpg",    "video/mpeg"},    
	{".mpg4",   "video/mp4"},     
	{".mpga",   "audio/mpeg"},  
	{".msg",    "application/vnd.ms-outlook"},  
	{".ogg",    "audio/ogg"},  
	{".pdf",    "application/pdf"},  
	{".png",    "image/png"},  
	{".pps",    "application/vnd.ms-powerpoint"},  
	{".ppt",    "application/vnd.ms-powerpoint"},  
	{".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},  
	{".prop",   "text/plain"},  
	{".rc", 	"text/plain"},  
	{".rmvb",   "audio/x-pn-realaudio"},  
	{".rtf",    "application/rtf"},  
	{".sh", 	"text/plain"},  
	{".tar",    "application/x-tar"},     
	{".tgz",    "application/x-compressed"},   
	{".txt",    "text/plain"},
	{".vec",	"application/vec"},
	{".wav",    "audio/x-wav"},  
	{".wma",    "audio/x-ms-wma"},  
	{".wmv",    "audio/x-ms-wmv"},  
	{".wps",    "application/vnd.ms-works"},  
	{".xml",    "text/plain"},  
	{".z",  	"application/x-compress"},  
	{".zip",    "application/x-zip-compressed"},
	{"",        "*/*"}    
	};
	public static void open(String f,String path)
	{
		final Intent e=new Intent();
		e.putExtra("path",path);
		String[] g=util.getMIMEType(new File(path)).split("/");
		//1类
		switch(g[0])
		{
			case "image":
				API.startService(ctx,e.putExtra("type",1),cls.IMAGEVIEWER);
				break;
			case "application":
				switch(g[1])
				{
					case "vec":
						API.startService(ctx,e.putExtra("type",2),cls.IMAGEVIEWER);
						break;
					default:
					openFile(new File(path));
				}
				break;
			case "audio":
				API.startService(ctx,e,cls.PLAYER);
				break;
			case "text":
				switch(g[1])
				{
					case "html":
						API.startService(ctx,e.putExtra("url",f),cls.WEBVIEWER);
						break;
					default:
						API.startService(ctx,e,cls.TEXTEDITOR);
						break;

				}
				break;
			default:
				new myDialog.Builder(ctx)
				.setTitle("选择打开方式")
				.setItems("文本,音频,视频,图片".split(","),new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						switch(p2)
						{
							case 0:
								API.startService(ctx,e,cls.TEXTEDITOR);
								break;
							case 1:
								API.startService(ctx,e,cls.PLAYER);
								break;
							case 2:
								API.startService(ctx,e,cls.PLAYER);
								break;
							case 3:
								API.startService(ctx,e.putExtra("type",1),cls.IMAGEVIEWER);
								break;
						}
					}
				})
				.setNegativeButton("取消",null)
				.show();
				break;
		}
	}
}
