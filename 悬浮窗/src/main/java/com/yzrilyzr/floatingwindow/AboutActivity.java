package com.yzrilyzr.floatingwindow;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.myTabLayout;
import com.yzrilyzr.ui.myTextViewTitle;
import com.yzrilyzr.ui.myViewPager;

public class AboutActivity extends myActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String s=getString(R.string.about2);
		myTabLayout tb=(myTabLayout)findViewById(R.id.activitymainmyTabLayout1);
		tb.setTouchable(false);
		int i=0;
		final String[] t=new String[]{
		s.substring(i,i=s.indexOf("1.")),
		s.substring(i,i=s.indexOf("2.",i)),
		s.substring(i,i=s.indexOf("3.",i)),
		s.substring(i,i=s.indexOf("4.",i)),
		s.substring(i,i=s.indexOf("5.",i)),
		s.substring(i,i=s.indexOf("6.",i)),
		s.substring(i,i=s.indexOf("7.",i)),
		s.substring(i)
		};
		ScrollView[] ps=new ScrollView[t.length];
		final myButton[] bs=new myButton[t.length];
		final myViewPager p=(myViewPager) findViewById(R.id.activitymainmyViewPager1);
		p.setTouchable(false);
		tb.setViewPager(p);
		p.setTabLayout(tb);
		i=0;
		OnClickListener ocl=new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				int c=p.getCurrentItem();
				if(++c==t.length)
				{
					finish();
					util.getSPWrite("abouta").putBoolean("abouta",true).commit();
					startActivity(new Intent(ctx,MainActivity.class));
				}
				else
				{
					p.setCurrentItem(c,true);
					new A(bs[c]);
				}
			}
		};
		for(String d:t)
		{
			ScrollView sv=new Scroll(this);
			LinearLayout lo=new LinearLayout(this);
			lo.setOrientation(1);
			myTextViewTitle v=new myTextViewTitle(this);
			v.setText(d);
			myButton b=new myButton(ctx);
			b.setOnClickListener(ocl);
			b.setText("我知道了");
			bs[i]=b;
			ps[i++]=sv;
			lo.addView(v);
			lo.addView(b);
			sv.addView(lo);
		}
		p.setPages(ps);
		new A(bs[0]);

	}
	class A extends Thread
	{
		myButton b;
		public A(myButton b)
		{
			this.b=b;
			b.setEnabled(false);
			start();
		}
		@Override
		public void run()
		{
			super.run();
			try
			{
				Thread.sleep(5000);
				runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						b.setEnabled(true);
					}
				});
			}
			catch (InterruptedException e)
			{}
		}
	}
	public class Scroll extends ScrollView
	{
		public Scroll(Context c)
		{
			super(c);
		}

		@Override
		public void fling(int velocityY)
		{
			// TODO: Implement this method
			super.fling(velocityY/6);
		}
	}
}
