package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.mySwitch;
import android.widget.CompoundButton;
import android.view.View;
import com.yzrilyzr.icondesigner.VECfile;
import java.io.IOException;
import com.yzrilyzr.icondesigner.Shape;
import com.yzrilyzr.floatingwindow.API;
import android.content.BroadcastReceiver;

public class FrontSight implements Window.OnButtonDown,OnClickListener,OnCheckedChangeListener
{
	private Window w,w2;
	private myButton b1,b4,b5;
	private myButton b2,b3;
	private int x=0,y=0,color=0xffff5555;
	private VecView v;
	private Context ctx;
	public FrontSight(Context c,Intent e)
	{
		ctx=c;
		w2=new Window(c,util.px(150),util.px(300))
		.setTitle("准星")
		.setIcon("frontsight")
		.setBar(0,8,0)
		.setCanFocus(false)
		.setOnButtonDown(this)
		.show()
		.setMinWin(true);
		w=new Window(c,-1,-1);
		w.setCanResize(false)
		.setTitle("准星")
		.setCanFocus(false)
		.setIcon("frontsight");
		w.getLayoutParams().flags=WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		w.show();
		LinearLayout a =(LinearLayout)w.getMainView();
        a.setBackgroundDrawable(null);
        a.removeAllViews();
		v=new VecView(c);
		a.addView(v);
		a.setGravity(Gravity.CENTER);
		x=util.getSPRead("frontsight").getInt("frontsightx",0);
		y=util.getSPRead("frontsight").getInt("frontsighty",0);
		color=util.getSPRead("frontsight").getInt("frontsightcolor",color);
		setColor();
		LinearLayout.LayoutParams p=(LinearLayout.LayoutParams) v.getLayoutParams();
		p.width=util.px(30);
		p.height=util.px(30);
		setm();
		mySwitch sw=new mySwitch(c);
		w2.addView(sw);
		sw.setChecked(true);
		sw.setOnCheckedChangeListener(this);
		sw.getLayoutParams().width=-2;
		((LinearLayout)w2.getMainView()).setGravity(Gravity.CENTER);
		b1=new myButton(c);
		b1.setText("↑");
		b1.setOnClickListener(this);
		w2.addView(b1);
		b2=new myButton(c);
		b2.setText("↓");
		b2.setOnClickListener(this);
		w2.addView(b2);
		b3=new myButton(c);
		b3.setText("←");
		b3.setOnClickListener(this);
		w2.addView(b3);
		b4=new myButton(c);
		b4.setText("→");
		b4.setOnClickListener(this);
		w2.addView(b4);
		b5=new myButton(c);
		b5.setText("设置颜色");
		b5.setOnClickListener(this);
		w2.addView(b5);
	}

	@Override
	public void onCheckedChanged(CompoundButton p1, boolean p2)
	{
		if(p2)w.show();
		else w.dismiss();
	}
	private void setColor()	
	{
		try
		{
			VECfile f=VECfile.readFileFromIs(ctx.getAssets().open("frontsighticon.vec"));
			for(Shape s:f.shapes)s.setStrokeColor(color);
			v.setImageVecFile(f);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)w.dismiss();
	}
	private void setm()
	{
		LinearLayout.LayoutParams p=(LinearLayout.LayoutParams) v.getLayoutParams();
		p.setMargins(util.px(x),util.px(y),0,0);
		//if(y<0)p.setMargins(0,0,0,util.px(-y));
		
		v.setLayoutParams(p);
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==b1)y--;
		else if(p1==b2)y++;
		else if(p1==b3)x--;
		else if(p1==b4)x++;
		else if(p1==b5){
			API.startServiceForResult(ctx,new Intent().putExtra("color",color),w2,new BroadcastReceiver(){
				@Override
				public void onReceive(Context p1, Intent p2)
				{
					color=p2.getIntExtra("color",0);
					util.getSPWrite("frontsight").putInt("frontsightcolor",color).commit();
					setColor();
				}
			},cls.COLORPICKER);
		}
		setm();
		if(p1==b1||p1==b2||p1==b3||p1==b4)
			util.getSPWrite("frontsight")
			.putInt("frontsightx",x)
			.putInt("frontsighty",y)
			.commit();
	}
}
