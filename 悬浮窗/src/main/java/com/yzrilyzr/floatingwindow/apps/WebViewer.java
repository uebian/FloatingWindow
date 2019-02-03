package com.yzrilyzr.floatingwindow.apps;
import android.webkit.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myImageButton;
import com.yzrilyzr.ui.myProgressBar;
import android.webkit.WebChromeClient.CustomViewCallback;
import com.yzrilyzr.ui.myLinearLayoutBack;
import com.yzrilyzr.ui.myLoadingView;

public class WebViewer implements DownloadListener,OnClickListener,Window.OnButtonDown,TextView.OnEditorActionListener
{
	WebView web;
	myProgressBar prog;
	Context ctx;
	Window w,cv;
	myEditText edit;
	myImageButton ba,bb,bc,bd,be;
	boolean sho=true;
	public WebViewer(Context c,Intent e)
	{
		ctx=c;
		w=new Window(c,util.px(300),util.px(360))
			.setTitle("WebViewer")
			.setIcon("internet")
			.show();
		ViewGroup v=(ViewGroup) w.addView(R.layout.window_webviewer);
		web=(WebView)v.getChildAt(2);
		prog=(myProgressBar) v.getChildAt(1);
		ViewGroup f=(ViewGroup)v.getChildAt(0);
		edit=(myEditText) f.getChildAt(0);
		edit.setOnEditorActionListener(this);
		be=(myImageButton) f.getChildAt(1);
		be.setOnClickListener(this);
		f=(ViewGroup)v.getChildAt(3);	
		(ba=(myImageButton) f.getChildAt(0)).setOnClickListener(this);
		(bb=(myImageButton) f.getChildAt(1)).setOnClickListener(this);
		(bc=(myImageButton) f.getChildAt(2)).setOnClickListener(this);
		(bd=(myImageButton) f.getChildAt(3)).setOnClickListener(this);
		WebSettings s=web.getSettings();
		s.setAppCacheEnabled(true);
		s.setDatabaseEnabled(true);
		s.setJavaScriptEnabled(true);
		s.setPluginState(WebSettings.PluginState.ON);
		s.setUseWideViewPort(true);
		s.setSavePassword(true);
        s.setSaveFormData(true);
		s.setLoadWithOverviewMode(true);
		s.setCacheMode(WebSettings.LOAD_NO_CACHE);
		s.setBuiltInZoomControls(true);// 隐藏缩放按钮
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);// 排版适应屏幕
		s.setGeolocationEnabled(true);// 启用地理定位
		s.setGeolocationDatabasePath(ctx.getDatabasePath("webviewer").getAbsolutePath());// 设置定位的数据库路径
		s.setDomStorageEnabled(true);
		s.setSupportMultipleWindows(true);// 新加
		cv=new Window(ctx,util.px(320),util.px(180));
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
		{
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
		web.setWebViewClient(new WebViewClient(){
				@Override
				public void onPageStarted(WebView view,String url,Bitmap favicon)
				{
					edit.setText(url);
					w.setIcon(new BitmapDrawable(favicon));
					if(favicon==null)w.setIcon("internet");
				}
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if(url.startsWith("http")){
					web.loadUrl(url);
					return false;
					}else return true;
				}
			});
		web.setWebChromeClient(new WebChromeClient(){

				private WebChromeClient.CustomViewCallback cbk;
				private View cview;
				
				public void onProgressChanged(WebView view, int progress)   
				{
					ba.setEnabled(web.canGoBack());
					bb.setEnabled(web.canGoForward());
					prog.setVisibility(View.VISIBLE); 
					prog.setProgress(progress);     
					if(progress == 100)
					{     
						prog.setVisibility(View.GONE); 
						w.setTitle(view.getTitle());
					}
				}
				public boolean onJsAlert(WebView view,String url,String message,final JsResult result)
				{
					if(sho)new myDialog.Builder(ctx)
							.setTitle("来自网页的提示")
							.setCancelable(false)
							.setMessage(message)
							.setPositiveButton("确定",new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface v,int i)
								{
									result.confirm();
								}
							})
							.setNeutralButton("不要再显示",new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface v,int i)
								{
									sho=false;
									result.cancel();
								}
							})
							.setNegativeButton("取消",new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface v,int i)
								{
									result.cancel();
								}
							})
							.show();
					else result.cancel();
					return true;
				}
				@Override
				public void onShowCustomView(View view, CustomViewCallback callback) {
					if (cview != null) {
						callback.onCustomViewHidden();
						return;
					}
					cv.addView(view);
					cview = view;
					cbk = callback;
					cv.show();
				}

				// 视频播放退出全屏会被调用的
				@Override
				public void onHideCustomView() {
					if (cview == null)// 不是全屏播放状态
						return;
					cv.dismiss();
					cview = null;
					cbk.onCustomViewHidden();
				}

				// 视频加载时进程loading
				@Override
				public View getVideoLoadingProgressView() {
					myLinearLayoutBack l=new myLinearLayoutBack(ctx);
					l.addView(new myLoadingView(ctx));
					return l;
				}
			});
		web.setDownloadListener(this);
		String ur=e.getStringExtra("url");
		if(ur!=null)web.loadUrl(ur);
		else web.loadUrl("http://m.hao123.com/");  
	}
	@Override
	public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
	{
		if(p2==6)
		{
			String s=edit.getText().toString();
			if(s.indexOf("://")==-1)s="http://www.baidu.com/s?word="+s;
			edit.setText(s);
			web.loadUrl(s);
		}
		return false;
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.MIN)
			if(!w.getMin())
			{
				web.onPause();
				web.pauseTimers();
			}
			else
			{
				web.onResume();
				web.resumeTimers();
			}
		else if(code==Window.ButtonCode.CLOSE)
		{
			web.loadUrl("about:blank");
			web.destroy();
			web=null;
		}
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==ba)web.goBack();
		else if(p1==bb)web.goForward();
		else if(p1==bc)web.reload();
		else if(p1==bd)web.stopLoading();
		else if(p1==be)onEditorAction(edit,6,null);
	}
	@Override
	public void onDownloadStart(String p1, String p2, String p3, String p4, long p5)
	{
		API.startService(ctx,new Intent().putExtra("url",p1).putExtra("length",p5),cls.DOWNLOADER);
	}
}
