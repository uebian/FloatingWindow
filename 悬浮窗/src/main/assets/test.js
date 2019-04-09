util.toast("c")
var ctx=ctx()
ctx.runOnUiThread(new Runnable(){
	run:function(){
		new Window(ctx,300,300).show();
	}
});
