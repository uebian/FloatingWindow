package com.yzrilyzr.connecttogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.ui.myTextView;
import android.view.Gravity;
import com.yzrilyzr.ui.myRippleDrawable;

public class MsgViewHolder
{
	VecView heada,headb,state;
	myTextView nick,msg;
	public ViewGroup get(Context c)
	{
		ViewGroup vg=(ViewGroup)LayoutInflater.from(c).inflate(R.layout.ct_msg_entry,null),vg2=null;
		heada=(VecView) vg.getChildAt(0);
		headb=(VecView)vg.getChildAt(2);
		vg2=(ViewGroup) vg.getChildAt(1);
		nick=(myTextView) vg2.getChildAt(0);
		vg2=(ViewGroup) vg2.getChildAt(1);
		state=(VecView) vg2.getChildAt(0);
		msg=(myTextView) vg2.getChildAt(1);
		vg.setTag(this);
		msg.setBackgroundDrawable(new myRippleDrawable());
		return vg;
	}
	public void setHeada(VecView heada)
	{
		this.heada = heada;
	}
	public void setHeadb(VecView headb)
	{
		this.headb = headb;
	}
	public void setState(boolean success)
	{
		this.state.setVisibility(success?4:0);
	}
	public void setNick(String nick,boolean me)
	{
		this.nick.setText(nick);
		this.nick.setGravity(me?Gravity.RIGHT:Gravity.LEFT);
		this.msg.setGravity(me?Gravity.RIGHT:Gravity.LEFT);
	}
	public void setMsg(String msg)
	{
		this.msg.setText(msg);
	}
}
