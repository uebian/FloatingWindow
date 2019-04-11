package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.PluginService;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.view.StarterView;
import com.yzrilyzr.floatingwindow.viewholder.HolderGrid;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PluginPicker implements AdapterView.OnItemClickListener,Window.OnSizeChanged,Window.OnButtonDown
{
	Context ctx;
	private static CopyOnWriteArrayList<Map<String,Object>> last=new CopyOnWriteArrayList<Map<String,Object>>();
	private static CopyOnWriteArrayList<Map<String,Object>> cache=new CopyOnWriteArrayList<Map<String,Object>>();
	Window w;
	int mode=0,which=-1;
	private static GridView mlv=null;
	public PluginPicker(final Context c,Intent e)
	{
		ctx=c;
		mode=e.getIntExtra("mode",0);
		which=e.getIntExtra("which",-1);
		w=new Window(c,util.px(250),util.px(300))
		.setTitle(mode==0?"启动程序":"选择程序")
		.setIcon("class")
		.setOnSizeChanged(this)
		.setOnButtonDown(this);
		BaseAdapter ad=new BaseAdapter(){

			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return last.size();
			}

			@Override
			public Object getItem(int p1)
			{
				// TODO: Implement this method
				return null;
			}

			@Override
			public long getItemId(int p1)
			{
				// TODO: Implement this method
				return 0;
			}

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
				return HolderGrid.get(ctx,last.get(position),convertView);
			}
        };
        mlv=new GridView(c);
        mlv.setAdapter(ad);
		mlv.setNumColumns(w.getLayoutParams().width/util.px(50));
		mlv.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        w.addView(mlv);
        w.show();
        mlv.setOnItemClickListener(this);
		new ReadPlugin().execute();
	}
	public static void load()
	{
		new ReadPlugin().execute();
	}
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mlv.setNumColumns(w/util.px(50));
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.MIN)new ReadPlugin().execute();
		else if(code==Window.ButtonCode.CLOSE)mlv=null;
	}
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		String p=(String)last.get(p3).get("pkg");
		String c=(String)last.get(p3).get("class");
		if(c.startsWith("."))c=p+c;
		w.dismiss();
		if(mode==0)
			PluginService.loadPlugin(ctx,new Intent()
			.putExtra("pkg",p)
			.putExtra("class",c));
		else
		{
			BitmapDrawable d=(BitmapDrawable)last.get(p3).get("icon");
			Bitmap b=null;
			if(d!=null)b=d.getBitmap();
			ByteArrayOutputStream o=new ByteArrayOutputStream();
			if(b!=null)
			{
				b.compress(Bitmap.CompressFormat.PNG,100,o);
			}
			util.getSPWrite("pluginpicker")
			.putString("pkg"+which,p)
			.putString("cls"+which,c)
			.putString("tip"+which,(String)last.get(p3).get("text1"))
			.putString("ico"+which,Base64.encodeToString(o.toByteArray(),0))
			.commit();
			StarterView.load(ctx);
		}
	}
	static class ReadPlugin extends AsyncTask
	{
		@Override
		protected void onPostExecute(Object result)
		{
			// TODO: Implement this method
			super.onPostExecute(result);
			if(mlv!=null)((BaseAdapter)mlv.getAdapter()).notifyDataSetChanged();
		}
		@Override
		protected Object doInBackground(Object[] p1)
		{
			cache.clear();
			PackageManager pm=util.ctx.getPackageManager();
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
								cls=cls.replace(" ","");
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
											m.put("icon",new BitmapDrawable(VECfile.createBitmap(VECfile.readFileFromIs(API.getPkgFile(util.ctx,appInfo.packageName,"assets/"+g[1].substring(1)+".vec")),util.px(64),util.px(64))));
										else if(g[1].startsWith("D"))m.put("icon",new BitmapDrawable(BitmapFactory.decodeStream(API.getPkgFile(util.ctx,appInfo.packageName,g[1].substring(1)))));
									}
									m.put("pkg",appInfo.packageName);
									m.put("text1",k[0]);
									m.put("class",g[0]);
									m.put("text2",appInfo.loadLabel(pm));
									cache.add(m);
								}
							}
						}
					}
					catch(Throwable ep)
					{
						util.toast("读取插件信息错误:"+appInfo.loadLabel(pm)+"\n详情查看控制台");
						ep.printStackTrace();
					}
				}
			}
			File d=new File(util.mainDir+"插件包");
			if(!d.exists())d.mkdirs();
			File[] fs=d.listFiles();
			for(File f:fs)
			{
				try
				{
					String cls=util.readwithN(PluginService.getPluginPkgFile(f.getAbsolutePath(),"info"));
					if(cls!=null)
					{
						cls=cls.replaceAll(" |\\n|	","");
						String[] cs=cls.split(";");
						for(String s:cs)
						{
							if("".equals(s))continue;
							String[] k=s.split(":");
							if(k.length<2)continue;
							String[] g=k[1].split("@");
							Map<String,Object> m=new HashMap<String,Object>();
							if(g.length==1)m.put("icon",new BitmapDrawable(VECfile.createBitmap(util.ctx,"class",util.px(64),util.px(64))));
							else if(g.length==2)
							{
								InputStream in=PluginService.getPluginPkgFile(f.getAbsolutePath(),g[1].substring(1));
								if(g[1].startsWith("V"))
									m.put("icon",new BitmapDrawable(VECfile.createBitmap(VECfile.readFileFromIs(in),util.px(64),util.px(64))));
								else if(g[1].startsWith("D"))m.put("icon",new BitmapDrawable(BitmapFactory.decodeStream(in)));
							}
							m.put("pkg","pkg:"+f.getAbsolutePath());
							m.put("text1",k[0]);
							m.put("class",g[0]);
							m.put("text2","插件包");
							cache.add(m);
						}
					}
				}
				catch(Throwable e)
				{
					util.toast("读取插件包信息错误:"+f.getName()+"\n详情查看控制台");
					e.printStackTrace();
				}
			}
			last.clear();
			for(Map<String,Object> k:cache)last.add(k);
//			Collections.sort(last,new Comparator<Map<String,Object>>(){
//				@Override
//				public int compare(Map<String, Object> p1, Map<String, Object> p2)
//				{
//					return ((String)p1.get("text1")).compareToIgnoreCase((String)p2.get("text1"));
//				}
//			});
			return null;
		}
	}
}
