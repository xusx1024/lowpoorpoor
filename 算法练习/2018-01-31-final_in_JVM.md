---
layout: post
title:  JVM中的final
date:   2018-01-31
categories: JAVA & kotlin
tag: [JVM]
---

最近用`kotlin`,配合`anko`,写Android非常舒服了.每次申请变量的时候,如果该变量在后续没有被重新赋值,而只是引用,`kotlin`此时会建议把变量标记为`val`的,即`final`的. 

#### 基础知识 ####

	final可以修饰类,属性和方法.
- 表示类不可继承
- 对于基本类型,值和引用都不可变;对于引用类型,引用不可变,引用指向的对象是可变的
- 对于方法,表示该方法不允许重写

#### final的使用场景 ####

一:工具类
	
	public final class Tools {
		private Tools() {
		}
	
		public static final void fun() {
		}
	}

同时在类和方法上使用了final,禁止此类的扩展,防止同事手误继承,防止反射回调.

二:同步相关
	
	public class Test {
		public static int VAL = 1;
	}


	public class Demo {
		void fun() {
			final int temp = Test.VAL;
		}
	}

Demo类中的方法,引用了Test中的静态变量.多线程环境下,使用final修饰局部变量,保证同一个方法中,拿到的值是一致的.

三:避免修改

一个局部变量,如果不希望被修改,把他定义为final的.可以借助编译器的帮助,检测被修改(误改)的状态.


#### final在JVM中 ####

在能够通过编译的前提下,无论局部变量声明时带不带final关键字修饰,对其访问的效率都一样.
重复访问一个局部变量比重复访问一个成员或静态变量快,即便将其final修饰符去掉,效果也一样.
	
如下代码:

	class Fish{
		static int foo() {
	  	final int a = someValueA();
	 	final int b = someValueB();
	  	return a + b;  
	}
	static int someValueA(){return 1;}
	static int someValueB(){return 2;}
	}

javac编译该类,使用`javap -c Fish`,查看其字节码文件,发现是一样的.
	
		 Code:
	    0: invokestatic  #2
	    3: istore_0
	    4: invokestatic  #3
	    7: istore_1
	    8: iload_0
	    9: iload_1
	   10: iadd
	   11: ireturn

所以,在局部变量需要计算得到结果的情况下,final的作用在编译阶段很小.


考虑另一种情况,局部变量指向常量的情况:

	class fish{
		static int foo() {
	    int a =1;
	    int b = 2;
	  	return a + b;  
		}
	}
	

字节码:

	static int foo();
    Code:
       0: iconst_1
       1: istore_0
       2: iconst_2
       3: istore_1
       4: iload_0
       5: iload_1
       6: iadd
       7: ireturn

--------------

	class fish{
		static int foo() {
  		final int a =1;
	   	final  int b = 2;
	  	return a + b;  
		}
	}

字节码:
	 static int foo();
	   Code:
	      0: iconst_3
	      1: ireturn

带有final的局部变量指向常量时,编译出的字节码,相当于如下代码的编译:
	
	static int foo3() {
	  return 5;
	}

在Java语言规范里,称为常量折叠.[详情](https://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.12.4).