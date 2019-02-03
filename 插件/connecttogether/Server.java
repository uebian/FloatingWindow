package com.yzrilyzr.connecttogether;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.yzrilyzr.connecttogether.data.C;

public class Server
{
	private ExecutorService pool;
	public ServerThread server,heartbeat;
	public static void main(String[] args)
	{
		System.out.println("running");
		Server s=new Server();
		s.startServer();
	}
	public Server()
	{
		try
		{
			Data.loadGroup();
			Data.loadUser();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		pool=Executors.newCachedThreadPool();
		server=new ServerThread("Server"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				try
				{
					server=new DatagramSocket(10000);
					System.out.println("started");
					//toast(new ConsoleMsg(TAG,"主线程","远程控制服务器已启动","local"));
					while(runn)
					{
						byte[] bb=new byte[1024];
						DatagramPacket pa=new DatagramPacket(bb,bb.length);
						((DatagramSocket)server).receive(pa);
						pool.execute(new ClientProcessor(pa,Server.this));
					}
				}
				catch(SocketException e)
				{
					//toast(new ConsoleMsg(TAG,"主线程","远程控制服务器已关闭","local"));
				}
				catch (IOException e)
				{
					//toast(new ConsoleMsg("Error","主线程","无法启动远程控制服务器:"+e,"local"));
				}
			}
		};
		heartbeat=new ServerThread("ServerHeartBeat"){
			@Override
			public void run()
			{
				// TODO: Implement this method
				while(runn)
				{
					synchronized(Data.remove)
					{
						DatagramSocket s=(DatagramSocket)Server.this.server.server;
						for(long id:Data.online.keySet())
						{
							Data.remove.add(id);
							try
							{
								s.send(new DatagramPacket(new byte[]{C.HEARTBEAT,0,0,0,0,0,0,0,0,0,0,0,0},13,Data.online.get(id).get()));
							}
							catch (IOException e)
							{}
						}
					}
					try
					{
						Thread.sleep(5*60*1000);
					}
					catch (InterruptedException e)
					{}
					synchronized(Data.remove){
						for(long id:Data.remove){
							Data.online.remove(id);
							Data.remove.remove(id);
							System.out.println("hbt removed:"+id);
						}
					}
				}
			}
		};
	}
	public void startServer()
	{
		server.start();
		heartbeat.start();
	}
	abstract class ServerThread implements Runnable
	{
		protected boolean runn=false;
		protected Closeable server=null;
		protected Thread th;
		protected String n;
		public ServerThread(String n)
		{
			this.n=n;
		}
		public void stopServer() throws IOException
		{
			if(!runn)return;
			runn=false;
			if(server!=null)server.close();
			server=null;
		}
		public void start()
		{
			if(runn)return;
			runn=true;
			th=new Thread(this,n);
			th.start();
		}
		public abstract void run();
	}
}
