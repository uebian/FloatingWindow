package com.yzrilyzr.myclass;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.ui.uidata;

public class myActivity extends Activity implements View.OnClickListener
{
	public void onPointerCaptureChanged(boolean hasCapture)
	{
		// TODO: Implement this method
	}
	
	public myActivity ctx=this;
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(uidata.BACK));
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
		{
			ctx.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			ctx.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO)
		{} 
		else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES)
		{}
	}
	@Override
	public void finish()
	{
		// TODO: Implement this method
		super.finish();
		overridePendingTransition(R.anim.activity_in2, R.anim.activity_out2);
	}
	public void exit(View v)
	{this.finish();}
}

