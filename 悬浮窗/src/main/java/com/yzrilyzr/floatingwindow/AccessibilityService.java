package com.yzrilyzr.floatingwindow;
import android.accessibilityservice.AccessibilityService;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import com.yzrilyzr.floatingwindow.apps.cls;
import android.os.Vibrator;

public class AccessibilityService extends AccessibilityService
{
	int k=0,u=0;
	@Override
	public void onAccessibilityEvent(AccessibilityEvent p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onInterrupt()
	{
		// TODO: Implement this method
	}
	@Override
	protected boolean onKeyEvent(KeyEvent event)
	{
		int c=event.getKeyCode(),e=event.getAction();
		int d=KeyEvent.ACTION_DOWN;
		if(e==d)
			if(c==KeyEvent.KEYCODE_VOLUME_DOWN)
			{
				u=0;
				if(k<2)k++;
				else k=0;
			}
			else if(c==KeyEvent.KEYCODE_HOME)
			{
				u=0;
				if(k>=2)k++;
				else k=0;
			}
			else if(c==KeyEvent.KEYCODE_MENU)
			{
				k=0;
				if(u<2)u++;
				else u=0;
			}
			else if(c==KeyEvent.KEYCODE_BACK)
			{
				k=0;
				if(u>=2)u++;
				else u=0;
			}
			else
			{
				k=0;u=0;
			}
		if(k==4)
		{
			PluginService.fstop(this);
			k=0;
			((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(200);
			return true;
		}
		else if(u==4){
			API.startService(this,cls.PLUGINPICKER);
			API.startService(this,cls.MANAGER);
			u=0;
			((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(200);
			return true;
		}
		return super.onKeyEvent(event);
	}
}
