package com.yzrilyzr.connecttogether;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.yzrilyzr.connecttogether.data.C;
import com.yzrilyzr.connecttogether.data.Client;
import com.yzrilyzr.connecttogether.data.User;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myEditText;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import com.yzrilyzr.connecttogether.data.Message;

public class UserCard implements Client.Rev,Window.OnButtonDown,OnClickListener
{
	Window w;
	Context ctx;
	VecView myhead;
	TextView mynick,mysign;
	Client client;
	short rcode;
	Button addf;
	long id;
	public UserCard(Client client,long id)
	{
		ctx=client.getCtx();
		this.id=id;
		this.client=client;
		if(id!=-1)
		{
			w=new Window(ctx,util.px(280),util.px(320))
				.setTitle("Card")
				.setIcon("ct/user")
				.setOnButtonDown(this)
				.show();
			ViewGroup vg=(ViewGroup)w.addView(R.layout.ct_usercard);
			myhead=(VecView) vg.findViewById(R.id.ctusercardVecView1);
			mynick=(TextView) vg.findViewById(R.id.ctusercardmyTextViewTitle1);
			mysign=(TextView) vg.findViewById(R.id.ctusercardmyTextView1);
			((TextView) vg.findViewById(R.id.ctusercardmyTextView)).setText(id+"");
			addf=(Button) vg.findViewById(R.id.ctusercardmyButton1);
			addf.setOnClickListener(this);
			rcode=client.addRev(this);
			client.send(new User(id).set(C.GETUSER,rcode,client.getMe().id));
		}
	}
	@Override
	public void rev(byte cmd,DatagramPacket p) throws Throwable
	{
		if(cmd==C.GETUSER||cmd==C.CHANGEUSERINFO)
		{
			User me=new User(p.getData());
			mynick.setText(me.nick);
			w.setTitle(me.nick);
			mysign.setText(me.signature);
			if(me.id==client.getMe().id)
			{
				mynick.setOnClickListener(this);
				mysign.setOnClickListener(this);
				myhead.setOnClickListener(this);
			}
		}
	}
	@Override
	public void onClick(final View p1)
	{
		if(p1==mysign||p1==mynick)
		{
			final myEditText e=new myEditText(ctx);
			if(p1==mynick)
			{
				e.setText(mynick.getText());
				e.setHint("昵称(3～15字)");
			}
			else if(p1==mysign)
			{
				e.setText(mysign.getText());
				e.setHint("个性签名(最多100字)");
			}
			new myDialog.Builder(ctx)
				.setTitle("设置")
				.setView(e)
				.setPositiveButton("保存",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface v,int i)
					{
						String s=e.getText().toString();
						if(p1==mynick)
						{
							if(s.length()<3)
							{
								util.toast("昵称太短～");
								return;
							}
							if(s.length()>15)s=s.substring(0,15);
							client.getMe().nick=s;
						}
						else if(p1==mysign)
						{
							if(s.length()>100)s=s.substring(0,100);
							client.getMe().signature=e.getText().toString();
						}
						client.send(client.getMe().set(C.CHANGEUSERINFO,rcode,client.getMe().id));

					}
				})
				.setNegativeButton("取消",null)
				.show();
			e.getLayoutParams().width=-1;
		}
		else if(p1==addf)
		{
			try
			{
				Message u=new Message(client.getMe().id,id,"请求添加好友");
				u.type=u.INF;
				User s=new User(id);
				client.send(u.set(C.MESSAGE,0,client.getMe().id));
				client.send(s.set(C.ADDFRIEND,0,client.getMe().id));
			}
			catch (Exception e)
			{
				util.toast("发送请求失败");
			}
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
