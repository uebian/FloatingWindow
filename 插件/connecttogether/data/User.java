package com.yzrilyzr.connecttogether.data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.net.DatagramPacket;

public class User extends BasePacket
{
	public long id=0;
	public String password="",nick="",signature="",ip="";
	public long registerTime=0;
	public CopyOnWriteArrayList<Long> friends=new CopyOnWriteArrayList<Long>();
	public CopyOnWriteArrayList<Long> groups=new CopyOnWriteArrayList<Long>();
	public User(byte[] b)throws IOException
	{
		super(b);
		id=in.readLong();
		super.id=id;
		password=in.readUTF();
		nick=in.readUTF();
		signature=in.readUTF();
		ip=in.readUTF();
		registerTime=in.readLong();
		int sa=in.readInt(),sb=in.readInt();
		for(int i=0;i<sa;i++)friends.add(in.readLong());
		for(int i=0;i<sb;i++)groups.add(in.readLong());
	}
	public User(DatagramPacket p) throws IOException{
		this(p.getData());
	}
	public User(long id)
	{
		super();
		this.id=id;
		super.id=id;
	}
	@Override
	public void onbuildPacket(DataOutputStream os)throws IOException
	{
		os.writeLong(id);
		os.writeUTF(password);
		os.writeUTF(nick);
		os.writeUTF(signature);
		os.writeUTF(ip);
		os.writeLong(registerTime);
		os.writeInt(friends.size());
		os.writeInt(groups.size());
		for(long a:friends)os.writeLong(a);
		for(long a:groups)os.writeLong(a);
	}
}
