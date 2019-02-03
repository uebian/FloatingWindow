package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.Intent;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class JsEncode
{
	public JsEncode(Context c,Intent e)
	{
		Window w=new Window(c,util.px(300),util.px(360))
		.setTitle("JS加密器")
		.setIcon("jsencode")
		.show();
	}
	
	}
