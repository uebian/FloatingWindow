package com.yzrilyzr.ui;
import android.view.*;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.drawable.ColorDrawable;
import java.util.List;
import android.annotation.TargetApi;
public class myDialog extends Dialog
{
	myLinearLayout content;
	LinearLayout view,buttonBar;
	myTextView title;
	LinearLayout titleBar;
	myButton pos,neg,neu;
	VecView icon;
	Context ctx;
	myListView list;
	public myDialog(Context c)
	{
		super(c);
		ctx=c;
		list=new myListView(c);
		content=new myLinearLayoutBack(c);
		content.setOrientation(1);
		//createView
		LayoutParams p=new LayoutParams(util.px(50),util.px(50));
		LinearLayout ll=new LinearLayout(c);
		ll.setGravity(Gravity.CENTER);
		ll.addView(icon=new VecView(c),p);
		icon.setVisibility(8);
		p=new LayoutParams(-1,-2);
		p.weight=1;
		ll.addView(title=new myTextViewTitle(c),p);
		p=new LayoutParams(-1,-2);
		content.addView(titleBar=ll,p);
		title.setVisibility(8);
		int m=util.px(10);
		title.setPadding(m,m,m,m);
		p=new LayoutParams(-1,-2);
		p.weight=1;
		content.addView(view=new LinearLayout(c),p);
		
		LayoutParams lp=new LayoutParams(-2,util.px(40));
		m=util.px(5);
		lp.setMargins(m,m,m,m);
		LinearLayout l=new LinearLayout(c);
		buttonBar=l;
		l.setVisibility(8);
		l.addView(neg=new myButton(c),lp);
		p=new LayoutParams(-2,-2);
		p.weight=1;
		l.addView(new LinearLayout(c),p);
		l.addView(neu=new myButton(c),lp);
		l.addView(pos=new myButton(c),lp);
		neg.setTextColor(uidata.MAIN);
		neu.setTextColor(uidata.MAIN);
		pos.setTextColor(uidata.MAIN);
		pos.setVisibility(8);
		neg.setVisibility(8);
		neu.setVisibility(8);
		neg.setBackgroundDrawable(new myRippleDrawable(uidata.BACK));
		neu.setBackgroundDrawable(new myRippleDrawable(uidata.BACK));
		pos.setBackgroundDrawable(new myRippleDrawable(uidata.BACK));
		p=new LayoutParams(-1,-2);
		content.addView(l,p);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(content);
		super.onCreate(savedInstanceState);
		setCanceledOnTouchOutside(true);
		Window dialogWindow=getWindow();
		dialogWindow.setBackgroundDrawable(new ColorDrawable(0));
		dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
		WindowManager.LayoutParams params=dialogWindow.getAttributes();
        params.width=(int)(util.getScreenWidth()*0.83f);
		params.height=-2;
        dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(params);
	}
	@Override
	public void setTitle(CharSequence titlep)
	{
		title.setVisibility(titlep==null?8:0);
		if(titlep!=null)title.setText(titlep);
	}
	
	@TargetApi(28)
	@Override
	public void onPointerCaptureChanged(boolean hasCapture)
	{
		// TODO: Implement this method
	}
	
