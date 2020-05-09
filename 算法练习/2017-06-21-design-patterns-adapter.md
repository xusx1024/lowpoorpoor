---
title: 适配器模式
date: 2017-06-21
tags: 设计模式
categories: Design Pattern
---

#### 定义 ####

适配器模式：将一个接口转换成客户希望的另一个接口，使接口不兼容的那些类可以一起工作，其别名为包装器(wrapper)。适配器模式有类的适配器模式和对象的适配器模式两种。
  
#### 模式结构 ####

##### 类适配器模式 #####

- Target：目标角色。所期待得到的接口。由于是类适配器模式，因此目标是接口不是类。
- Adapee：源角色。限制需要适配的接口。
- Adapter：适配器角色。适配器类是本模式的核心。适配器把源转换为目标。
 
![类图](/images/adapter_pattern_class_diagram_1.png)

##### 对象适配器模式 #####

![类图](/images/adapter_pattern_class_diagram_2.png)

#### 时序图 ####

![时序图](/images/adapter_pattern_sequence_diagram.png)

#### 代码 ####
	
##### 类适配器模式 #####

目标角色：
	package AdapterPattern;

	public interface Target {
	
		/**
		 * 源类{@link Adaptee}}中已存在
		 */
		void sampleOperation1();
	
		/**
		 * 源类{@link Adaptee}}中需要添加
		 */
		void sampleOperation2();
	
	}

源角色：
	
		package AdapterPattern;
	
	public class Adaptee {
		public void sampleOperation1() {
		}
	}

适配器角色：

	package AdapterPattern;
	
	public class Adapter extends Adaptee implements Target {
	
		/**
		 * 源类{@link Adaptee}}中没有，需要扩展的方法
		 */
		@Override
		public void sampleOperation2() {
			System.out.println("扩展方法");
		}
	
	}

##### 对象适配器模式 #####

源、目标角色都不变，只是在适配器类中，增加对源对引用，代码如下：
	
	package AdapterPattern;
	
	public class ObjectAdapter implements Target {
		Adaptee a;
	
		public ObjectAdapter(Adaptee a) {
			this.a = a;
		}
	
		@Override
		public void sampleOperation1() {
			a.sampleOperation1();
		}
	
		@Override
		public void sampleOperation2() {
			// 扩充方法
		}
	}



#### 分析 ####

- 通常，客户端通过目标类的接口访问目标提供的服务。有时，现有的类功能上可以满足，但是它所提供的接口不一定是客户类所期望的，这可能是因为现有类中方法名与目标类中定义的方法名不一致等原因所导致的。
- 适配器提供客户类需要的接口，把客户类的请求转化为对适配器的相应接口的调用。`也就是说，当客户类调用适配器的方法时，在适配器类的内部将调用适配者类的方法`，这个过程对客户类是透明的，客户类并不直接访问适配者类。

##### 优点 #####

- 将目标类和适配者类解耦，通过引入一个适配类来重用现有的适配者类，而无需修改原代码
- 增加类类的透明性和复用性
- 灵活性和扩展性都非常好，通过使用配置文件，可以很方便地更换/增加适配器
- 类适配器模式中，由于适配器类是适配者类的子类，因此可以在适配器类中置换一下适配者的方法，使得适配器更加灵活
- 对象适配器模式中，一个对象适配器可以把多个不同的适配者适配到同一个目标，也就是说，同一个适配器可以把适配者类和它的子类都适配到目标接口。

##### 缺点 #####

-  类适配器中，java、C#不支持多继承，一次最多只能适配一个适配者类，并且目标不能为具体类，使用有局限性，不能将一个适配者类和它的子类都适配到目标接口
-  对象适配器中，无法很好的置换适配者类的方法。如果一定要置换掉适配者类的一个或多个方法，就只好先做一个适配者类的子类，将适配者类的方法置换掉，然后再把适配者类的子类当做真正的适配者进行适配，实现过程较为复杂。
 
#### 扩展-缺省适配器 ####

简单理解：接口-抽象类-实现类
如果接口中定义的方法过多，可以先用一个抽象类，以空方法实现即`平庸实现`，然后具体实现类根据需要实现。
也叫做单接口适配器模式。

这个用的多一些。好像模版方法。




