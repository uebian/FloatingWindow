package com.yzrilyzr.connecttogether;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import com.yzrilyzr.connecttogether.data.Client;
import com.yzrilyzr.connecttogether.data.Message;
import com.yzrilyzr.connecttogether.data.User;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import java.net.DatagramPacket;
import com.yzrilyzr.connecttogether.data.C;
import android.widget.BaseAdapter;
import com.yzrilyzr.icondesigner.VecView;
import android.widget.LinearLayout;

public class Chat implements Client.Rev,Window.OnButtonDown,OnClickListener
{
	Context ctx;
	Window w;
	LinearLayout list;
	EditText edit;
	Client client;
	VecView send;
	User to;
	public Chat(Main n,User u)
	{
		ctx=n.ctx;
		to=u;
		client=n.client;
		client.addRev(this);
		w=new Window(ctx,util.px(220),util.px(220))
			.setTitle(u.nick)
			.setOnButtonDown(this)
			.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.ct_chat);
		list=(LinearLayout) ((ScrollView)((ViewGroup) vg.getChildAt(0)).getChildAt(0)).getChildAt(0);
		vg=(ViewGroup) vg.getChildAt(1);
		edit=(EditText) vg.getChildAt(0);
		send=(VecView) vg.getChildAt(1);
		send.setOnClickListener(this);
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==send)
		{
			if(edit.getText().toString().equals(""))return;
			Message u=new Message(client.getMe().id,to.id,edit.getText().toString());
			u.type=u.MSG;
			final MsgViewHolder v=new MsgViewHolder();
			ViewGroup vg=v.get(ctx);
			v.setNick(client.getMe().nick,true);
			v.setMsg(u.msg);
			list.addView(vg);
			client.send(u.set(C.MESSAGE,(short)0,client.getMe().id),new Client.ACK(){
					@Override
					public void ack(boolean success)
					{
						v.setState(success);
					}
				});
			edit.setText("");
			((ScrollView)list.getParent()).smoothScrollTo(0,list.getChildAt(0).getHeight());
		}
	}
	@Override
	public void rev(byte cmd,DatagramPacket p) throws Throwable
	{
		if(cmd==C.MESSAGE)
		{
			Message m=new Message(p.getData());
			if(m.from!=to.id||m.isGroup)return;
			MsgViewHolder v=new MsgViewHolder();
			ViewGroup vg=v.get(ctx);
			v.setNick(to.nick,false);
			v.setMsg(m.msg);
			list.addView(vg);
			ScrollView l=(ScrollView)list.getParent();
			l.smoothScrollBy(0,util.px(640));
		}
	}
	@Override
	public void onButtonDown(int p1)
	{
		if(p1==3)
		{
			client.removeRev(this);
		}
	}
}
