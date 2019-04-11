util.toast("c")
var ctx=getCtx()
ctx.runOnUiThread(new java.lang.Runnable(){
	run:function(){
		new Window(ctx,300,300).show();
	}
});