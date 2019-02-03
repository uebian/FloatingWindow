package com.yzrilyzr.lock;

import android.content.*;
import android.content.pm.*;
import android.view.*;
import java.lang.reflect.*;
import java.util.zip.*;

import android.content.res.Resources;
import java.io.InputStream;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

public class API
{
    public static Intent intent=new Intent();
    public static final String WINDOW_CLASS="com.yzrilyzr.floatingwindow.Window";
    public static final int API_VERSION=2;
    public static void startMainService(Context ctx,String targetClass)
    {
        intent.setAction("com.yzrilyzr.Service");
        intent.setPackage("com.yzrilyzr.floatingwindow");
        intent.putExtra("pkg",ctx.getPackageName());
        intent.putExtra("class",targetClass);
        //Intent i2=new Intent(createExplicitFromImplicitIntent(ctx,intent));
        ctx.startService(intent);
    }
    public static void startMainActivity(Context ctx,String targetClass)
    {
        intent.putExtra("IDATA","ACTIVITY");
        startMainService(ctx,targetClass);
    }
    public static InputStream getPkgFile(Context ctx,String pkgName,String file) 
    {
        try
        {
            String apk=ctx.getPackageManager().getPackageInfo(pkgName,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
            ZipFile f=new ZipFile(apk);
            ZipEntry en=f.getEntry(file);
            return f.getInputStream(en);
        }
        catch(Throwable e)
        {System.out.println(e);}
        return null;
    }
    public static View parseXmlViewFromFile(Context ctx,String pkgName,String file) 
    {
        try
        {
            InputStream is = getPkgFile(ctx,pkgName,file);
            byte[] data=new byte[is.available()];
            is.read(data);
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
    private static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent)
    {  
        PackageManager pm = context.getPackageManager();  
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);  
        if (resolveInfo == null || resolveInfo.size() != 1)return null; 
        ResolveInfo serviceInfo = resolveInfo.get(0);  
        String packageName = serviceInfo.serviceInfo.packageName;  
        String className = serviceInfo.serviceInfo.name;  
        ComponentName component = new ComponentName(packageName, className);  
        Intent explicitIntent = new Intent(implicitIntent);  
        explicitIntent.setComponent(component);  
        return explicitIntent;  
    }  
    public static Object class2Object(String clazz,Object... param) 
    {
        try
        {
            Class c=Class.forName(clazz);
            Constructor con=c.getDeclaredConstructor(getParamType(param));
            return con.newInstance(param);
        }
        catch(Throwable e)
        {System.out.println(e);}
        return null;
    }
    public static Object class2Object(String clazz,Class[] paramType,Object... param) 
    {
        try
        {
            Class c=Class.forName(clazz);
            Constructor con=c.getDeclaredConstructor(paramType);
            return con.newInstance(param);
        }
        catch(Throwable e)
        {System.out.println(e);}
        return null;
    }
    public static Class<?>[] getParamType(Object... param)
    {
        Class[] pt=null;
        if(param!=null)
        {
            pt=new Class[param.length];
            int index=0;
            for(Object c:param)
            {
                pt[index++]=c.getClass();
            }
        }
        return pt;
    }
    public static Object invoke(Object o,String name,Object... param) 
    {
        try
        {
            Class a=o.getClass();
            Method[] ms=a.getDeclaredMethods();
            for(Method m:ms)
            {
                if(m.getName().equals(name))
                {
                    m.setAccessible(true);
                    return m.invoke(o,param);
                }
            }
        }
        catch(Throwable e)
        {System.out.println(e);}
        return null;
    }
    public static Object invoke(Object o,String name,Class[] paramType,Object... param) 
    {
        try
        {
            Class a=o.getClass();
            Method m=a.getDeclaredMethod(name,paramType);
            m.setAccessible(true);
            return m.invoke(o,param);
        }
        catch(Throwable e)
        {System.out.println(e);}
        return null;
    }
    public static Object getField(Object o,String name) 
    {
        try
        {
            Class c=o.getClass();
            Field f=c.getDeclaredField(name);
			f.setAccessible(true);
            return f.get(o);
        }
        catch(Throwable e)
        {System.out.println(e);}
        return null;
    }
    public static final class ButtonCode
    {
        public static final int MIN=0,MAX=1,FOCUS=2,CLOSE=3,FOCUS_LONG=4,MIN_LONG=5,MAX_LONG=6,CLOSE_LONG=7;
    }
    public interface WindowInterface
    {
        public abstract void onButtonDown(int code);
        public abstract void onSizeChanged(int w,int h,int oldw,int oldh);
        public abstract void onPositionChanged(int x,int y);
    }
    public static int dip2px(float dipValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (int)(dipValue * scale + 0.5f);
    }

}
