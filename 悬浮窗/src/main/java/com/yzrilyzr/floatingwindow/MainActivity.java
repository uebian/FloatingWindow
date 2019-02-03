package com.yzrilyzr.floatingwindow;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.util;
import java.io.File;
import java.lang.reflect.Field;
import android.app.Fragment;

public class MainActivity extends Activity
{
	public static Object activityThread;
	static{
		Copyright.a();
	}
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		try
		{
			Field f=Activity.class.getField("mMainThread");
			f.setAccessible(true);
			activityThread=f.get(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Copyright.a();
		util.ctx=getApplicationContext();
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
		{
			if(!Settings.canDrawOverlays(this))
			{
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
				Uri.parse("package:" + getPackageName()));
				startActivityForResult(intent,10);
				Toast.makeText(this,"请设置悬浮窗权限\n之后重新启动本程序",1).show();
			}
			/*if(PackageManager..checkSelfPermission app
			Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				activity.requestPermissions(new String[]{
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
				}, 1);
				Intent intent = getPackageManager().buildRequestPermissionsIntent(permissions);
					startActivityForResult(REQUEST_PERMISSIONS_WHO_PREFIX, intent, requestCode, null);
			/*if(!Settings.System.canWrite(this))
			 {
			 Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, 
			 Uri.parse("package:" + getPackageName()));
			 startActivityForResult(intent,9);
			 }*/
        }
	if(!util.getSPRead("abouta").getBoolean("abouta",false))
		{
			startActivity(new Intent(this,AboutActivity.class));
			finish();
		}
		else
		{
			startService(new Intent(this,PluginService.class));
			Intent e=getIntent();
			System.out.println(e);
			String a=e.getAction();
			if(Intent.ACTION_VIEW.equals(a)||
			Intent.ACTION_EDIT.equals(a)
			)
			{
				finish();
				String f=e.getDataString();
				if(f!=null)
				{
					String[] g=f.split("://");
					switch(g[0])
					{
						case "file":
							//文件处理
							util.open(f,Uri.parse(f).getPath());
							break;
							//网页处理
						case "http":
						case "https":
							API.startService(this,new Intent().putExtra("url",f),cls.WEBVIEWER);
							break;
					}
				}
			}
			else if(Intent.ACTION_GET_CONTENT.equals(a)||
			Intent.ACTION_PICK.equals(a))API.startServiceForResult(this,new BroadcastReceiver(){
					@Override
					public void onReceive(Context p1, Intent p2)
					{
						MainActivity.this.setResult(RESULT_OK,new Intent()
						.setData(Uri.fromFile(new File(p2.getStringExtra("path"))))
						);
						finish();
					}
				},cls.EXPLORER);
			else finish();
			if(e.hasCategory("de.robv.android.xposed.category.MODULE_SETTINGS"))API.startService(this,cls.SETTINGS);
		}
    }
}
