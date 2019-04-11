package com.yzrilyzr.floatingwindow.apps;
import com.yzrilyzr.ui.*;
import java.io.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.PluginContext;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.longtexteditor.LongTextView;
import com.yzrilyzr.myclass.JSEnv;
import com.yzrilyzr.myclass.util;
import java.util.concurrent.CopyOnWriteArrayList;
import android.os.Handler;

public class Console extends PluginContext implements Window.OnButtonDown,OnCheckedChangeListener,myTextView.OnEditorActionListener,myViewPager.OnPageChangeListener,OnClickListener
{
	public static final OS out=new OS();
	public static final OS err=new OS();
	LongTextView n,m,b;
	OS.OnWrite oa,ob;
	myEditText edit;
	myLoadingView ld;
	String cmd;
	boolean su=false;
	LinearLayout l;
	JSEnv env=null;
	private VecView op;
	private VecView op2;
	private Context ctx;
	private Window w;
	public PluginContext plctx;
	public Console(Context c,Intent e) throws Exception
	{
		super(c,c.getPackageName(),c.getPackageCodePath());
		setIntent(e);
		ctx=c;
		w=new Window(c,util.px(300),util.px(360))
			.setTitle("控制台")
			.setIcon("console")
			.setOnButtonDown(this)
			.show();
		myTabLayout t=new myTabLayout(c);
		myViewPager p=new myViewPager(c);
		t.setViewPager(p);
		p.setTabLayout(t);
		p.setOnPageChangedListener(this);
		w.addView(t);
		w.addView(p);
		myCheckBox cb=new myCheckBox(c);
		op=new VecView(c);
		op.setImageVec("open");
		op2=new VecView(c);
		op2.setImageVec("send");
		op.setOnClickListener(this);
		op2.setOnClickListener(this);
		op.setVisibility(8);
		op2.setVisibility(8);
		cb.setOnCheckedChangeListener(this);
		myTextView sut=new myTextView(c);
		sut.setText("SU");
		l=new LinearLayout(c);
		l.addView(edit=new myEditText(c));
		l.addView(ld=new myLoadingView(c),new LinearLayout.LayoutParams(util.px(30),util.px(30)));
		l.addView(cb);
		l.addView(sut);
		l.addView(op,new LinearLayout.LayoutParams(util.px(30),util.px(30)));
		l.addView(op2,new LinearLayout.LayoutParams(util.px(30),util.px(30)));
		l.setGravity(Gravity.CENTER);
		ld.setVisibility(8);
		w.addView(l);
		util.setWeight(edit);
		util.setWeight(p);
		edit.setHint("指令");
		edit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		edit.setOnEditorActionListener(this);
		t.setItems("OUT","ERR","JS LOG");
		n=new LongTextView(c);
		m=new LongTextView(c);
		b=new LongTextView(c);
		p.setPages(n,m,b);
		n.setText(out.os.toString());
		m.setText(err.os.toString());
		n.gotoend();
		m.gotoend();
		out.o.add(oa=new OS.OnWrite(){
					  @Override
					  public void onWrite(byte[] b, int o, int l)
					  {
						  n.setText(out.os.toString());
						  n.gotoend();
					  }
				  });
		err.o.add(ob=new OS.OnWrite(){
					  @Override
					  public void onWrite(byte[] b, int o, int l)
					  {
						  m.setText(err.os.toString());
						  m.gotoend();
					  }
				  });
		String js=e.getStringExtra("jstext");
		if(js!=null)
		{
			p.setCurrentItem(2,false);
			initjs(js);
		}
		else
		{
			initjs("");
			print("#打开一个JS文件以执行");
		}
	}
	
