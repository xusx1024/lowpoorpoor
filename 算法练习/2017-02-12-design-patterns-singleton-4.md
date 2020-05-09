---
layout: post
title:  单例设计模式(四)并不总是有效的readResolve
date:   2017-02-12
categories: Design Pattern
tag: 设计模式
---
 
#### what ####

在[《单例设计模式(二)真●单例唯一$序列化》](http://xusx1024.github.io/2017/02/11/design-patterns-singleton-2)中，我们已经分析了序列化和反序列化对单例类的实例唯一性的影响，原因以及解决方法。当我读《effective java》第十一章：序列化，发现readResolve的解决办法，也会有一些问题。

1.对于实现了Serializable接口的单实例类，只要反序列化就一定会调用readObject方法，产生一个不同于现VM中实例的新对象。

2.readResolve方法允许你用另一个实例去替代readObject方法创建的实例，如下：
	
	private Object readResolve() {
			return singleton;
		}

3.所以说，单例模式在序列化成字节码流后对反序列化根本没有用(被readResolve替换掉了)，所以，不需要将任何域序列化，所以单例中的所有实例域都应该被声明为transient。
事实上，如果依赖readResolve进行实例控制，带有对象引用类型的所有实例域都必须声明为transient。


4.因此我们的结论是ENUM最佳。但是如果必须编写可序列化的实例受控的类，它的实例在编译时还不知道，那就无法将类表示成一个枚举类型，因此readResolve进行实例控制也并不过时。


#### 题外话-readObject ####

是的，关于反序列时的攻击。

伪字节流的攻击法：对应的策略是提供一个readObject方法。
但是并没有完成足够的保护性拷贝。

	private void readObject(ObjectInputStream s) throws IOException,
	       ClassNotFoundException {
	    s.defaultReadObject();//先调用默认恢复
	    // 再进行状态参数的有效性验证
	    if (start.compareTo(end) > 0)
	       throw new InvalidObjectException(start + " after " + end);
	}

内部私有域盗用攻击法：对应的处理代码如下。

	private void readObject(ObjectInputStream s) throws IOException,
	       ClassNotFoundException {
	    s.defaultReadObject();//先调用默认恢复
	 
	    // 对可变组件进行保护性拷贝
	    start = new Date(start.getTime());
	    end = new Date(end.getTime());
	 
	    // 进一步检测内部状态参数是否有效
	    if (start.compareTo(end) > 0)
	       throw new InvalidObjectException(start + " after " + end);
	}

#### 鸣谢 ####

[代码迷](http://www.daimami.com/java-other/356857.htm)


