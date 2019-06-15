package com.yzrilyzr.floatingwindow;

import android.widget.*;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.viewholder.HolderGrid;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myTabLayout;
import com.yzrilyzr.ui.myViewPager;
import android.view.KeyEvent;
import com.yzrilyzr.myclass.myComp;

public class Home extends myActivity implements AdapterView.OnItemClickListener
{

	Context ctx;
	List<Map<String,Object>> l=new ArrayList<Map<String,Object>>();
	List<Map<String,Object>> l2=new ArrayList<Map<String,Object>>();
	private GridView mlv;

	private GridView mlv2;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this,MainActivity.class));
		ctx=this;
		PackageManager pm=ctx.getPackageManager();
		List<ApplicationInfo> installedAppList =pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : installedAppList)
        {
            boolean flag = false;
            if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
            {
                flag = true;
            }
            else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                flag = true;
            }
            if (flag)
            {
				try
				{
					ApplicationInfo ai=pm.getApplicationInfo(appInfo.packageName,PackageManager.GET_META_DATA);
					if(ai==null||ai.metaData==null)continue;
					boolean is=ai.metaData.getBoolean("fwplugin",false);
					if(is)
					{
						String cls=ai.metaData.getString("fwpluginclasses",null);
						if(cls!=null)
						{
							String[] cs=cls.split(";");
							for(String s:cs)
							{
								String[] k=s.split(":");
								String[] g=k[1].split("@");
								Map<String,Object> m=new HashMap<String,Object>();
								if(g.length==1)m.put("icon",appInfo.loadIcon(pm));
								else if(g.length==2)
								{
									if(g[1].startsWith("V"))
										m.put("icon",new BitmapDrawable(VECfile.createBitmap(VECfile.readFileFromIs(API.getPkgFile(ctx,appInfo.packageName,"assets/"+g[1].substring(1)+".vec")),util.px(64),util.px(64))));
									else if(g[1].startsWith("D"))m.put("icon",new BitmapDrawable(BitmapFactory.decodeStream(API.getPkgFile(ctx,appInfo.packageName,g[1].substring(1)))));
								}
								m.put("pkg",appInfo.packageName);
								m.put("text1",k[0]);
								m.put("class",g[0]);
								m.put("text2",appInfo.loadLabel(pm));
								l.add(m);
							}
						}
					}
				}
				catch(Throwable ep)
				{

				}
            }
        }
        SimpleAdapter ad=new SimpleAdapter(ctx,l,R.layout.window_applist_entry,new String[]{"icon","name","info"},new int[]{R.id.windowapplistentryImageView1,R.id.windowapplistentryTextView1,R.id.windowapplistentryTextView2}){ 
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                return HolderGrid.get(ctx,l.get(position),convertView);
			}
        };
        mlv=new GridView(ctx);
        mlv.setAdapter(ad);
		mlv.setNumColumns(4);
		mlv.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        mlv.setOnItemClickListener(this);
		//{{{{{{{}}}}}}}
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> packageInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo appInfo : packageInfos)
        {
            Map<String,Object> m=new HashMap<String,Object>();
			m.put("icon",appInfo.loadIcon(pm));
			m.put("pkg",appInfo.activityInfo.packageName);
			m.put("main",appInfo.activityInfo.name);
			m.put("text1",appInfo.loadLabel(pm));
			l2.add(m);
        }
		Collections.sort(l2,new myComp<Map>(){
				@Override
				public int compare(Map p1, Map p2)
				{
					return ((String)p1.get("text1")).compareToIgnoreCase((String)p2.get("text1"));
				}
			});
        SimpleAdapter ad2=new SimpleAdapter(ctx,l2,R.layout.window_applist_entry,new String[]{"icon","name","info"},new int[]{R.id.windowapplistentryImageView1,R.id.windowapplistentryTextView1,R.id.windowapplistentryTextView2}){ 
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                return HolderGrid.get(ctx,l2.get(position),convertView);
			}
        };
        mlv2=new GridView(ctx);
        mlv2.setAdapter(ad2);
		mlv2.setNumColumns(4);
		mlv2.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        mlv2.setOnItemClickListener(this);
		//{{{{{{{}}}}}}}

		LinearLayout l=new LinearLayout(ctx);
		l.setOrientation(1);
		setContentView(l);
		myTabLayout t=new myTabLayout(ctx);
		myViewPager v=new myViewPager(ctx);
		t.setItems("插件","所有应用");
		t.setViewPager(v);
		v.setTabLayout(t);
		l.addView(t);
		l.addView(v);
		v.setPages(mlv,mlv2);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO: Implement this method
		return true;
	}
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		if(p1==mlv)
		{
			String p=(String)l.get(p3).get("pkg");
			String c=(String)l.get(p3).get("class");
			if(c.startsWith("."))c=p+c;
			PluginService.loadPlugin(ctx,new Intent()
									 .putExtra("pkg",p)
									 .putExtra("class",c));
		}
		else if(p1==mlv2)
		{
			String pp=(String)l2.get(p3).get("pkg");
			util.runAppByPkgName(pp);
		}
	}
}
