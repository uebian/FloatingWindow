package com.yzrilyzr.doutuzhushou;

import android.app.*;
import android.os.*;
import com.yzrilyzr.FloatWindow.API;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        API.startMainService(this,getPackageName()+".doutu");
        finish();
    }
}
