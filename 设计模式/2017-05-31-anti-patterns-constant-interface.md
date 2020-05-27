---
layout: post
title:  接口常量反模式
date:   2017-05-31
categories: Design Pattern
tags: 设计模式
---
 
#### what ####

在应用中，我们往往需要一个常量文件，用于存储被多个地方引用的共享常量。一般我们都将其设计为静态的公共常量，有时放在接口中，有时放在类中。<br>

下面是两个例子：

	public interface IConstant {
	String NAME = "";
	int VALUE = 2;
	boolean IS_TEACHER = false;
	}	

<br>
	
	public class CConstant {
		public static final String NAME = "";
		public static final int VALUE = 2;
		public static final boolean IS_TEACHER = false;
	}

#### 接口的优点 ####

- 接口会自动将成员变量设置为static、final的
- 使得代码更加简单清晰
- 字节码文件相对于类来说更少，jvm加载和维护的成本变低
- jvm加载接口时，不必担心类提供的额外特征(如重载、方法的动态绑定等)，因此加载更快

#### 反模式之处 ####

由于Java中没有一种方式可以阻止类实现接口。合作开发中，一旦某个类实现了我们的常量接口，这就导致增加不必要的常量。这会动摇整个基础，并引起混乱。

#### 反反模式的做法 ####

final类 + 私有构造，如下：<br>
	
	public final class Constants {
		private Constants() {
		}
	
		public static final String NAME = "";
		public static final int VALUE = 2;
		public static final boolean IS_TEACHER = false;
	
	}
