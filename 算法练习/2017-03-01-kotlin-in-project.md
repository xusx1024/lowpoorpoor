---
layout: post
title:  kotlin在实际使用中的一些问题解决
date:   2017-03-01
categories: JAVA & kotlin
tag: 杂项
---
 

#### 引起build false ####

##### 提示信息 #####
>Proguard Warning: org.jetbrains.anko.internals.AnkoInternals: can't find referenced method 'int getThemeResId()' in library class android.view.ContextThemeWrapper

##### 原因&解决 #####

>原因：This call only works with android.support.v7.view.ContextThemeWrapper because in system framework base ContextThemeWrapper is hide this call and the existence of getThemeResId not guaranteed.


>解决：compile 'org.jetbrains.anko:anko-common:0.9.1' 


注意，只要不使用0.9就可以，比如使用0.8.3也是可用的。[传送](https://github.com/Kotlin/anko/issues/206)。

<br/>
<br/>


#### fragment中没有使用findViewById，报null异常 ####

##### 提示信息 #####

>Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'android.view.View android.view.View.findViewById(int)' on a null object reference

##### 原因&解决 #####

>The problem is that you are accessing it too soon. getView() returns null in onCreateView. Try doing it in the onViewCreated method.

[传送。](http://stackoverflow.com/questions/34541650/kotlin-android-extensions-and-fragments)

#### companion object ####

companion object只能在类中使用，相当于java中的静态内部类（kotlin没有static关键字）.

#### 对一个可能为null对象的处理 ####

- surround with null check: `if (item != null)`
- Add non-null asserted:`obj!!`
- replace with safe  (?.) call:`obj?`

对象后跟一个问号，表明其可能为空，如果跟两个感叹号，表明确定该对象不为空。


#### kotlin中的操作符重载 ####

[官方传送](https://kotlinlang.org/docs/reference/basic-types.html) <br/>
[优秀blog传送](http://blog.csdn.net/io_field/article/details/52817471)

要用到的是java中的&操作，在kotlin中是and方法。

#### 一个对象在改变属性时，可能为null的提示 ####  

>Smart cast to 'Drawable' is impossible , because 'goldSwBean' is a mutable property that could have been change by this time

在每一步build的时候，都加上确认对象不会为空的判定,如下：<br/>
Add non-null asserted:`obj!!`

#### instanceof  ####

instanceof 使用is替代了  

#### 类型强制转换  ####

使用as。` (view as TextView)`

#### 集合操作  ####

[官方传送](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)<br/>
[民间传送](http://www.cnblogs.com/figozhg/p/5031398.html)<br/>
很多封装，方便。<br/>
注意`map``foreach`，好用的。

#### swithc-case的default ####
 
已知switch-case被when取代了，那么default是啥？如下：
	
	when (x) {
	    1 -> print("x == 1")
	    2 -> print("x == 2")
	    else -> { // Note the block
	        print("x is neither 1 nor 2")
	    }
	}


#### adapter中的getView问题 ####

>Parameter specified as non-null is null

语义比较明白：检测到非空参数为空。
<br/>
 
事实上，在getView中，该参数没有用到，不需要关系是否为空，因此，在该变量后加?，表示其可以为空即可。

[kotlin空安全扩展](http://kotlinlang.org/docs/reference/null-safety.html)

#### name  shadowed 警告 ####

>Any scope can shadow a variable used in another scope. It is NOT good practice and does produce a compiler warning -- but it is allowed.

#### 开启子线程 ####

如果需要UI线程之外做些事情：
	
	 doAsync {
                Thread.sleep(1000)
                uiThread {  }//回到UI线程
	          }


#### xml or text declaration not at start ####

这个是kotlin无关的问题，仅做记录:

`<?xml version="1.0" encoding="utf-8"?>`

这行代码在xml中，不要多不要少就要一行，放在首行。

#### open class ####

Kotlin的类默认是final的，也就是不可继承的，如果让类可继承，使用open关键字。当类有abstract关键字时，不需要使用open了。

#### return null ####
有时候需要返回某个对象的实例，如果不存在就返回空。在要返回的类型后追加“？”eg：

	fun method():String ? {
            return null
        }

#### 可变参数使用关键字：vararg ####

    fun demo(vararg texts : String){

     }

#### 究极快速方法 ####
如果有的kotlin实现方式不知道，可以先写java的代码，然后copy、paste进入kotlin文件，AndroidStudio有插件即可自动转换。

#### Unresolved reference: rem ####
1.1版本导致，1.1以前的版本，尽管没有此方法，依然会提示。<br/>
1.1把mod改成了rem但是导致了版本错误。建议使用mod+@Suppress("DEPRECATION")

#### for 循环 ####
##### 循环遍历对象 ####
	for (item in collection) {
    print(item)
}

##### 循环计数 #####
	for (index in 0..viewGroup.getChildCount() - 1) {
    val view = viewGroup.getChildAt(index)
    view.visibility = View.VISIBLE
}

##### 循环遍历对象数组 #####

	for (i in array.indices)
    print(array[i])

#### 延时执行任务 ####
java<br/>

	 mHandler.postDelayed(new Runnable() {
	                    @Override
	                    public void run() {
	                       //do something
	                    }
	                }, 3000);

kotlin<br/>

	mHandler.postDelayed(Runnable { //do something }, 3000)

#### lambda ####

##### android提示框 #####
	
	builder.setPositiveButton("同意", object : DialogInterface.OnClickListener {
	            override fun onClick(dialog: DialogInterface?, which: Int) {
	
	            }
	
	        })
    builder.setPositiveButton("同意") { dialog, which ->
        
    }

#### 覆盖父类的方法 ####

>标记为 override 的成员是 open的，它可以在子类中被复写。如果你不想被重写就要加 final

#### 构造函数 ####

一级构造：

	class Person constructor(firstName: String) {
	}
	或
	class Person(firstName: String){
	}	

二级构造：

	class Person {
    constructor(parent: Person) {
        parent.children.add(this)
    }	
	}	

私有构造：

	class DontCreateMe private constructor () {
	}

类初始化操作：

	class Customer(name: String) {
    init {
        logger,info("Customer initialized with value ${name}")
    }
	}

#### 接口 ####


	interface MyInterface {
	    var name:String //name 属性, 抽象的
	    fun bar()
	    fun foo() {
	        // 可选的方法体
	        println("foo")
	    }
	}
	class Child : MyInterface {
	    override var name: String = "runoob" //重载属性
	    override fun bar() {
	        // 方法体
	        println("bar")
	    }
	}
	fun main(args: Array<String>) {
	    val c =  Child()
	    c.foo();
	    c.bar();
	    println(c.name)
	 
	}

`update @ 2017年7月14日18:51:09`

#### list 如何添加元素 ####

声明list，则使用plus
声明ArrayList，则使用add

#### 可见性修饰词 ####

- 默认public
- private
- private
- internal
- protected