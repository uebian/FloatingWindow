package com.mojang.minecraftpe;
import android.app.Activity;
import java.lang.ref.WeakReference;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends Activity
{
	public void onPointerCaptureChanged(boolean hasCapture)
	{
		// TODO: Implement this method
	}

	public static WeakReference<Activity> currentMainActivity=null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		currentMainActivity=new WeakReference<Activity>(this);
	}
}
