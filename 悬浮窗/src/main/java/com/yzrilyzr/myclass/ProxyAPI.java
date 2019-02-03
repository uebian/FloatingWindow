package com.yzrilyzr.myclass;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
public class ProxyAPI
{
	protected Object obj;
	protected Class cls;
	public ProxyAPI(String clazz,Class[] partype,Object... par)
	{
		try
		{
			cls=Class.forName(clazz);
			if(partype==null)obj=cls.newInstance();
			else obj=cls.getConstructor(partype).newInstance(par);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	public ProxyAPI(String clazz)
	{
		this(clazz,null,null);
	}
	public ProxyAPI(String clazz,Object... par)
	{
		this(clazz,getClasses(par),par);
	}
	public Object getField(String name)
	{
		try
		{
			return cls.getField(name).get(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public void setField(String name,Object value)
	{
		try
		{
			cls.getField(name).set(obj,value);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public Object invoke(String name,Class[] type,Object... param)
	{
		try
		{
			if(type==null)return cls.getMethod(name).invoke(obj);
			else return cls.getMethod(name,type).invoke(obj,param);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public Object invoke(String name)
	{
		return invoke(name,null,null);
	}
	public Object invoke(String name,Object... par)
	{
		return invoke(name,getClasses(par),par);
	}
	public Object invokeSetInterface(String name,String interf,InvocationHandler h)
	{
		try
		{
			Class ff=Class.forName(cls.getName()+"$"+interf);
			return invoke(name,new Class[]{ff},createInterface(ff,h));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static Object createInterface(Class interfaceClass,InvocationHandler h)
	{
		return Proxy.newProxyInstance(ProxyAPI.class.getClassLoader(),new Class[]{interfaceClass},h);
	}
	public static Class[] getClasses(Object... p)
	{
		Class[] c=new Class[p.length];
		for(int i=0;i<c.length;i++)
		{
			if(p[i]!=null)
			{
				c[i]=p[i].getClass();
				if(c[i]==Byte.class)c[i]=byte.class;
				else if(c[i]==Short.class)c[i]=short.class;
				else if(c[i]==Integer.class)c[i]=int.class;
				else if(c[i]==Long.class)c[i]=long.class;
				else if(c[i]==Float.class)c[i]=float.class;
				else if(c[i]==Double.class)c[i]=double.class;
				else if(c[i]==Boolean.class)c[i]=boolean.class;
				else if(c[i]==Proxy.class)c[i]=((Proxy)p[i]).getClass();
			}
		}
		return c;
	}
}
