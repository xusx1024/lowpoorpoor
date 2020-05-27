---
layout: post
title:  单例设计模式(一)基础知识
date:   2017-02-11
categories: Design Pattern
tag: 设计模式
---

#### what ####

##### 来自wiki #####

>[单例模式](https://zh.wikipedia.org/wiki/%E5%8D%95%E4%BE%8B%E6%A8%A1%E5%BC%8F)又叫单子模式，是一种常用的软件设计模式。在应用这个模式时，单例对象的类必须保证只有一个实例存在。许多时候整个系统只需要拥有一个全局对象，这样有利于我们协调系统整体的行为。
比如：在某个服务器程序中，该服务器的配置信息存放在一个文件中，这些配置数据由一个单例对象统一读取，然后服务进程中的其他对象再通过这个单例对象获取这些配置信息。这种方式简化了在复杂环境下的配置管理。

##### 来自IBM #####

>与wiki相重复的内容，不再摘录。
[单例模式](http://www.ibm.com/developerworks/cn/java/j-lo-Singleton/)是一种对象创建模式，它用于产生一个对象的具体实例，它可以确保系统中一个类只产生一个实例。Java里面实现单例是一个虚拟机的范围，因为装载类的功能是由虚拟机做的，所以一个虚拟机在通过自己的ClassLoader装载实现单例类的时候就会创建一个类实例。在Java语言中，这样的行为有两大好处：
1.对于频繁使用的对象，可以省略创建对象所话费的时间，这对于那些重量级对象而言，是非常可观的一笔系统开销；
2.由于new操作的次数减少，因而对系统内存的使用频率降低，这将减轻GC压力，缩短GC停顿时间。 

#### how ####

##### 饿汉单例 #####
	public class Singleton {
	 private Singleton(){}
	 private static Singleton instance = new Singleton();
	 public static Singleton getInsatnce(){
	 return instance;
	 }
	}

> 上述代码不足之处是无法对instance实例做延时加载，假如单例的创建过程很慢，而由于instance成员变量是static定义的，因此在JVM加载单例类时，单例对象就会被建立，如果此时这个单例类在系统中还扮演其他角色，那么在任何使用这个单例类的地方都会初始化这个单例变量，而不管是否会用到。

##### 懒汉单例 #####

	public class LazySingleton {
	
		private LazySingleton() {
		}
	
		public static LazySingleton instance = null;
	
		public static synchronized LazySingleton getInstance() {
			if (instance == null) {
				instance = new LazySingleton();
			}
			return instance;
		}
	}

> 上述代码首先对于静态成员变量instance初始化复赋值为null，确保系统启动时没有额外的负载；其次在getInstance()工厂方法中判断单例是否已经存在；注意，我们的getInstance()方法中添加了synchronized关键字，这就是注意事项中，在多线程情况下保证单例对象唯一性的手段。因此每次调用都要进行同步，造成不必要的开销，明显增加多线程环境下耗时。<br/>

> 在多线程的应用场合下。如果当唯一实例尚未创建时，有两个线程同时调用创建方法，那么它们同时没有检测到唯一实例的存在，从而同时各自创建了一个实例，这样就有两个实例被构造出来，解决该问题的办法是为指示类是否已经实例化的变量提供一个互斥锁(会降低效率)。

##### 懒汉单例的改进：静态内部类单例 #####

	public class StaticInnerClassSingleton {
	
		private StaticInnerClassSingleton() {
		}
	
		public StaticInnerClassSingleton getInstance() {
			return SingletonHolder.instance;
		}
	
		private static class SingletonHolder {
			private static final StaticInnerClassSingleton instance = new StaticInnerClassSingleton();
		}
	}

> 使用内部类来维护单例的实例，第一次加载StaticInnerClassSingleton类时，不会初始化instance，只有在第一次调用getInstance()时，才会加载SingletonHolder，初始化instance。由于实例的建立是在类加载时完成，不仅可以确保线程安全，也无需使用synchronized关键字，因此推荐使用。

##### DoubleCheckLock 实现单例 #####
	
	public class DCLSingleton {
	
		private static DCLSingleton instance = null;
	
		private DCLSingleton() {
		}
	
		public DCLSingleton getInstance() {
			if (instance == null) {
				synchronized (DCLSingleton.class) {
					if (instance == null) {
						instance = new DCLSingleton();
					}
	
				}
			}
			return instance;
		}
	}

> 同步关键字加载方法上，如懒汉式单例，增加系统开销，影响效率。上述代码，第一次判空，可以避免不必要的同步，第二次判空是避免多线程情况下，instance实例为空，DCL失效。<br/>因为
> ```instance = new DCLSingleton();```
>并非原子操作，这句代码最终会被编译成多条汇编命令，大致做了三件事：<br/>
>1.给DCLSingleton的实例分配内存<br/>
>2.调用DCLSingleton的构造，初始化成员字段<br/>
>3.将instance对象指向分配的内存空间<br/>


>由于Java编译器允许处理器乱序执行，以及JDK1.5之前的JMM(java memory model即Java内存模型)中*Cache、寄存器到主内存回写顺序*的规定，上面2、3的顺序无法保证先后执行的。<br/>如果线程A的3执行完，此时instance不为null，2却没有执行，被切换到线程B上，B可以直接取走instance，使用就会报错，这就是DCL失效。<br/>
>解决：JDK1.5之后，```private static volatile DCLSingleton instance = null;``` 
>使用volatile关键字，可以保证instance对象*每次都是从主内存中读取*。

##### 容器维护多个单例 #####


	import java.util.HashMap;
	import java.util.Map;
	
	public class SingletonManager {
		private static Map<String,Object> objMap = new HashMap<>();
		
		private SingletonManager(){}
		
		public static void registerService(String key,Object instance){
			if(!objMap.containsKey(key)){
				objMap.put(key, instance);
			}
		}
		
		public static Object getService(String key){
			return objMap.get(key);
		}
	
	}

> 根据key,使用map管理多种类型的单例，在使用时可以统一进行获取操作，可降低用户的使用成本。(个人感觉没啥用)
 
##### 枚举单例 #####

	public enum EnumSingleton {
	
		INSTANCE;
	
		public void doSomething() {
		}
	}

> 写法简单，线程安全，在任何情况下都是单例！


















