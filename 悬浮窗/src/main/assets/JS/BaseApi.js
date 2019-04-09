var _cbk=null;
function _setcbk(p){_cbk=p;}
function alert(s){
	_cbk.print("<Alert>"+s);
}
function _callMethod(str){
	_cbk.print(">"+str);
	eval(str);
	_cbk.print("执行完毕");
}
function ctx(){
	return _cbk.ctx();
}
function close(){}
function print(s){
	_cbk.print("<JS>"+s);
}
function clientMessage(s){
	_cbk.print("<客户端信息>"+s);
}
var util=com.yzrilyzr.myclass.util;
var Window=com.yzrilyzr.floatingwindow.Window;
var API=com.yzrilyzr.floatingwindow.API;
var UI=com.yzrilyzr.ui;
var ModPE={
	setItem:function(a,b,c,d,e){_cbk.print("设置物品,id:"+a+",名称:"+b+",damage:"+c+",贴图名:"+d+",可堆叠数:"+e);},
	setFoodItem:function(a,b,c,d,e,f){_cbk.print("设置食物物品,id:"+a+",名称:"+b+",damage:"+c+",恢复饥饿:"+d+",贴图名:"+e+",可堆叠数:"+f);},
}
var Block={
	defineBlock:function(a,b,c,d,e){_cbk.print("定义方块,id:"+a+",名称:"+b+",贴图:"+c+",原型id:"+d);},
	setDestroyTime:function(a,b){_cbk.print("设置方块摧毁时间,id:"+a+",时间:"+b);},
	setLightLevel:function(a,b){_cbk.print("设置方块发光亮度,id:"+a+",亮度:"+b);},
	setExplosionResistance:function(a,b){_cbk.print("设置方块爆炸抗性,id:"+a+",抗性:"+b);},
	setShape:function(a,b,c,d,e,f,g){}
}
function getPlayerEnt(){
	return null;
}
//设置合成
/*
Item.addShapedRecipe(物品id组[9],1,0,[" c "," cc","c  "],["c",物品id组[8],0]);
Item.addShapedRecipe(物品id组[4],1,0,["  r","dr ","sd "],["r",331,0,"s",280,0,"d",264,0]);
Item.addShapedRecipe(物品id组[5],1,0,["  c"," c ","d  "],["c",物品id组[8],0,"d",264,0]);
Item.addShapedRecipe(物品id组[6],1,0,["d i"," i ","s d"],["d",264,0,"i",332,0,"s",280,0]);
Item.addShapedRecipe(物品id组[7],1,0,["  b","bb ","sb "],["b",98,0,"s",369,0]);
Item.addShapedRecipe(物品id组[2],1,0,[" c "," c "," c "],["c",物品id组[8],0]);
Item.addShapedRecipe(物品id组[8],1,0,["dri","gcl","yhb"],["d",264,0,"r",331,0,"i",265,0,"g",266,0,"c",388,0,"l",351,4,"y",348,0,"h",406,0,"b",369,0]);
Item.addCraftRecipe(物品id组[1],1,0,[物品id组[0],1,0,物品id组[0],1,0,物品id组[0],1,0,物品id组[0],1,0,物品id组[0],1,0,物品id组[0],1,0,物品id组[0],1,0,物品id组[0],1,0,物品id组[0],1,0]);
Item.addCraftRecipe(物品id组[1],9,0,[255,1,0]);
Item.addFurnaceRecipe(物品id组[1],物品id组[3],0);
//添加到创造物品栏
Player.addItemCreativeInv(物品id组[0],1,0);
Player.addItemCreativeInv(物品id组[1],1,0);
Player.addItemCreativeInv(物品id组[2],1,0);
Player.addItemCreativeInv(物品id组[3],1,0);
Player.addItemCreativeInv(物品id组[4],1,0);
Player.addItemCreativeInv(物品id组[5],1,0);
Player.addItemCreativeInv(物品id组[6],1,0);
Player.addItemCreativeInv(物品id组[7],1,0);
Player.addItemCreativeInv(物品id组[8],1,0);
Player.addItemCreativeInv(物品id组[9],1,0);
Player.addItemCreativeInv(255,1,0);
Player.addItemCreativeInv(254,1,0);
Player.addItemCreativeInv(253,1,0);
Item.setHandEquipped(物品id组[2],true);
Item.setMaxDamage(物品id组[2],400);
Item.setHandEquipped(物品id组[4],true);
Item.setMaxDamage(物品id组[4],400);
Item.setHandEquipped(物品id组[5],true);
Item.setMaxDamage(物品id组[5],400);
Item.setHandEquipped(物品id组[6],true);
Item.setMaxDamage(物品id组[6],400);
Item.setHandEquipped(物品id组[7],true);
Item.setMaxDamage(物品id组[7],400);
Item.setHandEquipped(物品id组[9],true);
Item.setMaxDamage(物品id组[9],400);
//设置方块
Block.defineBlock(255,"假钻石块",[["diamond_block",0],["diamond_block",0],["diamond_block",0],["diamond_block",0],["diamond_block",0],["diamond_block",0]],1,false);
Block.setDestroyTime(255,1);
Block.setLightLevel(255,15);
Block.setExplosionResistance(255,100000);
*/
