package com.yzrilyzr.floatingwindow.apps;
import com.yzrilyzr.ui.*;
import java.io.*;
import java.util.*;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.view.Graph;
import com.yzrilyzr.floatingwindow.viewholder.HolderList;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;

public class Manager implements Runnable,Window.OnButtonDown
{
	private ListView l,l2,l3;
	private Window w;
	private Context ctx;
	private Handler han;
	private boolean run=true;
	private Thread[] list=new Thread[0];
	private List<ApplicationInfo> apps=new ArrayList<ApplicationInfo>();
	private myTextView txt;
	private Graph graph,cpu,mem,io,net;
	private myProgressBar mpb;
	private ArrayList<String> whitelist=new ArrayList<String>();
	private ThreadGroup topGroup;
	private myViewPager page;
	public Manager(Context c,Intent e)
	{
		this.ctx=c;
		han=new Handler(c.getMainLooper());
		l=new myListView(c);
		l2=new myListView(c);
		l3=new myListView(c);
		w=new Window(c,util.px(300),util.px(400))
			.setTitle("管理器")
			.setIcon("manager")
			.setOnButtonDown(this)
			.show();
		myTabLayout t=new myTabLayout(ctx);
		t.setItems("窗口管理","任务管理","线程查看","程序状态","系统性能");
		page=new myViewPager(ctx);
		w.addView(t);
		w.addView(page);
		t.setViewPager(page);
		page.setTabLayout(t);
		w.getLayoutParams().type=WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		w.dismiss().show();
		l.setAdapter(new Adapter());
		l2.setAdapter(new Adapter2());
		l3.setAdapter(new Adapter3());
		ScrollView sv=new ScrollView(ctx);
        txt=new myTextView(ctx);
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
       	lp.weight=1;
        sv.addView(txt);
        mpb=new myProgressBar(ctx);
        mpb.setMax((int)(Runtime.getRuntime().maxMemory()/1048576));
        txt.setText("载入中…");
		LinearLayout li=new LinearLayout(c);
		li.setOrientation(1);
        li.addView(sv,lp);
		li.addView(graph=new Graph(c));
        li.addView(mpb);
		myButton b=new myButton(c);
		b.setText("清理内存");
		li.addView(b);
		b.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					System.gc();
				}
			});
		myButton b22=new myButton(c);
		b22.setText("ANR测试(10s)(请务必保存所有任务)");
		li.addView(b22);
		b22.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					new myDialog.Builder(ctx)
						.setTitle("ANR测试(10s)")
						.setMessage("此操作会让程序无响应，确定要继续吗？")
						.setNegativeButton("取消",null)
						.setPositiveButton("确定",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								try
								{
									Thread.sleep(10000);
								}
								catch (InterruptedException e)
								{}
							}
						})
						.show();
				}
			});
		b22=new myButton(c);
		b22.setText("程序崩溃测试(请务必保存所有任务)");
		li.addView(b22);
		b22.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					new myDialog.Builder(ctx)
						.setTitle("崩溃测试")
						.setMessage("此操作会让程序崩溃，确定要继续吗？")
						.setNegativeButton("取消",null)
						.setPositiveButton("确定",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								throw new RuntimeException("崩溃测试");
							}
						})
						.show();
				}
			});
		LinearLayout li2=new LinearLayout(c);
		li2.setOrientation(1);
        li2.addView(l2);
		myButton b2=new myButton(c);
		b2.setText("全部停止");
		li2.addView(b2);
		util.setWeight(l2);
		b2.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					new Thread(){
						public void run()
						{
							try
							{
								for(ApplicationInfo a:apps)
								{
									if(!ctx.getPackageName().equals(a.packageName)&&!whitelist.contains(a.packageName))
										forceStopAPK(a.packageName);
								}
								util.toast("已停止所有任务");
								new Task().execute();
							}
							catch(Throwable e)
							{}
						}
					}.start();
				}
			});
		sv=new ScrollView(ctx);
		LinearLayout v=new LinearLayout(ctx);
		v.setOrientation(1);
		myTextView m=new myTextView(ctx);
		m.setText("CPU");
		v.addView(m);
		v.addView(cpu=new Graph(c));
		m=new myTextView(ctx);
		m.setText("内存");
		v.addView(m);
		v.addView(mem=new Graph(c));
		m=new myTextView(ctx);
		m.setText("磁盘IO");
		v.addView(m);
		v.addView(io=new Graph(c));
		m=new myTextView(ctx);
		m.setText("网络");
		v.addView(m);
		v.addView(net=new Graph(c));
		sv.addView(v);
		page.setPages(l,li2,l3,li,sv);
		ThreadGroup group=Thread.currentThread().getThreadGroup();  
		topGroup=group;  
		while (group!=null)
		{  
			topGroup=group;  
			group=group.getParent();  
		}
		Set<String> set=util.getSPRead().getStringSet("managerwhitelist",null);
		if(set!=null)
		{
			for(String z:set)whitelist.add(z);
		}
		han.post(this);
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)run=false;
	}
	@Override
	public void run()
	{
		new Task().execute();
		if(run)han.postDelayed(this,1000);
	}
	class Adapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return Window.windowList.size();
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
			HolderList holder;
			if(convertView==null)
			{
				holder=new HolderList(ctx);
				convertView=holder.vg;
				convertView.setTag(holder);
			}
			else holder=(HolderList) convertView.getTag();
			if(position<Window.windowList.size())
			{
				Window w=Window.windowList.get(position);
				holder.v[0].setImageBitmap(w.getIcon());
				holder.text.setText(w.getTitle());
				holder.v[1].setOnClickListener(new Cl4(position));
				holder.v[1].setImageVec("focusedwin");
				holder.v[2].setOnClickListener(new Cl(position));
				holder.v[2].setImageVec("closewin");
				holder.v[3].setVisibility(8);
			}
			return convertView;
		}
	}
	class Adapter2 extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return apps.size();
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
			HolderList holder;
			if(convertView==null)
			{
				holder=new HolderList(ctx);
				convertView=holder.vg;
				convertView.setTag(holder);
			}
			else holder=(HolderList) convertView.getTag();
			if(position<apps.size())
			{
				ApplicationInfo ap=apps.get(position);
				holder.v[0].setImageDrawable(ap.loadIcon(ctx.getPackageManager()));
				holder.text.setText(ap.loadLabel(ctx.getPackageManager()));
				holder.v[1].setOnClickListener(new Cl3(position));
				holder.v[1].setImageVec(whitelist.contains(ap.packageName)?"lock":"unlock");
				holder.v[2].setOnClickListener(new Cl2(position));
				holder.v[2].setImageVec("closewin");
				holder.v[3].setVisibility(8);
			}
			return convertView;
		}
	}
	class Adapter3 extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return list.length;
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
		public View getView(int p1, View p2, ViewGroup p3)
		{
			Thread t=list[p1];
			if(p2==null)p2=LayoutInflater.from(ctx).inflate(R.layout.window_manager_entry,null);
			((ViewGroup)p2).getChildAt(0).setVisibility(8);
			((TextView)((ViewGroup)p2).getChildAt(1)).setText(String.format("%s 优先级:%d",t.getName(),t.getPriority()));
			return p2;
		}
	}
	class Cl3 implements OnClickListener
	{
		int y=0;
		public Cl3(int i)
		{
			y=i;
		}
		@Override
		public void onClick(View p1)
		{
			String k=apps.get(y).packageName;
			boolean b=whitelist.contains(k);
			if(b)whitelist.remove(k);
			else whitelist.add(k);
			b=!b;
			((VecView)p1).setImageVec(b?"lock":"unlock");
			TreeSet<String> s=new TreeSet<String>();
			for(String l:whitelist)s.add(l);
			util.getSPWrite().putStringSet("managerwhitelist",s).commit();
		}
	}
	class Cl2 implements OnClickListener
	{
		int y=0;
		public Cl2(int i)
		{
			y=i;
		}
		@Override
		public void onClick(final View p1)
		{
			try{
			forceStopAPK(apps.get(y).packageName);
			TranslateAnimation t=new TranslateAnimation(1,0,1,1,1,0,1,0);
			t.setDuration(300);
			t.setAnimationListener(new Animation.AnimationListener(){
					@Override
					public void onAnimationStart(Animation p1)
					{
						// TODO: Implement this method
					}
					@Override  
					public void onAnimationEnd(Animation p)
					{  
						apps.remove(y);
						((Adapter2)l2.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onAnimationRepeat(Animation p1)
					{
						// TODO: Implement this method
					}
				});
			((View)p1.getParent()).startAnimation(t);
			util.toast("已停止 "+apps.get(y).loadLabel(ctx.getPackageManager()));
			new Task().execute();
			}catch(Throwable e){}
		}
	}
	class Cl4 implements OnClickListener
	{
		int y=0;
		public Cl4(int i)
		{
			y=i;
		}
		@Override
		public void onClick(View p1)
		{
			Window w=Window.windowList.get(y);
			if(w.getMin())w.toggleMinWin();
			else
			{
				w.dismiss();
				w.show();
			}
		}
	}
	class Cl implements OnClickListener
	{
		int y=0;
		public Cl(int i)
		{
			y=i;
		}
		@Override
		public void onClick(View p1)
		{
			Window.windowList.get(y).dismiss();
			TranslateAnimation t=new TranslateAnimation(1,0,1,1,1,0,1,0);
			t.setDuration(300);
			t.setAnimationListener(new Animation.AnimationListener(){
					@Override
					public void onAnimationStart(Animation p1)
					{
						// TODO: Implement this method
					}
					@Override  
					public void onAnimationEnd(Animation p)
					{  
						((Adapter)l.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onAnimationRepeat(Animation p1)
					{
						// TODO: Implement this method
					}
				});
			((View)p1.getParent()).startAnimation(t);
			new Task().execute();
		}
	}
	private void forceStopAPK(final String pkgName)
	{
		try
		{
			final Process sh = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(sh.getOutputStream());
			final String Command = "am force-stop "+pkgName+ "\n";
			os.writeBytes(Command);
			os.flush();
			os.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				Runtime.getRuntime().exec("am force-stop "+pkgName+"\n");
			}
			catch (IOException pe)
			{
				pe.printStackTrace();
			}
		}
	}
	class Task extends AsyncTask
	{
		private long max;
		private long total;
		private long free;
		private long usable;
		private Runtime ru;
		private float cpuf,memf,iof,netf;
		@Override
		protected Object doInBackground(Object[] p1)
		{
			int pg=page.getCurrentItem();
			int estimatedSize=topGroup.activeCount()*2;  
			Thread[] slackList=new Thread[estimatedSize];  
			int actualSize=topGroup.enumerate(slackList);  
			list=new Thread[actualSize];  
			System.arraycopy(slackList,0,list,0,actualSize);
			Arrays.sort(list,new Comparator<Thread>(){
					@Override
					public int compare(Thread p1, Thread p2)
					{
						// TODO: Implement this method
						return p1.getName().compareToIgnoreCase(p2.getName());
					}
				});
			ru = Runtime.getRuntime();
			max = ru.maxMemory();
			total = ru.totalMemory();
			free = ru.freeMemory();
			usable = max - total + free;
			final PackageManager pm = ctx.getPackageManager(); 
			// 查询所有已经安装的应用程序 
			List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES); 
			Collections.sort(listAppcations,new Comparator<ApplicationInfo>(){
					@Override
					public int compare(ApplicationInfo p1, ApplicationInfo p2)
					{
						return new String(p1.loadLabel(pm)+"").compareTo(p2.loadLabel(pm)+"");
					}
				});// 排序 
			// 保存所有正在运行的包名 以及它所在的进程信息 
			Map<String, ActivityManager.RunningAppProcessInfo> pgkProcessAppMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>(); 
			ActivityManager mActivityManager = (ActivityManager)ctx. getSystemService(Context.ACTIVITY_SERVICE); 
			// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程 
			List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager 
				.getRunningAppProcesses(); 
			for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList)
			{ 
				String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包 
				// 输出所有应用程序的包名 
				for (int i = 0; i < pkgNameList.length; i++)
				{ 
					String pkgName = pkgNameList[i]; 
					pgkProcessAppMap.put(pkgName, appProcess); 
				} 
			} 
			apps.clear();
			for (ApplicationInfo ap : listAppcations)
			{ 
				// 如果该包名存在 则构造一个RunningAppInfo对象 
				if (pgkProcessAppMap.containsKey(ap.packageName))
				{ 
					boolean flag = false;
					if ((ap.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
					{
						flag = true;
					}
					else if ((ap.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
					{
						flag = true;
					}
					if(flag)apps.add(ap);
				} 
			}
			cpuf=cpuUsage();
			memf=memUsage();
			//iof=ioUsage();
			//netf=netUsage();
			return null;
		}
		@Override
		protected void onPostExecute(Object result)
		{
			graph.setMax((int)(max/1048576l));
			graph.addPoint((int)((total-free)/1048576l));
			txt.setText(String.format(
							"虚拟机最大内存:%dMB\n虚拟机可用内存:%dMB\n已分配内存:%dMB\n已分配内存中的剩余空间:%dMB\n可用的CPU个数:%d个\n",
							max/1048576l,usable/1048576l,total/1048576l,
							free/1048576l,ru.availableProcessors()));
			mpb.setSecondaryProgress((int)(total/1048576));
			mpb.setProgress((int)((total-free)/1048576));
			
			cpu.setMax(10000);
			cpu.addPoint((int)(cpuf*10000f));
			mem.setMax(10000);
			mem.addPoint((int)(memf*10000f));
			io.setMax(10000);
			io.addPoint((int)(iof*10000f));
			net.setMax(10000);
			net.addPoint((int)(netf*10000f));
			
			int pg=page.getCurrentItem();
			if(pg==0)((Adapter)l.getAdapter()).notifyDataSetChanged();
			else if(pg==1)((Adapter2)l2.getAdapter()).notifyDataSetChanged();
			else if(pg==2)((Adapter3)l3.getAdapter()).notifyDataSetChanged();
		}
	}
	public float cpuUsage()
	{
		System.out.println("开始收集cpu使用率");
		float cpuUsage = 0;
		Process pro1,pro2;
		Runtime r = Runtime.getRuntime();
		try
		{
			String command = "cat /proc/stat";
			//第一次采集CPU时间
			long startTime = System.currentTimeMillis();
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String line = null;
			long idleCpuTime1 = 0, totalCpuTime1 = 0;	//分别为系统启动后空闲的CPU时间和总的CPU时间
			while((line=in1.readLine()) != null)
			{	
				if(line.startsWith("cpu"))
				{
					line = line.trim();
					System.out.println(line);
					String[] temp = line.split("\\s+"); 
					idleCpuTime1 = Long.parseLong(temp[4]);
					for(String s : temp)
					{
						if(!s.equals("cpu"))
						{
							totalCpuTime1 += Long.parseLong(s);
						}
					}	
					System.out.println("IdleCpuTime: " + idleCpuTime1 + ", " + "TotalCpuTime" + totalCpuTime1);
					break;
				}						
			}	
			in1.close();
			pro1.destroy();
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				System.err.println("CpuUsage休眠时发生InterruptedException. " + e.getMessage());
				System.err.println(sw.toString());
			}
			//第二次采集CPU时间
			long endTime = System.currentTimeMillis();
			pro2 = r.exec(command);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			long idleCpuTime2 = 0, totalCpuTime2 = 0;	//分别为系统启动后空闲的CPU时间和总的CPU时间
			while((line=in2.readLine()) != null)
			{	
				if(line.startsWith("cpu"))
				{
					line = line.trim();
					System.out.println(line);
					String[] temp = line.split("\\s+"); 
					idleCpuTime2 = Long.parseLong(temp[4]);
					for(String s : temp)
					{
						if(!s.equals("cpu"))
						{
							totalCpuTime2 += Long.parseLong(s);
						}
					}
					System.out.println("IdleCpuTime: " + idleCpuTime2 + ", " + "TotalCpuTime" + totalCpuTime2);
					break;	
				}								
			}
			if(idleCpuTime1 != 0 && totalCpuTime1 !=0 && idleCpuTime2 != 0 && totalCpuTime2 !=0)
			{
				cpuUsage = 1 - (float)(idleCpuTime2 - idleCpuTime1)/(float)(totalCpuTime2 - totalCpuTime1);
				System.out.println("本节点CPU使用率为: " + cpuUsage);
			}				
			in2.close();
			pro2.destroy();
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			System.err.println("CpuUsage发生InstantiationException. " + e.getMessage());
			System.err.println(sw.toString());
		}	
		return cpuUsage;
	}
	public float memUsage()
	{
		System.out.println("开始收集memory使用率");
		float memUsage = 0.0f;
		Process pro = null;
		Runtime r = Runtime.getRuntime();
		try
		{
			String command = "cat /proc/meminfo";
			pro = r.exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line = null;
			int count = 0;
			long totalMem = 0, freeMem = 0;
			while((line=in.readLine()) != null)
			{	
				System.out.println(line);	
				String[] memInfo = line.split("\\s+");
				if(memInfo[0].startsWith("MemTotal"))
				{
					totalMem = Long.parseLong(memInfo[1]);
				}
				if(memInfo[0].startsWith("MemFree"))
				{
					freeMem = Long.parseLong(memInfo[1]);
				}
				memUsage = 1- (float)freeMem/(float)totalMem;
				System.out.println("本节点内存使用率为: " + memUsage);	
				if(++count == 2)
				{
					break;
				}				
			}
			in.close();
			pro.destroy();
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			System.err.println("MemUsage发生InstantiationException. " + e.getMessage());
			System.err.println(sw.toString());
		}	
		return memUsage;
	}
	public float ioUsage()
	{
		System.out.println("开始收集磁盘IO使用率");
		float ioUsage = 0.0f;
		Process pro = null;
		Runtime r = Runtime.getRuntime();
		try
		{
			String command = "iostat -d";
			pro = r.exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line = null;
			int count =  0;
			while((line=in.readLine()) != null)
			{		
				if(++count >= 4)
				{
//					System.out.println(line);
					String[] temp = line.split("\\s+");
					if(temp.length > 1)
					{
						float util =  Float.parseFloat(temp[temp.length-1]);
						ioUsage = (ioUsage>util)?ioUsage:util;
					}
				}
			}
			if(ioUsage > 0)
			{
				System.out.println("本节点磁盘IO使用率为: " + ioUsage);	
				ioUsage /= 100; 
			}			
			in.close();
			pro.destroy();
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			System.err.println("IoUsage发生InstantiationException. " + e.getMessage());
			System.err.println(sw.toString());
		}	
		return ioUsage;
	}
	public float netUsage()
	{
		System.out.println("开始收集网络带宽使用率");
		float netUsage = 0.0f;
		Process pro1,pro2;
		Runtime r = Runtime.getRuntime();
		try
		{
			String command = "cat /proc/net/dev";
			//第一次采集流量数据
			long startTime = System.currentTimeMillis();
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String line = null;
			long inSize1 = 0, outSize1 = 0;
			while((line=in1.readLine()) != null)
			{	
				line = line.trim();
				if(line.startsWith("wlan0"))
				{
					System.out.println(line);
					String[] temp = line.split("\\s+"); 
					inSize1 = Long.parseLong(temp[1]);	//Receive bytes,单位为Byte
					outSize1 = Long.parseLong(temp[9]);				//Transmit bytes,单位为Byte
					break;
				}				
			}	
			in1.close();
			pro1.destroy();
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				System.err.println("NetUsage休眠时发生InterruptedException. " + e.getMessage());
				System.err.println(sw.toString());
			}
			//第二次采集流量数据
			long endTime = System.currentTimeMillis();
			pro2 = r.exec(command);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			long inSize2 = 0 ,outSize2 = 0;
			while((line=in2.readLine()) != null)
			{	
				line = line.trim();
				if(line.startsWith("wlan0"))
				{
					System.out.println(line);
					String[] temp = line.split("\\s+"); 
					inSize2 = Long.parseLong(temp[1]);
					outSize2 = Long.parseLong(temp[9]);
					break;
				}				
			}
			if(inSize1 != 0 && outSize1 !=0 && inSize2 != 0 && outSize2 !=0)
			{
				float interval = (float)(endTime - startTime)/100;
				//网口传输速度,单位为bps
				float curRate = (float)(inSize2 - inSize1 + outSize2 - outSize1)*8/(1000000*interval);
				netUsage = curRate/1000;
				System.out.println("本节点网口速度为: " + curRate + "Mbps");
				System.out.println("本节点网络带宽使用率为: " + netUsage);
			}				
			in2.close();
			pro2.destroy();
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			System.err.println("NetUsage发生InstantiationException. " + e.getMessage());
			System.err.println(sw.toString());
		}	
		return netUsage;
	}


}
