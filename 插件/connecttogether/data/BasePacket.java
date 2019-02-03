package com.yzrilyzr.connecttogether.data;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.Inet4Address;
import com.yzrilyzr.myclass.util;

public class BasePacket
{
	protected DataInputStream in;
	public short randomcode=0;
	public byte cmd=0;
	public long id=0;
	public short pid=0;
	public BasePacket(byte[] data) throws IOException
	{
		if(data!=null)
		{
			in=new DataInputStream(new ByteArrayInputStream(data));
			cmd=in.readByte();
			randomcode=in.readShort();
			pid=in.readShort();
			id=in.readLong();
		}
	}
	public BasePacket()
	{}
	public DatagramPacket buildPacket(BasePacket src,InetSocketAddress addr) throws IOException
	{
		return buildPacket(src.cmd,src.randomcode,src.pid,src.id,addr);
	}
	public BasePacket set(byte cmd,int rcode,int pid,long id) throws IOException
	{
		this.cmd=cmd;
		this.randomcode=(short) rcode;
		this.pid=(short) pid;
		this.id=id;
		return this;
	}
	public DatagramPacket buildPacket(byte cmd,int rcode,int pid,long id,InetSocketAddress addr) throws IOException
	{
		ByteArrayOutputStream o=new ByteArrayOutputStream();
		DataOutputStream d=new DataOutputStream(o);
		d.writeByte(cmd);
		d.writeShort(rcode);
		d.writeShort(pid);
		d.writeLong(id);
		onbuildPacket(d);
		d.flush();
		d.close();
		byte[] b=o.toByteArray();
		return new DatagramPacket(b,b.length,addr);
	}
	public void onbuildPacket(DataOutputStream os)throws IOException
	{};
}
