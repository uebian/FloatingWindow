package com.yzrilyzr.floatingwindow.pluginapi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import com.yzrilyzr.floatingwindow.mBroadcastReceiver;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.xmlpull.v1.XmlPullParser;

public class API
{
    public static final String WINDOW_CLASS="com.yzrilyzr.floatingwindow.Window";
    public static final float density=Resources.getSystem().getDisplayMetrics().density;
    public static void startService(Context ctx,String targetClass)
    {
        startService(ctx,new Intent(),targetClass);
    }
	public static void startServiceForResult(Context ctx,Intent intent,BroadcastReceiver h,String targetClass)
    {
		int ind=mBroadcastReceiver.index++;
		mBroadcastReceiver.cbk.put(ind,h);
        startService(ctx,intent.putExtra("rescode",ind),targetClass);
    }
	public static void startServiceForResult(Context ctx,BroadcastReceiver h,String targetClass)
    {
		int ind=mBroadcastReceiver.index++;
		mBroadcastReceiver.cbk.put(ind,h);
        startService(ctx,new Intent().putExtra("rescode",ind),targetClass);
    }
    public static void startService(Context ctx,Intent intent,String targetClass)
    {
        intent.setAction("com.yzrilyzr.Service");
        intent.setPackage("com.yzrilyzr.floatingwindow");
        intent.putExtra("pkg",ctx.getPackageName());
        intent.putExtra("class",targetClass);
        ctx.startService(intent);
    }
	public static void callBack(Context ctx,Intent extra,int code){
		ctx.sendBroadcast(extra.setAction("com.yzrilyzr.callback")
		.putExtra("rescode",code));
	}
    public static InputStream getPkgFile(Context ctx,String pkgName,String file)throws Throwable
    {
        String apk=ctx.getPackageManager().getPackageInfo(pkgName,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
		ZipFile f=new ZipFile(apk);
		ZipEntry en=f.getEntry(file);
		return f.getInputStream(en);
    }
	public static View parseView(Context ctx,int id){
		return LayoutInflater.from(ctx).inflate(ctx.getResources().getLayout(id),null);
	}
    public static void exPkgFile(Context ctx,String pkgName,String file,String to) throws Throwable
    {
        String apk=ctx.getPackageManager().getPackageInfo(pkgName,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
		ZipFile f=new ZipFile(apk);
		ZipEntry en=f.getEntry(file);
        InputStream i=f.getInputStream(en);
		BufferedOutputStream o=new BufferedOutputStream(new FileOutputStream(to));
		byte[] b=new byte[2048];
		int p=0;
		while((p=i.read(b))!=-1)o.write(b,0,p);
		i.close();
		o.close();
		f.close();
    }
	public static View parseXmlViewFromFile(Context ctx,String pkgName,String file) 
    {
        try
        {
            InputStream is = getPkgFile(ctx,pkgName,file);
            byte[] data=new byte[is.available()];
            is.read(data);
			is.close();
            Class<?> clazz = Class.forName("android.content.res.XmlBlock");
            Constructor<?> constructor = clazz.getDeclaredConstructor(byte[].class);
            constructor.setAccessible(true);
            Object block = constructor.newInstance(data);
            Method method = clazz.getDeclaredMethod("newParser");
            method.setAccessible(true);
            XmlPullParser parser = (XmlPullParser) method.invoke(block);
            return LayoutInflater.from(ctx).inflate(parser,null);
        }
        catch(Throwable e)
        {System.out.println(e);}
        return null;
    }
	public static int dip(int pxValue)
    {
        return (int) (pxValue / density + 0.5f);
    }
    public static int px(float dipValue)
    {
        return  (int)(dipValue*density+0.5f);
    }
}
