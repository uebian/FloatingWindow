package com.yzrilyzr.lock;

import android.app.*;
import android.os.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		API.startMainService(this,getPackageName()+".Main");
		finish();
    }
}
