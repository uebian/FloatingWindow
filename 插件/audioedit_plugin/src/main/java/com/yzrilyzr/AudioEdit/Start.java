package com.yzrilyzr.AudioEdit;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.yzrilyzr.floatingwindow.pluginapi.API;

public class Start extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		String f="";
		try
		{
			f=Uri.parse(getIntent().getDataString()).getPath();
		}
		catch(Throwable e)
		{}
		API.startMainService(this,new Intent().putExtra("file",f),"com.yzrilyzr.AudioEdit.Main");
		finish();
	} 
}
