package com.yzrilyzr.connecttogether;
import java.net.InetSocketAddress;

public class Address
{
	public int port;
	public String ip;
	public Address(int port, String ip)
	{
		this.port = port;
		this.ip = ip;
	}
	public Address(InetSocketAddress l){
		port=l.getPort();
		ip=l.getAddress().getHostAddress();
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public int getPort()
	{
		return port;
	}
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	public String getIp()
	{
		return ip;
	}
	public InetSocketAddress get(){
		return new InetSocketAddress(ip,port);
	}
}
