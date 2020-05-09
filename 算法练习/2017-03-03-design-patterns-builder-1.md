---
layout: post
title:  建造者设计模式(一) 基础知识
date:   2017-03-03
categories: Design Pattern
tag: 设计模式
---
 

#### what ####

一步一步创建一个复杂对象的创建型模式，它允许用户在不知道内部构建细节的情况下，可以更精细地控制对象的构造流程。
<br/>

将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。

#### 使用场景 ####

1. 相同的方法，不同的执行顺序，产生不同的事件结果时；
2. 多个部件或零件，都可以装配到一个对象中，但是产生的运行结果又不相同时；
3. 当初始化一个对象特别复杂，如参数多，且很多参数都具有默认值。

记住一句话:`遇到多个构造器参数时要考虑用构建器。`


#### 为什么是builder模式 ####

在创建一个对象时，如果有多个构造器参数，可选的方式有以下几种：

1. 重叠构造器-telescoping constructor
2. JavaBeans模式
3. 构造器模式

##### 重叠构造器 #####

- 随着参数数目的增加，很难控制
- 代码很难编写，难以阅读
- 如果参数的类型相同，那么调用时如果不小心颠倒了其中的俩。。
	
		
		/**
		 * 重叠构造器Demo
		 * 
		 * @author Administrator
		 * 
		 */
		public class TeleScopDemo {
		
			private final int param1;
			private final int param2;
			private final int param3;
			private final int param4;
			private final int param5;
		
			public TeleScopDemo(int param1) {
				this(param1, 0);
			}
		
			public TeleScopDemo(int param1, int param2) {
				this(param1, param2, 0);
			}
		
			public TeleScopDemo(int param1, int param2, int param3) {
				this(param1, param2, param3, 0);
			}
		
			public TeleScopDemo(int param1, int param2, int param3, int param4) {
				this(param1, param2, param3, param4, 0);
			}
		
			public TeleScopDemo(int param1, int param2, int param3, int param4,
					int param5) {
				this.param1 = param1;
				this.param2 = param2;
				this.param3 = param3;
				this.param4 = param4;
				this.param5 = param5;
			}
		
		}

##### JavaBeans #####

由于其构造过程被分到了几个调用中，在构造过程中可能处于不一致的状态。需要同步来保证其线程安全。 
		
		/**
		 * JavaBeans模式Demo
		 * 
		 * @author Administrator
		 * 
		 */
		public class JavaBeansDemo {
		
			private int param1;
			private int param2;
			private int param3;
			private int param4;
			private int param5;
		
			public JavaBeansDemo() {
			}
		
			public void setParam1(int param1) {
				this.param1 = param1;
			}
		
			public void setParam2(int param2) {
				this.param2 = param2;
			}
		
			public void setParam3(int param3) {
				this.param3 = param3;
			}
		
			public void setParam4(int param4) {
				this.param4 = param4;
			}
		
			public void setParam5(int param5) {
				this.param5 = param5;
			}
		
		}



##### Builder模式 #####
		
既有重叠构造器的安全性，也有JavaBeans的可读性。
 
		/**
		 * 构建器模式Demo
		 * 
		 * @author Administrator
		 * 
		 */
		public class BuilderPatternDemo {
		
			private final int param1;
			private final int param2;
			private final int param3;
			private final int param4;
			private final int param5;
		
			private BuilderPatternDemo(Builder builder) {
				param1 = builder.param1;
				param2 = builder.param2;
				param3 = builder.param3;
				param4 = builder.param4;
				param5 = builder.param5;
			}
		
			public static class Builder {
				// 必需的参数
				private final int param1;
		
				// 可选的参数，带有初始值
				private int param2 = 0;
				private int param3 = 0;
				private int param4 = 0;
				private int param5 = 0;
		
				public Builder(int param1) {
					this.param1 = param1;
				}
		
				public Builder param2(int val) {
					param2 = val;
					return this;
				}
		
				public Builder param3(int val) {
					param3 = val;
					return this;
				}
		
				public Builder param4(int val) {
					param4 = val;
					return this;
				}
		
				public Builder param5(int val) {
					param5 = val;
					return this;
				}
		
				public BuilderPatternDemo build(){
					return new BuilderPatternDemo(this);
				}
			}
		}

	