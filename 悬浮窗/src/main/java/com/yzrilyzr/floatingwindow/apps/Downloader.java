package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.myProgressBar;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.text.TextWatcher;
import android.text.Editable;
import com.yzrilyzr.icondesigner.VecView;
import android.os.AsyncTask;
import com.yzrilyzr.floatingwindow.API;

public class Downloader implements OnClickListener,Runnable,Window.OnButtonDown,TextWatcher
{
	Context ctx;
	Window w;
	EditText e1,e2;
	View cc,dd;
	boolean dow=false,isrun=true;
	myProgressBar pgb;
	long progress,length;
	public Downloader(Context c,Intent e)
	{
		ctx=c;
		String p1=e.getStringExtra("url");
		if(p1==null)p1="";
		w=new Window(c,util.px(300),util.px(400))
		.setTitle("下载器")
		.setIcon("download")
		.setOnButtonDown(this)
		.show();
		ViewGroup v=(ViewGroup) w.addView(R.layout.window_webviewer_download);
		cc=v.getChildAt(0);
		dd=v.getChildAt(1);
		e1=(EditText)v.findViewById(R.id.windowwebviewerdownloadmyEditText1);
		e2=(EditText) v.findViewById(R.id.windowwebviewerdownloadmyEditText2);
		v.findViewById(R.id.windowwebviewerdownloadVecView1).setOnClickListener(this);
		pgb=(myProgressBar) v.findViewById(R.id.windowwebviewerdownloadmyProgressBar1);
		e1.setText(p1);
		e1.addTextChangedListener(this);
		run();
	}

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		try
		{
			e2.setText(util.mainDir+"/下载的文件/"+URLDecoder.decode(p1.toString().substring(p1.toString().lastIndexOf("/")+1)));
		}
		catch(Throwable q)
		{q.printStackTrace();}

	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			dow=false;
		}
	}
	@Override
	public void run()
	{
		try
		{
			if(isrun)new Handler(ctx.getMainLooper()).postDelayed(this,300);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void onClick(View py1)
	{
		((VecView)py1).setImageVec(dow?"pause":"play");
		if(!dow)new DownloaderTask().execute(e1.getText()+"",e2.getText()+"");
	}
	private class DownloaderTask extends AsyncTask
	{
		@Override
		protected void onPreExecute()
		{
			
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			util.toast("下载完毕");
			API.startService(ctx,new Intent().putExtra("path",(String)result),cls.EXPLORER);
		}

		@Override
		protected void onCancelled(Object result)
		{
			// TODO: Implement this method
			super.onCancelled(result);
			util.toast(result);
		}
		@Override
		protected void onProgressUpdate(Object[] values)
		{
			pgb.setProgress((int)progress);
			pgb.setMax((int)length);
			super.onProgressUpdate(values);
		}
		@Override
		protected Object doInBackground(Object[] p1)
		{
			String url=(String)p1[0];
			String to=(String)p1[1];
			dow=true;
			try
			{    
				File file=new File(to.substring(0,to.lastIndexOf("/")));   
				if(!file.exists())file.mkdirs();
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
				if(progress!=0)get.addHeader("Range", "bytes="+progress+"-");   
                HttpResponse response = client.execute(get);   
				int code=response.getStatusLine().getStatusCode();
				if(HttpStatus.SC_OK==code||HttpStatus.SC_PARTIAL_CONTENT==code)
				{   
                    HttpEntity entity = response.getEntity();   
                    InputStream input = entity.getContent();
					length=input.available();
					byte[] bu=new byte[10240];
					int ii=0,k=0;
					BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(to));
					while(dow&&(ii=input.read(bu))!=-1)
					{
						os.write(bu,0,ii);
						os.flush();
						progress+=ii;
						if(k++==100)publishProgress();
					}
					os.close();
                    input.close();
					if(dow)return "下载完毕";
                }
				else
				{   
                    return "无法连接服务器";
                }   
            }
			catch (Exception e)
			{
				return "下载失败";
            }
			return "下载失败";
		}
	}
}
