package com.yzrilyzr.connecttogether;

import com.yzrilyzr.connecttogether.Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public abstract class UdpProcessor implements Runnable
{
	DatagramPacket packet;
	Server server;
	InetSocketAddress address;
	public UdpProcessor(DatagramPacket packet, Server server)
	{
		this.packet = packet;
		this.server = server;
		this.address=(InetSocketAddress)packet.getSocketAddress();
	}
	public InetSocketAddress getAddress()
	{
		return address;
	}
	public DatagramPacket getPacket()
	{
		return packet;
	}
	public Server getServer()
	{
		return server;
	}
	public byte[] getData(){
		return packet.getData();
	}
	public void send(DatagramPacket p) throws IOException{
		((DatagramSocket)getServer().server.server).send(p);
		System.out.println("cmd:"+p.getData()[0]+",sendto:"+p.getSocketAddress());
	}
	public void sendCmd(byte cmd) throws IOException{
		send(new DatagramPacket(new byte[]{cmd,0,0,0,0,0,0,0,0,0,0,0,0},13,getPacket().getSocketAddress()));
	}
}
