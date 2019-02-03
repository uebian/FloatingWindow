package com.yzrilyzr.connecttogether.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Group extends BasePacket
{
	public long id=0,ownerid=0;
	public String nick="",signature="";
	public long registerTime=0;
	public CopyOnWriteArrayList<Long> users=new CopyOnWriteArrayList<Long>();
	public CopyOnWriteArrayList<Long> admins=new CopyOnWriteArrayList<Long>();
	public Group(byte[] b)throws IOException
	{
		super(b);
		id=in.readLong();
		ownerid=in.readLong();
		nick=in.readUTF();
		signature=in.readUTF();
		registerTime=in.readLong();
		int sa=in.readInt(),sb=in.readInt();
		for(int i=0;i<sa;i++)users.add(in.readLong());
		for(int i=0;i<sb;i++)admins.add(in.readLong());
	}
	public Group(DatagramPacket p) throws IOException{
		this(p.getData());
	}
	public Group(long id)
	{
		super();
		this.id=id;
	}
	@Override
	public void onbuildPacket(DataOutputStream os)throws IOException
	{
		os.writeLong(id);
		os.writeLong(ownerid);
		os.writeUTF(nick);
		os.writeUTF(signature);
		os.writeLong(registerTime);
		os.writeInt(users.size());
		os.writeInt(admins.size());
		for(long a:users)os.writeLong(a);
		for(long a:admins)os.writeLong(a);
	}
}