	public static class Builder
    {
		private Context ctx;
		private myDialog dialog;
        public Context getContext()
		{
			return ctx;
		}
        public Builder setTitle(int titleId)
		{
			dialog.setTitle(titleId);
			return this;
		}
        public Builder setTitle(CharSequence title)
		{
			dialog.setTitle(title);
			return this;
		}
        public Builder setCustomTitle(View customTitleView)
		{
			dialog.titleBar.removeAllViews();
			dialog.titleBar.addView(customTitleView);
			return this;
		}
        public Builder setMessage(int messageId)
		{
			setMessage(ctx.getString(messageId));
			return this;
		}
		public Builder setMessage(CharSequence message)
		{
			myTextView t=new myTextView(ctx);
			ScrollView v=new ScrollView(ctx);
			ScrollView.LayoutParams p=new ScrollView.LayoutParams(-1,-2);
			int m=util.px(10);
			p.setMargins(m,m,m,0);
			t.setText(message);
			v.addView(t,p);
			v.setLayoutParams(new LayoutParams(-1,-2));
			setView(v);
			return this;
		}
        public Builder setIcon(int iconId)
		{
			dialog.icon.setImageVec(null);
			dialog.icon.setVisibility(0);
			dialog.icon.setImageResource(iconId);
			return this;
		}
        public Builder setIcon(String assetVec)
		{
			dialog.icon.setVisibility(assetVec==null?8:0);
			dialog.icon.setImageVec(assetVec);
			return this;
		}
        public Builder setIcon(Drawable icon)
		{
			dialog.icon.setImageVec(null);
			dialog.icon.setVisibility(icon==null?8:0);
			dialog.icon.setImageDrawable(icon);
			return this;
		}
        public Builder setIconAttribute(int attrId)
		{
			return this;
		}
        public Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener)
		{
			setPositiveButton(ctx.getString(textId),listener);
			return this;
		}
        public Builder setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener)
		{
			dialog.buttonBar.setVisibility(0);
			dialog.pos.setText(text);
			dialog.pos.setVisibility(0);
			dialog.pos.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						if(listener!=null)listener.onClick(null,-1);
						dialog.dismiss();
					}
				});
			return this;
		}
        public Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener)
		{
			setNegativeButton(ctx.getString(textId),listener);
			return this;
		}
        public Builder setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener)
		{
			dialog.buttonBar.setVisibility(0);
			dialog.neg.setText(text);
			dialog.neg.setVisibility(0);
			dialog.neg.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						if(listener!=null)listener.onClick(null,-1);
						dialog.dismiss();
					}
				});
			return this;
		}
        public Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener)
		{
			setNeutralButton(ctx.getString(textId),listener);
			return this;
		}
        public Builder setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener)
		{
			dialog.buttonBar.setVisibility(0);
			dialog.neu.setText(text);
			dialog.neu.setVisibility(0);
			dialog.neu.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						if(listener!=null)listener.onClick(null,-1);
						dialog.dismiss();
					}
				});
			return this;
		}
        public Builder setCancelable(boolean cancelable)
		{
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(!cancelable);
			return this;
		}
        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener)
		{
			dialog.setOnCancelListener(onCancelListener);
			return this;
		}
        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener)
		{
			dialog.setOnDismissListener(onDismissListener);
			return this;
		}
        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener)
		{
			dialog.setOnKeyListener(onKeyListener);
			return this;
		}
        public Builder setItems(int itemsId, DialogInterface.OnClickListener listener)
		{
			setItems(ctx.getResources().getStringArray(itemsId),listener);
			return this;
		}
        public Builder setItems(final CharSequence[] items, final DialogInterface.OnClickListener listener)
		{
			myListView m=dialog.list;
			m.setAdapter(new BaseAdapter(){
					@Override
					public int getCount()
					{
						return items.length;
					}
					@Override
					public Object getItem(int p1)
					{
						return null;
					}
					@Override
					public long getItemId(int p1)
					{
						return 0;
					}
					@Override
					public View getView(int p1, View p2, ViewGroup p3)
					{
						IViewHolder h=null;
						if(p2==null){
							myTextView t=new myTextView(ctx);
							p2=t;
							h=new IViewHolder();
							h.text=t;
							p2.setTag(h);
							int y=util.px(10);
							t.setPadding(y,y,y,y);
							t.setText(items[p1]);
						}
						else h=(myDialog.IViewHolder) p2.getTag();
						return p2;
					}
				});
			m.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						if(listener!=null)listener.onClick(null,p3);
						dialog.dismiss();
					}
				});
			m.setLayoutParams(new LayoutParams(-1,-2));
			setView(m);
			return this;
		}
        public Builder setAdapter(ListAdapter adapter, final DialogInterface.OnClickListener listener)
		{
			myListView m=dialog.list;
			m.setAdapter(adapter);
			m.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						if(listener!=null)listener.onClick(null,p3);
						dialog.dismiss();
					}
				});
			m.setLayoutParams(new LayoutParams(-1,-2));
			setView(m);
			return this;
		}
		public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener)
		{
			setMultiChoiceItems(ctx.getResources().getStringArray(itemsId),checkedItems,listener);
			return this;
		}
        public Builder setMultiChoiceItems(final CharSequence[] items, final boolean[] checkedItems, final DialogInterface.OnMultiChoiceClickListener listener)
		{
			myListView m=dialog.list;
			m.setAdapter(new BaseAdapter(){
					@Override
					public int getCount()
					{
						return items.length;
					}
					@Override
					public Object getItem(int p1)
					{
						return null;
					}
					@Override
					public long getItemId(int p1)
					{
						return 0;
					}
					@Override
					public View getView(int p1, View p2, ViewGroup p3)
					{	
						MViewHolder h=null;
						if(p2==null){
							LinearLayout l=new LinearLayout(ctx);
							l.setDescendantFocusability(myListView.FOCUS_BLOCK_DESCENDANTS);
							myTextView t=new myTextView(ctx);
							final myCheckBox r=new myCheckBox(ctx);
							r.setChecked(checkedItems[p1]);
							r.setClickable(false);
							int y=util.px(10);
							t.setPadding(y,y,y,y);
							t.setText(items[p1]);
							LayoutParams p=new LayoutParams(-1,-2);
							p.weight=1;
							l.addView(t,p);
							l.addView(r);
							l.setGravity(Gravity.CENTER);
							p2=l;
							h=new MViewHolder();
							h.text=t;
							h.lay=l;
							h.radio=r;
							p2.setTag(h);
						}
						else h=(myDialog.MViewHolder) p2.getTag();
						return p2;
					}
				});
			m.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						myCheckBox c=(myCheckBox)((ViewGroup)p2).getChildAt(1);
						c.setChecked(!c.isChecked());
						if(listener!=null)listener.onClick(null,p3,c.isChecked());
					}
				});
			m.setLayoutParams(new LayoutParams(-1,-2));
			setView(m);
			return this;
		}
		public Builder setSingleChoiceItems(int itemsId, int checkedItem, DialogInterface.OnClickListener listener)
		{
			setSingleChoiceItems(ctx.getResources().getStringArray(itemsId),checkedItem,listener);
			return this;
		}
        public Builder setSingleChoiceItems(final CharSequence[] items, final int checkedItem, final DialogInterface.OnClickListener listener)
		{
			myListView m=dialog.list;
			m.setAdapter(new BaseAdapter(){
					@Override
					public int getCount()
					{
						return items.length;
					}
					@Override
					public Object getItem(int p1)
					{
						return null;
					}
					@Override
					public long getItemId(int p1)
					{
						return 0;
					}
					@Override
					public View getView(int p1, View p2, ViewGroup p3)
					{	
						SViewHolder h=null;
						if(p2==null){
							LinearLayout l=new LinearLayout(ctx);
							l.setDescendantFocusability(myListView.FOCUS_BLOCK_DESCENDANTS);
							myTextView t=new myTextView(ctx);
							myRadioButton r=new myRadioButton(ctx);
							if(p1==checkedItem)r.setChecked(true);
							r.setClickable(false);
							int y=util.px(10);
							t.setPadding(y,y,y,y);
							t.setText(items[p1]);
							LayoutParams p=new LayoutParams(-1,-2);
							p.weight=1;
							l.addView(t,p);
							l.addView(r);
							l.setGravity(Gravity.CENTER);
							p2=l;
							h=new SViewHolder();
							h.text=t;
							h.lay=l;
							h.radio=r;
							p2.setTag(h);
						}
						else h=(myDialog.SViewHolder) p2.getTag();
						return p2;
					}
				});
			m.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						if(listener!=null)listener.onClick(null,p3);
						dialog.dismiss();
					}
				});
			m.setLayoutParams(new LayoutParams(-1,-2));
			setView(m);
			return this;
		}
        public Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener)
		{
			dialog.list.setOnItemSelectedListener(listener);
			return this;
		}
        public Builder setView(int layoutResId)
		{
			setView(LayoutInflater.from(ctx).inflate(layoutResId,null));
			return this;
		}
        public Builder setView(View view)
		{
			dialog.view.removeAllViews();
			dialog.view.addView(view);
			util.setWeight(view);
			return this;
		}
        public myDialog create()
		{
			dialog.create();
			return dialog;
		}
        public myDialog show()
		{
			dialog.show();
			return dialog;
		}
        public Builder(Context context)
		{
			this(context,0);
		}
        public Builder(Context context, int themeResId)
		{
			ctx=context;
			dialog=new myDialog(context);
		}

    }
	private static class SViewHolder{
		myTextView text;
		myRadioButton radio;
		LinearLayout lay;
	}
	private static class MViewHolder{
		myTextView text;
		myCheckBox radio;
		LinearLayout lay;
	}
	private static class IViewHolder{
		myTextView text;
	}
}
