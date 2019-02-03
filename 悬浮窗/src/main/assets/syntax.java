#引用关键字组方法:@[前缀和组名,分隔符]#
#组名前缀:C:类; F:域; M:方法; P:包; N:无;
#组名前必须加前缀 比如:Cname
#一个语法可以包含多个关键字组
#关键字组只能用在当前语法
#
#以下为Java的高亮语法示例
#
#语法:语法的名称
Syntax:Java
#十六进制颜色 正则表达式
ff80c0ff \b(@[Nclass,|]#|@[Cclass,|]#)\b
ff20a0ff \b(@[Nkey,|]#)\b
ffffa020 \b(@[Nvalue,|]#)\b
ffaaaaaa \b(@[Ppackage,|]#)\b
ff20ff20 //.*
ff20ff20 /\*[\s\S]*?\*/
ffffa020 (\"|').*?(\"|')
ffffa020 \b([0-9]+\.?[0-9]*(f|F|l|L|d|D)?)\b
ffffa020 \b(0x([0-9]|[A-F]|[a-f])+)\b
ffa0c0ff (\{|\}|\(|\)|;|,|\.|\=|\||&|!|\[|\]|<|>|\+|\-|/|\*|\?|\:|\^)
#关键字组:前缀和组名
KeyGroup:Nkey
#关键字……
abstract
assert
break
case
catch
class
continue
const
default
do
else
enum
extends
final
finally
for
goto
if
import
implements
instanceof
interface
native
new
package
private
protected
public
return
static
strictfp
super
switch
synchronized
this
throw
throws
transient
try
volatile
while
KeyGroup:Nclass
boolean
byte
char
double
float
int
long
short
void
KeyGroup:Nvalue
true
false
null
KeyGroup:Cclass
String
ArrayList
StringBuilder
AlertDialog
ClipboardManager
Context
Canvas
Path
Paint
HashMap
Override
Activity
File
InputStream
OutputStream
DataInputStream
DataOutputStream
InputStreamReader
FileInputStream
FileOutputStream
FileReader
FileWriter
System
RandomAccessFile
Environment
View
Button
SeekBar
ImageView
TextView
EditText
ImageButton
Switch
ProgressBar
AdapterView
BaseAdapter
SimpleAdapter
ListView
Spinner
LinearLayout
RelativeLayout
FrameLayout
ScrollView
HorizontalScrollView
ByteArrayOutputStream
Exception
IOException
PrintWriter
DialogInterface
Intent
Bundle
KeyEvent
MotionEvent
SurfaceView
CopyOnWriteArrayList
Arrays
Comparator
KeyGroup:Ppackage
android
app
io
view
widget
text
util
content
graphics
java
lang
os
concurrent
nio
Syntax:JavaScript
ff20a0ff \b(@[Nkey,|]#)\b
ffffa020 \b(@[Nvalue,|]#)\b
ff20ff20 //.*
ff20ff20 /\*[\s\S]*?\*/
ffffa020 (\"|').*?(\"|')
ffffa020 \b([0-9]+\.?[0-9]*(f|F|l|L|d|D)?)\b
ffffa020 \b(0x([0-9]|[A-F]|[a-f])+)\b
ffa0c0ff (\{|\}|\(|\)|;|,|\.|\=|\||&|!|\[|\]|<|>|\+|\-|/|\*|\?|\:|\^)
KeyGroup:Nkey
var
function
prototype
break
case
catch
continue
const
default
do
else
finally
for
if
instanceof
new
return
switch
this
throw
try
while
KeyGroup:Nvalue
true
false
null
