package com.yzrilyzr.myclass;

import java.lang.reflect.*;

import java.lang.annotation.Annotation;

public class ClassSrc
{
    private StringBuilder a;
    public ClassSrc(Class<?> clazz,boolean api)
	{
        a=new StringBuilder();
        if(!api)getClassSrc(clazz,0);
		else getAPIClassSrc(clazz,0);
    }
	public String get()
	{
        return a.toString();
    }
    public void getClassSrc(Class<?> c,int inter)
    {
        //head
        Package pkg=c.getPackage();
        if(pkg!=null&&inter==0)a.append("package "+c.getPackage().getName()+";");
        appendN(a,0);
        //annotation
        appendAnnotation(c.getDeclaredAnnotations(),a,inter+1);
        appendN(a,inter);
        //class head
        appendModifier(a,c);
		if(a.indexOf("interface")==-1)a.append("class ");
        a.append(splitClass(c).replace(pkg==null?"null":pkg.getName()+".",""));
        //extends
        Type su=c.getGenericSuperclass();
        if(su!=null)a.append(" extends "+splitType(su));
        //implements
        appendClasses(c.getGenericInterfaces(),a,"implements");
        //start
        appendN(a,inter);
        a.append("{");
        //field
        Field[] fs=c.getDeclaredFields();
        for(Field m:fs)
        {
            appendAnnotation(m.getDeclaredAnnotations(),a,inter+1);
            appendN(a,inter+1);
            appendModifier(a,m);
            appendType(m.getGenericType(),a);
            a.append(m.getName());
            try
            {
                m.setAccessible(true);
                Object o=m.get(c);
                a.append(" = "+o);
            }
            catch(Throwable e)
            {}
            a.append(";");
            appendN(a,inter+1);
        }
        //method
        Method[] ms=c.getDeclaredMethods();
        for(Method m:ms)
        {
            appendAnnotation(m.getDeclaredAnnotations(),a,inter+1);
            appendN(a,inter+1);
            appendModifier(a,m);
            appendType(m.getGenericReturnType(),a);
            a.append(m.getName()+"(");
            appendParam(m.getGenericParameterTypes(),a);
            appendClasses(m.getGenericExceptionTypes(), a,"throws");
            a.append(" {}");
            appendN(a,inter+1);
        }
        //constructor
        Constructor<?>[] cs=c.getDeclaredConstructors();
        for(Constructor<?> m:cs)
        {
            appendAnnotation(m.getDeclaredAnnotations(),a,inter+1);
            appendN(a,inter+1);
            appendModifier(a,m);
            a.append(c.getSimpleName()+"(");
            appendParam(m.getGenericParameterTypes(),a);
            appendClasses(m.getGenericExceptionTypes(), a,"throws");
            a.append(" {}");
            appendN(a,inter+1);
        }
        //class
        Class<?>[] cls=c.getDeclaredClasses();
        for(Class<?> cc:cls)
        {
            if(splitType(cc.getGenericSuperclass()).indexOf(">")!=-1)
            {
                Object[] enu=cc.getEnumConstants();
                appendN(a,inter+1);
                appendModifier(a,cc);
                a.append("enum ");
                a.append(cc.getSimpleName());
                appendN(a,inter+1);
                a.append("{");
                for(Object o:enu)
                {
                    appendN(a,inter+2);
                    a.append(o+(o==enu[enu.length-1]?";":","));
                }
                appendN(a,inter+1);
                a.append("}");
            }
            else getClassSrc(cc,inter+1);
        }
        //end
        appendN(a,inter);
        a.append("}");
    }
	public void getAPIClassSrc(Class<?> c,int inter)
    {
        //head
        Package pkg=c.getPackage();
        if(pkg!=null&&inter==0)a.append("package "+c.getPackage().getName()+";");
        appendN(a,0);
        //annotation
        appendAnnotation(c.getDeclaredAnnotations(),a,inter+1);
        appendN(a,inter);
        //class head
        appendModifier(a,c);
		if(a.indexOf("interface")==-1)a.append("class ");
        a.append(splitClass(c).replace(pkg==null?"null":pkg.getName()+".",""));
        //extends
        a.append(" extends ProxyAPI");
        //implements
        appendClasses(c.getGenericInterfaces(),a,"implements");
        //start
        appendN(a,inter);
        a.append("{");
        //field
        Field[] fs=c.getDeclaredFields();
        for(Field m:fs)
        {
			if(Modifier.toString(m.getModifiers()).contains("private"))continue;
			appendAnnotation(m.getDeclaredAnnotations(),a,inter+1);
            appendN(a,inter+1);
            appendModifier(a,m);
            appendType(m.getGenericType(),a);
            a.append(m.getName());
            try
            {
                m.setAccessible(true);
                Object o=m.get(c);
                a.append(" = "+o);
            }
            catch(Throwable e)
            {}
            a.append(";");
            appendN(a,inter+1);
        }
        //method
        Method[] ms=c.getDeclaredMethods();
        for(Method m:ms)
        {
			if(Modifier.toString(m.getModifiers()).contains("private"))continue;
			appendAnnotation(m.getDeclaredAnnotations(),a,inter+1);
            appendN(a,inter+1);
            appendModifierNoNative(a,m);
            appendType(m.getGenericReturnType(),a);
			StringBuilder b=new StringBuilder();
            a.append(m.getName()).append("(");
            appendParam(m.getGenericParameterTypes(),a);
            appendClasses(m.getGenericExceptionTypes(), a,"throws");
			appendN(a,inter+1);
			a.append("{");
			appendN(a,inter+2);
			b.append(m.getName()).append("(");
            appendParam(m.getGenericParameterTypes(),b);
			if(!splitType(m.getGenericReturnType()).equals("void"))a.append("return ");
			a.append("invoke(\"");
			String[] d=b.toString().split("\\(");
			a.append(d[0]).append("\"");
			d=d[1].split(",");
			if(d.length!=0&&!d[0].equals(")"))
			{
				a.append(",new Class[]{");
				for(int i=0;i<d.length;i++)
				{
					a.append(d[i].split(" ")[0]).append(".class");
					if(i!=d.length-1)a.append(",");
				}
				a.append("}");
				for(int i=0;i<d.length;i++)
					a.append(",").append(d[i].split(" ")[1]);
			}
			else a.append(")");
			a.append(";");
			appendN(a,inter+1);
			a.append("}");
            appendN(a,inter+1);
        }
        //constructor
        Constructor<?>[] cs=c.getDeclaredConstructors();
        for(Constructor<?> m:cs)
        {
            appendAnnotation(m.getDeclaredAnnotations(),a,inter+1);
            appendN(a,inter+1);
            appendModifier(a,m);
            a.append(c.getSimpleName()+"(");
            appendParam(m.getGenericParameterTypes(),a);
            appendClasses(m.getGenericExceptionTypes(), a,"throws");
            appendN(a,inter+1);
			a.append("{");
			appendN(a,inter+2);
			StringBuilder b=new StringBuilder();
			b.append(m.getName()).append("(");
            appendParam(m.getGenericParameterTypes(),b);
			a.append("super(\"")
			.append(c.getName())
			.append("\"");
			String[] d=b.toString().split("\\(");
			d=d[1].split(",");
			if(d.length!=0&&!d[0].equals(")"))
			{
				a.append(",new Class[]{");
				for(int i=0;i<d.length;i++)
				{
					a.append(d[i].split(" ")[0]).append(".class");
					if(i!=d.length-1)a.append(",");
				}
				a.append("}");
				for(int i=0;i<d.length;i++)
					a.append(",").append(d[i].split(" ")[1]);
			}
			else a.append(")");
			a.append(";");
			appendN(a,inter+1);
			a.append("}");
            appendN(a,inter+1);
        }
        //class
        Class<?>[] cls=c.getDeclaredClasses();
        for(Class<?> cc:cls)
        {
            if(splitType(cc.getGenericSuperclass()).indexOf(">")!=-1)
            {
                Object[] enu=cc.getEnumConstants();
                appendN(a,inter+1);
                appendModifier(a,cc);
                a.append("enum ");
                a.append(cc.getSimpleName());
                appendN(a,inter+1);
                a.append("{");
                for(Object o:enu)
                {
                    appendN(a,inter+2);
                    a.append(o+(o==enu[enu.length-1]?";":","));
                }
                appendN(a,inter+1);
                a.append("}");
            }
            else getClassSrc(cc,inter+1);
        }
        //end
        appendN(a,inter);
        a.append("}");
    }
    public void appendType(Type t,StringBuilder b)
    {
        b.append(splitType(t)+" ");
    }
    public void appendParam(Type[] ts, StringBuilder a)
    {
        int i=1;
        for(Type t:ts)
        {
            a.append(splitType(t)+(i==ts.length?" p"+i:" p"+i+","));
            i++;
        }
        a.append(")");
    }
    public void appendClasses(Type[] ts, StringBuilder a,String key)
    {
        if(ts.length!=0)a.append(" "+key+" ");
        int i=1;
        for(Type t:ts)
        {
            a.append(splitType(t)+(i==ts.length?"":" ,"));
            i++;
        }

    }
    public void appendAnnotation(Annotation[] as, StringBuilder a, int inter)
    {
        for(Annotation an:as)
        {
            appendN(a,inter);
            a.append(an);
        }
    }
    public String splitClass(Class<?> c)
    {
        if(c==null)return "";
        String[] s=c.toString().split(" ");
        String t=s[s.length-1];
        int size=0;
        char ch=0;
        while((ch=t.charAt(0))=='[')
        {
            t=t.substring(1);
            size++;
        }
        switch(ch)
        {
            case 'L':t=t.substring(1,t.length()-1);
                break;
            case 'B':t="byte";
                break;
            case 'C':t="char";
                break;
            case 'D':t="double";
                break;
            case 'F':t="float";
                break;
            case 'I':t="int";
                break;
            case 'S':t="short";
                break;
            case 'Z':t="boolean";
                break;
            case 'J':t="long";
                break;
        }
        for(int i=0;i<size;i++)t+="[]";
        int $=t.indexOf("$");
        while($!=-1)
        {
            t=t.substring($+1,t.length());
            $=t.indexOf("$");
        }
        return t;
    }
    public String splitType(Type c)
    {
        if(c==null)return "";
        String[] s=c.toString().split(" ");
        String t=s[s.length-1];
        int size=0;
        char ch=0;
        while((ch=t.charAt(0))=='[')
        {
            t=t.substring(1);
            size++;
        }
        switch(ch)
        {
            case 'L':t=t.substring(1,t.length()-1);
                break;
            case 'B':t="byte";
                break;
            case 'C':t="char";
                break;
            case 'D':t="double";
                break;
            case 'F':t="float";
                break;
            case 'I':t="int";
                break;
            case 'S':t="short";
                break;
            case 'Z':t="boolean";
                break;
            case 'J':t="long";
                break;
        }
        for(int i=0;i<size;i++)t+="[]";
        int $=t.indexOf("$");
        while($!=-1)
        {
            t=t.substring($+1,t.length());
            $=t.indexOf("$");
        }
        return t;
    }
    public void appendModifier(StringBuilder b,Member a)
    {
        String s=Modifier.toString(a.getModifiers());
        if(!s.equals(""))b.append(s).append(" ");
    }
	public void appendModifierNoNative(StringBuilder b,Member a)
    {
        String s=Modifier.toString(a.getModifiers());
        if(!s.equals(""))b.append(s.replace("native","")).append(" ");
    }
    public void appendModifier(StringBuilder b,Class a)
    {
        String s=Modifier.toString(a.getModifiers());
        if(!s.equals(""))b.append(s).append(" ");
    }
    public void appendN(StringBuilder b,int r)
    {
        b.append("\n");
        for(int i=0;i<r;i++)b.append("    ");
    }

}
