package com.yzrilyzr.connecttogether.data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.net.InetSocketAddress;
import android.content.Context;
import com.yzrilyzr.myclass.util;
import android.os.Handler;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class Client extends Thread
{
	private DatagramSocket socket;
	private boolean run=false;
	private ConcurrentHashMap<Short,Thread> sendThread;
	private ConcurrentHashMap<Short,Rev> revs;
	private Context ctx;
	private User me;
	public InetSocketAddress serveraddr=new InetSocketAddress("192.168.0.2",10000);
	public Client(Context ctx) throws SocketException
	{
		this.ctx=ctx;
		Data.DIR=ctx.getDir("ConnectTogether",ctx.MODE_PRIVATE).getAbsolutePath();
		try
		{
			Data.loadGroup();
			Data.loadUser();
		}
		catch (Exception e)
		{
			
		}
		revs=new ConcurrentHashMap<Short,Rev>();
		sendThread=new ConcurrentHashMap<Short,Thread>();
		socket=new DatagramSocket();
	}
	public Context getCtx()
	{
		return ctx;
	}
	public void setMe(User me)
	{
		this.me = me;
		Data.users.put(me.id,me);
	}
	public User getMe()
	{
		return me;
	}
	public void connect() throws Exception
	{
		if(!run)start();
		sendCmd(C.GETSTAT);
	}
	public void send(final BasePacket p,final ACK ack)
	{
		new Thread(){
			@Override
			public void run()
			{
				boolean bo=false;
				try
				{
					p.pid=(short)util.random(-32768,32767);
					sendThread.put(p.pid,this);
					socket.send(p.buildPacket(p,serveraddr));
					sleep(5000);
				}
				catch (IOException e)
				{
				}
				catch(InterruptedException e)
				{
					bo=true;
				}
				final boolean k=bo;
				new Handler(ctx.getMainLooper()).post(new Runnable(){
						@Override
						public void run()
						{
							if(ack!=null)ack.ack(k);
						}
				});
			}
		}.start();
	}
	public InetSocketAddress getServerAddr()
	{
		return serveraddr;
	}
	public void send(BasePacket p)
	{
		send(p,null);
	}
	public void sendCmd(byte cmd)
	{
		BasePacket b=new BasePacket();
		b.cmd=cmd;
		send(b);
	}
	@Override
	public void run()
	{
		Handler h=new Handler(ctx.getMainLooper());
		run=true;
		while(run)
			try
			{
				final DatagramPacket p=new DatagramPacket(new byte[1024],1024);
				socket.receive(p);
				h.post(new Runnable(){
						@Override
						public void run()
						{
							try
							{
								BasePacket k=new BasePacket(p.getData());
								if(k.cmd==C.HEARTBEAT)sendCmd(C.HEARTBEAT);
								else if(k.cmd==C.ACKNOWLEDGE)
								{
									sendThread.get(k.pid).interrupt();
									sendThread.remove(k.pid);
								}
								else
								{
									if(k.randomcode!=0)
									{
										Rev r=revs.get(k.randomcode);
										if(r!=null)r.rev(k.cmd,p);
									}
									else
									{
										for(Rev rv:revs.values())rv.rev(k.cmd,p);
									}
								}
							}
							catch (Throwable e)
							{
								e.printStackTrace();
							}
						}
					});
			}
			catch(Throwable e)
			{

			}
	}
	public void destroy()
	{
		run=false;
		socket.close();
	}
	public short addRev(Rev r)
	{
		short i=(short)util.random(-32768,32767);
		revs.put(i,r);
		return i;
	}
	public void removeRev(Rev r)
	{
		Iterator<Map.Entry<Short,Rev>> vs=revs.entrySet().iterator();
		while(vs.hasNext())
		{
			Map.Entry<Short,Rev> e=vs.next();
			if(e.getValue()==r)
			{
				revs.remove(e.getKey());
				break;
			}
		}
	}
	public interface Rev
	{
		public abstract void rev(byte cmd,DatagramPacket p)throws Throwable;
	}
	public interface ACK
	{
		public abstract void ack(boolean success);
	}
}
