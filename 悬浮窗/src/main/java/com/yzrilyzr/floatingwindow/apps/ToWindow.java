package com.yzrilyzr.floatingwindow.apps;

import android.widget.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yzrilyzr.floatingwindow.MainActivity;
import com.yzrilyzr.floatingwindow.PluginContext;
import com.yzrilyzr.floatingwindow.PluginService;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myTextView;
import com.yzrilyzr.ui.uidata;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;

public class ToWindow implements AdapterView.OnItemClickListener
{
	Context ctx;
	List<Map<String,Object>> l=new ArrayList<Map<String,Object>>();
	Window w;
	public ToWindow(final Context c,Intent e) throws PackageManager.NameNotFoundException
	{
		ctx=c;
		w=new Window(c,util.px(250),util.px(300))
            .setTitle("选择程序")
            .setIcon("class");
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager pm=c.getPackageManager();
		List<ResolveInfo> packageInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo appInfo : packageInfos)
        {
            Map<String,Object> m=new HashMap<String,Object>();
			m.put("icon",appInfo.loadIcon(pm));
			m.put("pkg",appInfo.activityInfo.packageName);
			m.put("main",appInfo.activityInfo.name);
			m.put("name",appInfo.loadLabel(pm));
			l.add(m);
        }
        SimpleAdapter ad=new SimpleAdapter(c,l,R.layout.window_applist_entry,new String[]{"icon","name","info"},new int[]{R.id.windowapplistentryImageView1,R.id.windowapplistentryTextView1,R.id.windowapplistentryTextView2}){ 
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                ViewHolder holder;
                if(convertView==null)
                {
                    convertView=LayoutInflater.from(c).inflate(R.layout.window_applist_entry,parent,false);
                    holder=new ViewHolder();
                    holder.text1 = (TextView) convertView.findViewById(R.id.windowapplistentryTextView1);
                    holder.text2 = (TextView) convertView.findViewById(R.id.windowapplistentryTextView2);
                    holder.icon = (ImageView) convertView.findViewById(R.id.windowapplistentryImageView1);
                    convertView.setTag(holder);
                }
                else holder=(ViewHolder) convertView.getTag();
                Map m=l.get(position);
                holder.text1.setText(""+m.get("name"));
                holder.text2.setVisibility(8);
				holder.text1.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.9f));
                Object o=m.get("icon");
                if(o!=null)holder.icon.setImageDrawable((Drawable)o);
                return convertView;
			}
        };
        GridView mlv=new GridView(c);
        mlv.setAdapter(ad);
		mlv.setNumColumns(4);
		mlv.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        w.addView(mlv);
        w.show();
        mlv.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		String p=(String)l.get(p3).get("main");
		String pp=(String)l.get(p3).get("pkg");
		loadPlugin(p,pp);
	}
	private void setField(Activity o,String a,Object v){
		try
		{
			Field f=Activity.class.getField(a);
			f.setAccessible(true);
			f.set(o,v);
		}
		catch (NoSuchFieldException e)
		{}
		catch (IllegalAccessException e)
		{}
		catch (IllegalArgumentException e)
		{}
	}
	public void loadPlugin(final String activity,final String pkg)
    {
        PackageManager PackageManager=ctx.getPackageManager();
        try
        {
			String path=PackageManager.getPackageInfo(pkg,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
            ClassLoader mainloader=ctx.getClassLoader();
            PathClassLoader pcl=new PathClassLoader(path,mainloader);
            Class c=pcl.loadClass(activity);
			Activity ac=(Activity) c.newInstance();
			Method[] ms=Activity.class.getDeclaredMethods();
			Method m=null;
			for(Method l:ms)
			if("onCreate".equals(l.getName())&&l.getParameterTypes().length==1){
				m=l;
				break;
			}
			m.setAccessible(true);
			setField(ac,"mActivityInfo",new ActivityInfo());
			//m.invoke(ac,new PluginContext(ctx,pkg,path),MainActivity.activityThread,null,null,0,util.ctx,new Intent(),new ActivityInfo(),"iii",null,"test",null,util.ctx.getResources().getConfiguration());*/
			m.invoke(ac,null);
        }
        catch(Throwable e)
        {
			String pkginfo="";
			try
			{
				pkginfo=PackageManager.getPackageInfo(pkg,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.loadLabel(PackageManager)+"";
			}
			catch (PackageManager.NameNotFoundException e2)
			{
				pkginfo="未知";
			}
			myTextView mtv=new myTextView(ctx);
			mtv.setText("插件:"+pkginfo+"("+pkg+")\n\nstacktrace:\n"+PluginService.handleException(e));
			mtv.setTextIsSelectable(true);
			mtv.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.8f));
			ScrollView sv=new ScrollView(ctx);
			sv.addView(mtv);
			new Window(ctx,util.px(300),util.px(200))
				.addView(sv)
				.setTitle("出错了")
				.show();
        }
    }
	static class ViewHolder
	{
		TextView text1,text2;
		ImageView icon;
	}
}
