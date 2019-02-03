package com.yzrilyzr.floatingwindow;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myTextView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import com.yzrilyzr.myclass.util;
import org.mozilla.javascript.NativeObject;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JSEnv implements Runnable
{
	private Scriptable scope;
	private String js;
	private Object cbk;
	public JSEnv(String js,Object cbk)
	{
		this.js=js;
		this.cbk=cbk;
		new Thread(Thread.currentThread().getThreadGroup(),this,"js解析线程",256*1024).start();
	}
	public void eval(String func)
	{
		Context rhino=Context.enter();
		rhino.evaluateString(scope,func,"JS",1, null);
	}
	public void function(String name,Object... o)
	{
		Context rhino=Context.enter();
		((Function)scope.get(name,scope)).call(rhino, scope, scope,o);
	}
	@Override
	public void run()
	{
		try
		{
			Context rhino = Context.enter();
			rhino.setOptimizationLevel(-1);
			scope = rhino.initStandardObjects();
			ScriptableObject.putProperty(scope, "javaContext", Context.javaToJS(util.ctx, scope));
			ScriptableObject.putProperty(scope, "javaLoader", Context.javaToJS(JSEnv.class.getClassLoader(), scope));
			BufferedReader i=new BufferedReader(new InputStreamReader(util.ctx.getAssets().open("MODPE/BaseApi.js")));
			StringBuilder b=new StringBuilder();
			String x=null;
			while((x=i.readLine())!=null)b.append(x).append("\n");
			i.close();
			rhino.evaluateString(scope,b.toString(),"NativeCode",1,null);
			((Function)scope.get("_setcbk",scope)).call(rhino, scope, scope,new Object[]{cbk});
			rhino.evaluateString(scope,js,"JS",1, null);
			rhino.evaluateString(scope,b.toString(),"NativeCode",1,null);
			((Function)scope.get("_setcbk",scope)).call(rhino, scope, scope,new Object[]{cbk});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				Context rhino = Context.enter();
				((Function)scope.get("print",scope)).call(rhino, scope, scope,new Object[]{"js执行失败:"+util.getStackTrace(e)});
			}
			catch(Throwable ee)
			{}
		}
	}
}
