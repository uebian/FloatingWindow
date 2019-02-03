package com.yzrilyzr.icondesigner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
public class MainActivity extends Activity 
{
	long time=0;
	static String file;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(new MainView(this));
		try
        {
            file=getIntent().getDataString();
            file=Uri.parse(file).getPath();
        }
        catch(Exception e)
        {
			file=null;
		}
    }
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		try
        {
            file=intent.getDataString();
            file=Uri.parse(file).getPath();
        }
        catch(Exception e)
        {
			file=null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3 && resultCode == RESULT_OK && null != data)
		{  
			Uri selectedImage = data.getData();  
			String[] filePathColumn = { MediaStore.Images.Media.DATA };  
			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);  
			cursor.moveToFirst();  
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
			final String picturePath = cursor.getString(columnIndex);  
			cursor.close();
			MainView.render.builder.setBgpath(picturePath);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK&&time+1000<System.currentTimeMillis())
		{
			if(MainView.render.toast!=null)MainView.render.toast("再按一次退出");
			time=System.currentTimeMillis();
			return true;
		}
		return super.onKeyDown(keyCode,event);
	}
}
