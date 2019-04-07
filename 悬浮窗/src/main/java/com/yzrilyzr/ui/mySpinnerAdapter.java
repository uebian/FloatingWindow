package com.yzrilyzr.ui;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import com.yzrilyzr.myclass.util;

public class mySpinnerAdapter implements SpinnerAdapter
{
	public CharSequence[] getAutofillOptions()
	{
		// TODO: Implement this method
		return null;
	}
	
	String[] ws=new String[0];
	public mySpinnerAdapter(String[] k){
		ws=k;
	}
	@Override
	public void registerDataSetObserver(DataSetObserver p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean hasStableIds()
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public int getItemViewType(int p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int getViewTypeCount()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public View getDropDownView(int p1, View p2, ViewGroup p3)
	{
		myTextView t=new myTextView(util.ctx);
		t.setText(ws[p1]);
		int p=util.px(7);
		t.setPadding(p,p,p,p);
		return t;
	}

	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return ws.length;
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		myTextView t=new myTextView(util.ctx);
		t.setText(ws[p1]);
		int p=util.px(7);
		t.setPadding(p,p,p,p);
		return t;
	}
}
