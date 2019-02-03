package com.yzrilyzr.connecttogether;
import com.yzrilyzr.connecttogether.Server;
import com.yzrilyzr.connecttogether.data.BasePacket;
import com.yzrilyzr.connecttogether.data.C;
import com.yzrilyzr.connecttogether.data.Group;
import com.yzrilyzr.connecttogether.data.Message;
import com.yzrilyzr.connecttogether.data.User;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientProcessor extends UdpProcessor
{
	public ClientProcessor(DatagramPacket p,Server s)
	{
		super(p,s);
	}
	@Override
	public void run()
	{
		try
		{
			BasePacket pk=new BasePacket(getData());
			System.out.println(String.format("@%s cmd:%d id:%d",getPacket().getSocketAddress().toString(),pk.cmd,pk.id));
			send(new BasePacket().buildPacket(C.ACKNOWLEDGE,pk.randomcode,pk.pid,pk.id,getAddress()));
			switch(pk.cmd)
			{
				case C.GETSTAT:
					sendCmd(C.GETSTAT);
					return;
				case C.HEARTBEAT:
					Data.remove.remove(pk.id);
					return;
				case C.LOGIN:
					User u=new User(this);
					User u2=Data.users.get(u.id);
					if(u2==null||!u2.password.equals(u.password))sendCmd(C.LOGINFAILED);
					else
					{
						u.signature=u2.signature;
						u.nick=u2.nick;
						u.registerTime=u2.registerTime;
						u.friends=u2.friends;
						u.groups=u2.groups;
						u.cmd=C.LOGINSUCCESS;
						send(u.buildPacket(u,getAddress()));
						Data.online.put(u2.id,new Address(getAddress()));
					}
					return;
				case C.REGISTER:
					u=new User(this);
					u2=new User(new Random().nextInt(999999999)+100000);
					User u3=new User(u2.id);
					u3.password=u.password;
					u3.nick="新用户";
					u3.ip=getAddress().toString();
					u3.registerTime=System.currentTimeMillis();
					u3.signature="添加个性签名";
					Data.users.put(u3.id,u3);
					Data.saveUser();
					send(u2.buildPacket(u,getAddress()));
					return;
			}
			if(Data.online.get(pk.id)==null||!getAddress().toString().equals(Data.online.get(pk.id).get()+"")){
				sendCmd(C.LOGINFAILED);
				return;
			}
			switch(pk.cmd)
			{
				case C.GETUSER:
					User u=new User(this);
					User u2=Data.users.get(u.id);
					User u3=null;
					if(u2==null)u3=new User(-1);
					else
					{
						u3=new User(u2.id);
						u3.nick=u2.nick;
						u3.signature=u2.signature;
						u3.registerTime=u2.registerTime;
					}
					send(u3.buildPacket(u,getAddress()));
					break;
				case C.ADDFRIEND:
					u=new User(this);
					u2=Data.users.get(pk.id);
					u3=Data.users.get(u.id);
					if(u2==null||u3==null)break;
					if(!u3.friends.contains(u2.id)&&!u2.friends.contains(u3.id))
					{
						CopyOnWriteArrayList<Long> l=Data.addfriend.get(u2.id);
						if(l!=null&&l.contains(u3.id))
						{
							l.remove(u3.id);
							u3.friends.add(u2.id);
							u2.friends.add(u3.id);
							Data.saveUser();
						}
						else{
							if(l==null)Data.addfriend.put(u3.id,l=new CopyOnWriteArrayList<Long>());
							if(!l.contains(u2.id))l.add(u2.id);
						}
					}
					break;
				case C.ADDGROUP:
					Group g=new Group(this);
					Group g2=Data.groups.get(g.id);
					u=Data.users.get(pk.id);
					if(g2==null||u==null)break;
					if(!u.groups.contains(g2.id)&&!g2.users.contains(u.id))
					{
						u.groups.add(g2.id);
						g2.users.add(u.id);
						Data.saveUser();
						Data.saveGroup();
					}
					break;
				case C.DELETEFRIEND:
					u=new User(this);
					u2=Data.users.get(pk.id);
					u3=Data.users.get(u.id);
					if(u2==null||u3==null)break;
					u3.friends.remove(u2.id);
					u2.friends.remove(u3.id);
					Data.saveUser();
					break;
				case C.DELETEGROUP:
					g=new Group(this);
					g2=Data.groups.get(g.id);
					u=Data.users.get(pk.id);
					if(g2==null||u==null)break;
					g2.users.remove(u.id);
					u.groups.remove(g2.id);
					Data.saveUser();
					Data.saveGroup();
					break;
				case C.MESSAGE:
					Message m=new Message(this);
					m.time=System.currentTimeMillis();
					m.randomcode=0;
					if(!m.isGroup)
					{
						Address s=Data.online.get(m.to);
						if(s==null)Data.messages.add(m);
						else send(m.buildPacket(m,s.get()));
					}
					else
					{
						g=Data.groups.get(m.to);
						Data.messages.add(m);
						Address s=Data.online.get(g.ownerid);
						if(s!=null)send(m.buildPacket(m,s.get()));
						for(long i:g.users)
						{
							s=Data.online.get(i);
							if(s!=null)send(m.buildPacket(m,s.get()));
						}
						for(long i:g.admins)
						{
							s=Data.online.get(i);
							if(s!=null)send(m.buildPacket(m,s.get()));
						}
					}
					break;
				case C.GETCHATRECORD:
					u=new User(this);
					Address a=Data.online.get(u.id);
					if(a!=null)
						for(Message t:Data.messages)
						{
							if(t.to==u.id)
							{
								send(t.buildPacket(u,a.get()));
								Data.messages.remove(t);
							}
						}
					break;
				case C.CHANGEUSERINFO:
					u=new User(this);
					u2=Data.users.get(u.id);
					u2.nick=u.nick;
					u2.signature=u.signature;
					send(u.buildPacket(u,getAddress()));
					Data.saveUser();
					break;
				case C.FINDUSER:
					u=new User(this);
					u2=Data.users.get(u.id);
					u3=null;
					if(u2==null)
					{
						for(User u4:Data.users.values())
							if(u4.nick.contains(u.nick))
							{
								u2=u4;
								if(u2!=null)
								{
									u3=new User(u2.id);
									u3.nick=u2.nick;
									u3.signature=u2.signature;
									u3.registerTime=u2.registerTime;
									send(u3.buildPacket(u,getAddress()));
								}
							}
					}
					else
					{
						u3=new User(u2.id);
						u3.nick=u2.nick;
						u3.signature=u2.signature;
						u3.registerTime=u2.registerTime;
						send(u3.buildPacket(u,getAddress()));
					}
					break;
				default:
					System.out.println("cmd:"+pk.cmd);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
