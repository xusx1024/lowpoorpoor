---
layout: post
title:  kotlin-扩展函数
date:   2017-12-26
categories: JAVA & kotlin
tag: 杂项
---

#### 问题引出 ####

在调用一个对象的方法的时候,IDE自动提示了如下的方法:(请看图中白色的方法)
![1-1](/images/kotlin_extension_fun.png)

#### 追踪 ####

	文件位置`org\jetbrains\kotlin\kotlin-stdlib\1.1.51\kotlin-stdlib-1.1.51.jar!\kotlin\NotImplementedError.class`

	打开方法所在的文件,第一行:

	@file:kotlin.jvm.JvmMultifileClass // 包名相同,类名也相同,或者有相同的@JvmName注解,会出错,使用此注解
	@file:kotlin.jvm.JvmName("StandardKt") // 使用注解,修改生成的Java类的类名

引申:
	在Java中,如果需要把kotlin属性作为字段暴露,需要使用@JvmField注解标注.[更多Java和kotlin互相调用](https://www.kotlincn.net/docs/reference/java-to-kotlin-interop.html)

最后发现这几个函数叫做扩展函数.官方api:[链接](https://www.kotlincn.net/docs/reference/extensions.html)

- 能够扩展一个类的新功能而无需继承该类或使用像装饰者这样的设计模式.
- 扩展函数不能真正的修改他们所扩展的类.通过定义一个扩展,你并没有在一个类中插入新成员,仅仅是可以通过该类型的变量用点表达式去调用该函数
- 图片上的是官方提供的扩展函数,我们也可以自己扩展,参考链接中的注意事项.

#### 官方扩展函数 ####

###### let函数 #####

	let默认当前这个对象作为闭包的it参数，返回值是函数里面最后一行，或者指定return

- 闭包
	- 外部变量 + 函数 组成一个闭包
	- 通常实现方式为函数内定义另外一个函数
	- 内部函数可以引用外部函数的变量和参数
	- 参数和变量不会被垃圾回收器回收
	- 作用:
		- 隐藏关键的变量,把一个全局变量写成局部变量
		- 
- it参数,一个方法如果只有一个参数,可以用it代替 
 

###### apply函数 #####

	调用某对象的apply函数,在apply里可以调用该对象的任意方法,并返回该对象,如果let指定return it,两个一样的.
	
	
buıxbuıɥs‾

 
 