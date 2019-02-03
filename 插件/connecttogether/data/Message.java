package com.yzrilyzr.connecttogether.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class Message extends BasePacket
{
	public long time=0,from=0,to=0;
	public String msg="";
	public boolean isGroup=false;
	public byte type=0;
	public static final byte
	MSG=0,
	INF=1,
	SYS=2,
	FILE=3,
	PIC=4,
	VOICE=5;
	public Message(byte[] b)throws IOException
	{
		super(b);
		from=in.readLong();
		to=in.readLong();
		time=in.readLong();
		isGroup=in.readBoolean();
		type=in.readByte();
		msg=in.readUTF();
	}
	public Message(DatagramPacket p) throws IOException{
		this(p.getData());
	}
	public Message(long from,long to,String msg)
	{
		super();
		this.from=from;
		this.to=to;
		this.msg=msg;
	}
	@Override
	public void onbuildPacket(DataOutputStream os)throws IOException
	{
		os.writeLong(from);
		os.writeLong(to);
		os.writeLong(time);
		os.writeBoolean(isGroup);
		os.writeByte(type);
		os.writeUTF(msg);
	}
}
