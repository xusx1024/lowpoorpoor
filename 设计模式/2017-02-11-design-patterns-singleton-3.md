---
layout: post
title:  单例设计模式(三)真●单例唯一$反射
date:   2017-02-11
categories: Design Pattern
tag: 设计模式
---
 

#### 反射对单例的破坏 ####

先看一个DCL单例：

	import java.io.Serializable;
	
	/**
	 * double check lock 实现单例
	 */
	public class Singleton implements Serializable {
		private Singleton() {
		};
	
		public static volatile Singleton singleton;
	
		public static Singleton getInstance() {
			if (singleton == null) {
				synchronized (Singleton.class) {
					if (singleton == null) {
						singleton = new Singleton();
					}
				}
			}
			return singleton;
		}
	
	}

然后看我们的测试代码：
	
	
	import java.lang.reflect.Constructor;
	
	public class ReflectAttackSingleton {
	
		public static void main(String[] args) {
			try {
				Class<?> classType = Singleton.class;
				Constructor<?> c;
				c = classType.getDeclaredConstructor(null);
				c.setAccessible(true);
				Singleton instance1 = (Singleton) c.newInstance();
				Singleton instance2 = Singleton.getInstance();
				System.out.println("反射得到的实例与单例中的实例是否相等？" +(instance1 == instance2));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
	}

> 运行后： ``` 反射得到的实例与单例中的实例是否相等？false ```

> 通过反射获取构造函数，然后调用即可获取新的单例的实例，这就破坏了Singleton的单例性。


#### 修改构造器抵御攻击 ####
	
	private static boolean flag = false;
	private Singleton() {
		synchronized (Singleton.class) {
			if(flag == false)
				flag = !flag;
			else
				throw new RuntimeException("单例模式被攻击！");
		}
	};
 

#### 终极方法 ####
 	
	使用枚举单例