	public void print(Object o)
	{
		b.addText(o+"");
		b.gotoend();
	}
	private void initjs(String js)
	{
		b.clear();
		if(js.contains("com.mojang.minecraftpe.MainActivity"))
			ctx.startActivity(new Intent(ctx,com.mojang.minecraftpe.MainActivity.class)
							  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		env=new JSEnv(js,this);
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==op)
		{
			API.startServiceForResult(ctx,w,new BroadcastReceiver(){
					@Override
					public void onReceive(Context c,Intent e)
					{
						try
						{
							String file=e.getStringExtra("path");
							initjs(util.readwithN(file));
						}
						catch(Exception ex)
						{
							print("打开失败");
						}
					}
				},cls.EXPLORER);
		}
		else if(p1==op2)
		{
			String cmd=edit.getText()+"";
			edit.setText("");
			try
			{
				env.function("_callMethod",cmd);
			}
			catch(Throwable e)
			{
				print("执行出错:"+util.getStackTrace(e));
			}
		}
	}
	@Override
	public void onPageChanged(int last, int newone)
	{
		if(newone<2)
		{
			edit.setHint("指令");
			l.getChildAt(2).setVisibility(0);
			l.getChildAt(3).setVisibility(0);
			l.getChildAt(4).setVisibility(8);
			l.getChildAt(5).setVisibility(8);
			edit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		}
		else
		{
			edit.setHint("JS语句");
			l.getChildAt(2).setVisibility(8);
			l.getChildAt(3).setVisibility(8);
			l.getChildAt(4).setVisibility(0);
			l.getChildAt(5).setVisibility(0);
			edit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		}
		w.setAddButton(newone<2?null:"sugar");
	}
	@Override
	public void onCheckedChanged(CompoundButton p1, boolean p2)
	{
		su=p2;
	}
	@Override
	public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
	{
		if(p2==6&&op.getVisibility()==8)
		{
			cmd=edit.getText()+"";
			edit.setText("");
			new Task().execute();
		}
		return false;
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			out.o.remove(oa);
			err.o.remove(ob);
			if(com.mojang.minecraftpe.MainActivity.currentMainActivity!=null)com.mojang.minecraftpe.MainActivity.currentMainActivity.get().finish();
			env.function("_callMethod","close()");
		}
		else if(code==Window.ButtonCode.ADD)
		{
			myImageView i=new myImageView(ctx);
			i.setImageResource(R.drawable.sugar);
			new myDialog.Builder(ctx)
				.setTitle("JavaScript语法糖")
				.setView(i)
				.setPositiveButton("了解！",null)
				.show();
		}
	}
	protected static final class OS extends OutputStream
	{
		protected final ByteArrayOutputStream os=new ByteArrayOutputStream();
		protected CopyOnWriteArrayList<OnWrite> o=new CopyOnWriteArrayList<OnWrite>();
		public void write(int p1) throws IOException
		{
			os.write(p1);
			for(OnWrite g:o)g.onWrite(new byte[]{(byte)p1},0,1);
		}
		@Override
		public void write(byte[] buffer) throws IOException
		{
			os.write(buffer);
			for(OnWrite g:o)g.onWrite(buffer,0,buffer.length);
		}
		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException
		{
			os.write(buffer, offset, count);
			for(OnWrite g:o)g.onWrite(buffer,offset,count);
		}
		@Override
		public void close() throws IOException
		{
			throw new IOException("无法关闭");
		}
		public interface OnWrite
		{
			public double y=Math.random();
			public void onWrite(byte[] b,int o,int l);
		}
	}
	class Task extends AsyncTask
	{
		@Override
		protected void onPreExecute()
		{
			edit.setEnabled(false);
			ld.setVisibility(0);
		}
		@Override
		protected Object doInBackground(Object[] p1)
		{
			try
			{
				Process p=Runtime.getRuntime().exec(su?"su":cmd);
				if(su)
				{
					DataOutputStream os = new DataOutputStream(p.getOutputStream());
					os.writeBytes(cmd+"\n");
					os.flush();
					os.close();
				}
				BufferedReader o=new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader e=new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String b=null;
				while((b=o.readLine())!=null)System.out.println(b);
				while((b=e.readLine())!=null)System.out.println(b);
				p.waitFor();
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				util.toast("执行出错");
			}
			return null;
		}
		@Override
		protected void onPostExecute(Object result)
		{
			edit.setEnabled(true);
			ld.setVisibility(8);
		}
	}
}
