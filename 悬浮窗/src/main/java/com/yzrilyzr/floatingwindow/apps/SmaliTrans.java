package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.ClassSrc;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myTextView;
import android.widget.LinearLayout;

public class SmaliTrans implements OnClickListener
{
	private myEditText met;
	private Context ctx;
	private myButton mb;
	public SmaliTrans(Context c,Intent i)
	{
		ctx=c;
		Window w=new Window(ctx,util.px(246),util.px(354))
            .setTitle("Smali方法翻译")
            .setIcon("smali")
            .setBar(0,8,0)
            .show();
        myTextView m=new myTextView(ctx);
        m.setText("请输入反编译Java方法后的smali");
        w.addView(m);
        met=new myEditText(ctx);
		met.setLayoutParams(new LinearLayout.LayoutParams(-1,util.px(200)));
        mb=new myButton(ctx);
		w.addView(met);
        w.addView(mb);
        mb.setText("翻译");
        mb.setOnClickListener(this);
		util.setWeight(met);
	}
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		try
		{
			String s=met.getText().toString();
			API.startService(ctx,new Intent()
								 .putExtra("title","翻译结果")
								 .putExtra("text",translation(s)),cls.TEXTEDITOR);
		}
		catch(Exception e)
		{
			util.toast("翻译错误:"+e);
		}
	}
	private String translation(String sb)
	{
		String[] s=sb.split("\n");
		String result="";
		StringBuilder bd=new StringBuilder();
		for(String d:s)
		{
			if(d.startsWith("aget"))
			{
				String[] k=d.split(" ");
				bd.append(k[2]+"["+k[3]+"]");
			}
			else if(d.startsWith("monitor-enter"))
			{
				String[] k=d.split(" ");
				bd.append("synchronized开始("+k[1]+")");
			}
			else if(d.startsWith("monitor-exit"))
			{
				String[] k=d.split(" ");
				bd.append("synchronized结束("+k[1]+")");
			}
			else if(d.startsWith("aput"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"="+k[2]+"["+k[3]+"]");
			}
			else if(d.startsWith("move-r")||d.startsWith("move-e"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"="+result);
			}
			else if(d.startsWith("move"))
			{
			String[] k=d.split(" ");
			bd.append(k[1]+"="+k[2]);
			}
			else if(d.startsWith("iget"))
			{
				String[] k=d.split(" ");
				String[] k2=k[3].split("->");
				String[] k3=k2[1].split(":");
				bd.append(k[2]+"="+"("+getType(k3[1])+")(("+getType(k2[0])+")"+k[1]+")."+k3[0]);
			}
			else if(d.startsWith("iput"))
			{
				String[] k=d.split(" ");
				String[] k2=k[3].split("->");
				String[] k3=k2[1].split(":");
				bd.append(k[2]+"="+"("+getType(k3[1])+")(("+getType(k2[0])+")"+k[1]+")."+k3[0]);
			}
			else if(d.startsWith("check"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"可转换为"+getType(k[2]));
			}
			else if(d.startsWith("instance-of"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"="+k[2]+" instanceof "+getType(k[3]));
			}
			else if(d.startsWith("add"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"+="+k[2]);
			}
			else if(d.startsWith("sub"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"-="+k[2]);
			}
			else if(d.startsWith("mul"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"*="+k[2]);
			}
			else if(d.startsWith("div"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"/="+k[2]);
			}
			else if(d.startsWith("rem"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"%="+k[2]);
			}
			else if(d.startsWith("new-instance"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"=new "+getType(k[2]));
			}
			else if(d.startsWith("array-length"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"="+k[2]+".length");
			}
			else if(d.startsWith("invoke"))
			{
				String k[]=d.split(" ");
				String[] o=k[1].substring(1,k[1].length()-1).split(",");
				//Landroid/graphics/Canvas;->drawColor(I)V
				String[] k2=k[2].split("->");
				String methodname=k2[1].split("\\(")[0];
				String param="";
				for(int y=1;y<o.length;y++)param+=o[y]+(y==o.length-1?"":",");
				result="(("+getType(k2[0])+")"+o[0]+")."+methodname+"("+param+");";
				bd.append("调用:"+result);
			}
			else if(d.startsWith("throw"))
			{
				bd.append(d+";");
			}
			else if(d.startsWith("const"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"="+k[2]);
			}
			else if(d.startsWith("goto"))
			{
				bd.append("跳转:"+d.split(" :")[1]);
			}
			else if(d.startsWith("if-eqz"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"==0)跳转:"+k[2]);
			}
			else if(d.startsWith("if-nez"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"!=0)跳转:"+k[2]);
			}
			else if(d.startsWith("if-ltz"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"<0)跳转:"+k[2]);
			}
			else if(d.startsWith("if-gez"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+">=0)跳转:"+k[2]);
			}
			else if(d.startsWith("if-gtz"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+">0)跳转:"+k[2]);
			}
			else if(d.startsWith("if-lez"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"<=0)跳转:"+k[2]);
			}
			else if(d.startsWith("if-eq"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"=="+k[2]+")跳转:"+k[3]);
			}
			else if(d.startsWith("if-ne"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"!="+k[2]+")跳转:"+k[3]);
			}
			else if(d.startsWith("if-lt"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"<"+k[2]+")跳转:"+k[3]);
			}
			else if(d.startsWith("if-ge"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+">="+k[2]+")跳转:"+k[3]);
			}
			else if(d.startsWith("if-gt"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+">"+k[2]+")跳转:"+k[3]);
			}
			else if(d.startsWith("if-le"))
			{
				String[] k=d.split(" ");
				bd.append("if("+k[1]+"<="+k[2]+")跳转:"+k[3]);
			}
			else if(d.startsWith("return"))
			{
				if(d.contains("-void"))bd.append("return;");
				else bd.append("return "+d.split(" ")[1]);
			}
			else if(d.startsWith("cmp"))
			{
				String[] k=d.split(" ");
				bd.append(k[1]+"="+k[2]+"=="+k[3]);
			}
			else bd.append("无法翻译:"+d);
			bd.append("\n");
		}
		return bd.toString();
	}
	public static String getType(String s)
	{
		String type="未知类型";
		switch(s.substring(0,1))
		{
			case "Z":type="boolean";break;
			case "B":type="byte";break;
			case "S":type="short";break;
			case "C":type="char";break;
			case "I":type="int";break;
			case "J":type="long";break;
			case "F":type="float";break;
			case "D":type="double";break;
			case "V":type="void";break;
			case "L":type=s.substring(1,s.length()-1).substring(s.contains("/")?s.lastIndexOf("/"):0);break;
			case "[":type="数组";break;
		}
		return type;
	}
}
