package com.yzrilyzr.tts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import com.yzrilyzr.floatingwindow.pluginapi.API;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		try
		{
			String l=getPackageManager().getPackageInfo("com.yzrilyzr.tts",PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.nativeLibraryDir;
			File[] fs=new File(l).listFiles();
			File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/悬浮窗/文字转语音/lib");
			if(!f.exists()){
				f.mkdirs();
				for(File h:fs){
					FileChannel i=new FileInputStream(h).getChannel();
					FileChannel o=new FileOutputStream(f.getAbsolutePath()+"/"+h.getName()).getChannel();
					i.transferTo(0,i.size(),o);
					i.close();
					o.close();
				}
				String[] s=getAssets().list("");
				/*new String[]{
					"bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat",
					"bd_etts_text.dat"
				};*/
				for(String h:s){
					try{
					InputStream i=getAssets().open(h);
					BufferedOutputStream o=new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()+"/"+h));
					byte[] b=new byte[1024];
					int y=0;
					while((y=i.read(b))!=-1)o.write(b,0,y);
					i.close();
					o.flush();
					o.close();
					}catch(Throwable e){}
				}
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this,e+"",0).show();
		}
        API.startMainService(this,new Intent(),"com.yzrilyzr.tts.Main");
		finish();
    }
}
