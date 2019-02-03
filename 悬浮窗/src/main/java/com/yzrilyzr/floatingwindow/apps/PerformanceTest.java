package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.floatingwindow.API;
import android.os.Handler;
import com.yzrilyzr.floatingwindow.view.PerfTestView;
import com.yzrilyzr.ui.myTextView;

public class PerformanceTest implements OnClickListener,Runnable,Window.OnButtonDown
{
	boolean start=false;
	private Window w,vtest;
	private Context ctx;
	private myButton b;
	private myTextView t;
	private PerfTestView perfview;
	public PerformanceTest(Context c,Intent e)
	{
		ctx=c;
		w=new Window(c,util.px(200),util.px(250))
			.setTitle("性能测试")
			.setBar(8,8,0)
			.setCanFocus(false)
			.setOnButtonDown(this)
			.setIcon("performance")
			.show();
		b=new myButton(c);
		t=new myTextView(c);
		b.setText("开始测试");
		w.addView(b);
		w.addView(t);
		b.setOnClickListener(this);
		vtest=new Window(c,util.getScreenWidth(),util.getScreenHeight())
			.setTitle("性能测试-View绘图测试")
			.setBar(8,8,0)
			.setCanFocus(false)
			.setCanResize(false)
			.setOnButtonDown(new Window.OnButtonDown(){
				@Override
				public void onButtonDown(int code)
				{
					start=false;
				}
			})
			.setIcon("performance");
		perfview=new PerfTestView(ctx);
		vtest.addView(perfview);
	}
	private void l(final String s){
		new Handler(ctx.getMainLooper()).post(new Runnable(){
				@Override
				public void run()
				{
					t.append(s);
					t.append("\n");
				}
			});
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			if(start)
				new myDialog.Builder(ctx)
					.setMessage("测试正在进行中，真的要停止吗？")
					.setPositiveButton("停止",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							start=false;
						}
					})
					.setNegativeButton("返回",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							w.show();
						}
					})
					.show();
		}
	}
	@Override
	public void onClick(View p1)
	{
		if(!start)
		{
			start=true;
			t.setText("");
			new Thread(this,"性能测试").start();
			b.setText("停止");
			w.setTitle("性能测试(测试中…)");
		}
		else new myDialog.Builder(ctx)
				.setMessage("测试正在进行中，真的要停止吗？")
				.setPositiveButton("停止",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						start=false;
						b.setText("开始测试");
						w.setTitle("性能测试");
					}
				})
				.setNegativeButton("取消",null)
				.show();
	}
	@Override
	public void run()
	{
		try{
			l("正在测试CPU整数运算性能");
			int n=util.random(1000,10000);
			long ns=System.nanoTime();
			for(int i=0;i<10000000&&start;i++){
				long test=n+n;
				test=n-n;
				test=n*n;
				test=n/n;
				test=n%n;
			}
			ns=System.nanoTime()-ns;
			l("CPU整数运算用时:"+ns);
			
			l("正在测试CPU浮点运算性能");
			double m=util.random(1000,10000);
			ns=System.nanoTime();
			for(int i=0;i<10000000&&start;i++){
				double test=m+m;
				test=m-m;
				test=m*m;
				test=m/m;
				test=m%m;
			}
			ns=System.nanoTime()-ns;
			l("CPU浮点运算用时:"+ns);
			
			if(!start)return;
			l("正在测试View绘图性能");
			new Handler(ctx.getMainLooper()).post(new Runnable(){
					@Override
					public void run()
					{
						vtest.show();
						perfview.start();
					}
				});
			while(start&&!perfview.finish())Thread.sleep(1);
			if(!start)return;
			new Handler(ctx.getMainLooper()).post(new Runnable(){
					@Override
					public void run()
					{
						vtest.dismiss();
					}
				});
			int fps=perfview.getAverageFps();
			l("View绘图平均FPS:"+fps+"\n绘制对象总数:"+perfview.sh.pos.size());
			l("体验指数:"+new String[]{"换手机吧","极差","PPT","流畅","优秀"}[fps/12]);
		}catch(Throwable e){
			l("测试出现异常,已经终止本次测试");
			e.printStackTrace();
		}
		finally{
			if(!start)l("测试被终止");
			new Handler(ctx.getMainLooper()).post(new Runnable(){
					@Override
					public void run()
					{
						start=false;
						b.setText("开始测试");
						w.setTitle("性能测试");
					}
				});
		}
	}
}
