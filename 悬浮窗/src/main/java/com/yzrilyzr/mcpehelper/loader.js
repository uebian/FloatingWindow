var ctx=com.mojang.minecraftpe.MainActivity.currentMainActivity.get();
var path=ctx.getPackageManager()
		.getPackageInfo("com.yzrilyzr.floatingwindow",
			android.content.pm.PackageInfo.INSTALL_LOCATION_AUTO)
		.applicationInfo.publicSourceDir;
var loader=new Packages.dalvik.system.PathClassLoader(path,ctx.getClass().getClassLoader());
var tob=loader.loadClass("com.yzrilyzr.mcpehelper.Main").getConstructors()[0];
tob=tob.newInstance([ctx,
new java.lang.reflect.InvocationHandler(){
	invoke:function(o, m, p) {
		var a="";
		for(var i in p){
			if(i<p.length-1){a+="p["+i+"],";}
			else {a+="p["+i+"]";}
		}
		return eval(o+"("+a+")")
	}
  }
 ]);
function newLevel(){
	tob.jsc("newLevel",[]);
}
function modTick(){
	tob.jsc("modTick",[]);
}