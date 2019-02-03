package com.yzrilyzr.connecttogether;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yzrilyzr.connecttogether.data.C;
import com.yzrilyzr.connecttogether.data.Client;
import com.yzrilyzr.connecttogether.data.Data;
import com.yzrilyzr.connecttogether.data.Message;
import com.yzrilyzr.connecttogether.data.User;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myListView;
import com.yzrilyzr.ui.myTabLayout;
import com.yzrilyzr.ui.myViewPager;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class Main implements Client.Rev,Window.OnButtonDown,OnClickListener
{
	Window w;
	Context ctx;
	VecView myhead;
	TextView mynick,mysign;
	Client client;
	View userinfo;
	String contactSearchKey="";
	myListView msglist,contact,cloudfile;
	short rcode;
	Contact contacta;
	MsgList msglista;
	public Main(Client client)
	{
		this.client=client;
		ctx=client.getCtx();
		rcode=client.addRev(this);
		w=new Window(ctx,util.px(230),util.px(420))
			.setTitle("ConnectTogether")
			.setIcon("ct/user")
			.setOnButtonDown(this)
			.show();
		ViewGroup vg=(ViewGroup)w.addView(R.layout.ct_me);
		myTabLayout t=new myTabLayout(ctx);
		myViewPager v=new myViewPager(ctx);
		t.setItems("消息","联系人","云存储");
		t.setViewPager(v);
		v.setTabLayout(t);
		w.addView(t);
		w.addView(v);
		msglist=new myListView(ctx);
		contact=new myListView(ctx);
		cloudfile=new myListView(ctx);
		msglist.setAdapter(msglista=new MsgList());
		msglist.setOnItemClickListener(msglista);
		contact.setAdapter(contacta=new Contact());
		contact.setOnItemClickListener(contacta);
		LinearLayout l=new LinearLayout(ctx);
		l.setOrientation(1);
		myEditText pe=new myEditText(ctx);
		pe.setHint("搜索");
		Action a=new Action();
		pe.setOnEditorActionListener(a);
		pe.addTextChangedListener(a);
		l.addView(pe,new LinearLayout.LayoutParams(-1,-2));
		l.addView(contact,new LinearLayout.LayoutParams(-1,-2));
		v.setPages(msglist,l,cloudfile);
		util.setWeight(v);
		myhead=(VecView) vg.findViewById(R.id.ctmeVecView1);
		mynick=(TextView) vg.findViewById(R.id.ctmemyTextViewRitle1);
		mysign=(TextView) vg.findViewById(R.id.ctmemyTextView1);
		userinfo=(View)myhead.getParent();
		userinfo.setOnClickListener(this);
		mynick.setText(client.getMe().nick);
		mysign.setText(client.getMe().signature);
		for(long id:client.getMe().friends)
		{
			client.send(new User(id).set(C.GETUSER,rcode,client.getMe().id));
		}
		contacta.list();
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==userinfo)
		{
			new UserCard(client,client.getMe().id);
		}
	}
	@Override
	public void rev(byte cmd,DatagramPacket p) throws Throwable
	{
		if(cmd==C.FINDUSER)
		{
			contacta.list.add(0,new User(p.getData()));
			contacta.notifyDataSetChanged();
		}
		else if(cmd==C.GETUSER)
		{
			User u=new User(p.getData());
			Data.users.put(u.id,u);
			contacta.list();
			msglista.notifyDataSetChanged();
			Data.saveUser();
		}
		else if(cmd==C.MESSAGE){
			Message m=new Message(p.getData());
			Data.messages.add(m);
			msglista.list(m);
		}
	}
	@Override
	public void onButtonDown(int p1)
	{
		if(p1==3)
		{
			client.removeRev(this);
			client.destroy();
			client=null;
		}
	}
	class MsgList extends BaseAdapter implements OnItemClickListener
	{
		ArrayList<Message> list=new ArrayList<Message>();
		@Override
		public int getCount()
		{
			return list.size();
		}
		@Override
		public Object getItem(int p1)
		{
			return null;
		}
		@Override
		public long getItemId(int p1)
		{
			return 0;
		}
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			UserViewHolder h=null;
			if(p2==null)
			{
				h=new UserViewHolder();
				p2=h.get(ctx);
			}
			else h=(UserViewHolder)p2.getTag();
			Message me=list.get(p1);
			{
				User u=Data.users.get(me.from);
				if(u==null)client.send(new User(me.from).set(C.GETUSER,rcode,client.getMe().id));
				h.setNick(u==null?""+me.from:u.nick);
				h.setSign(me.msg);
				h.setHeadAsset("ct/user");
			}
			return p2;
		}
		public void list(Message m)
		{
			boolean c=false;
			for(Message v:list)
			if(v.from==m.from&&v.isGroup==m.isGroup){
				c=true;
				v.msg=m.msg;
				v.time=m.time;
				v.type=m.type;
				break;
			}
			if(!c)list.add(0,m);
			notifyDataSetChanged();
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
		{
			long id=list.get(p3).from;
			User u=Data.users.get(id);
			if(u==null)new UserCard(client,id);
			else new Chat(Main.this,u);
		}
	}
	class Contact extends BaseAdapter implements OnItemClickListener
	{
		ArrayList<User> list=new ArrayList<User>();
		@Override
		public int getCount()
		{
			return list.size();
		}
		@Override
		public Object getItem(int p1)
		{
			return null;
		}
		@Override
		public long getItemId(int p1)
		{
			return 0;
		}
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			UserViewHolder h=null;
			if(p2==null)
			{
				h=new UserViewHolder();
				p2=h.get(ctx);
			}
			else h=(UserViewHolder)p2.getTag();
			User me=list.get(p1);
			{
				h.setNick(me.nick);
				h.setSign(me.signature);
				h.setHeadAsset("ct/user");
			}
			return p2;
		}
		public void list()
		{
			list.clear();
			for(User u:Data.users.values())
			{
				if(u.nick.contains(contactSearchKey)||Long.toString(u.id).contains(contactSearchKey))
					list.add(u);
			}
			notifyDataSetChanged();
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
		{
			long id=list.get(p3).id;
			User u=Data.users.get(id);
			if(u==null)new UserCard(client,id);
			else new Chat(Main.this,u);
		}
	}
	class Action implements myEditText.OnEditorActionListener,TextWatcher,Runnable
	{
		Thread thr=null;
		long time=0;
		@Override
		public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
		}
		@Override
		public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			contactSearchKey=p1.toString();
			contacta.list();
			time=System.currentTimeMillis();
			if(thr==null)//&&!"".equals(contactSearchKey))
			{
				thr=new Thread(this);
				thr.start();
			}
		}
		@Override
		public void run()
		{
			try
			{
				while(time+1000>System.currentTimeMillis())
					Thread.sleep(50);
				long id=-1;
				try
				{
					id=Long.parseLong(contactSearchKey);
				}
				catch(Throwable e)
				{}
				User u=new User(id);
				u.nick=contactSearchKey;
				client.send(u.set(C.FINDUSER,rcode,client.getMe().id));
			}
			catch(Exception e)
			{}
			thr=null;
		}
		@Override
		public void afterTextChanged(Editable p1)
		{
		}
		@Override
		public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
		{

			return true;
		}
	}
}
