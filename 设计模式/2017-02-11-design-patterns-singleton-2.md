---
layout: post
title:  单例设计模式(二)真●单例唯一$序列化
date:   2017-02-11
categories: Design Pattern
tag: 设计模式
---
 

#### 序列化对单例的破坏 ####

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

	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.ObjectInputStream;
	import java.io.ObjectOutputStream;

	public class SerializableDemo1 {
		public static void main(String[] args) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream("tempFile"));
				oos.writeObject(Singleton.getInstance());
				File file = new File("tempFile");
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
						file));
				Singleton instance = (Singleton) ois.readObject();
				System.out.println("反序列化后的对象和原来的是否相等？"
						+ (Singleton.getInstance() == instance));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

> 运行后： ``` 反序列化后的对象和原来的是否相等？false ```

> 通过对Singleton的序列化与反序列化得到的对象是```一个新的对象```，这就破坏了Singleton的单例性。


#### 序列化破坏单例の原因 ####

反序列化后，为什么是一个新的对象？我们从```ObjectInputStream$readObject()```方法追踪,<br/>
发现：``` Object obj = readObject0(false);```<br/>
继续：

	 case TC_OBJECT:
	                    return checkResolve(readOrdinaryObject(unshared));

探究： ```ObjectInputStream$readOrdinaryObject```

该方法关键代码片段一：

	 Object obj;
	        try {
	            obj = desc.isInstantiable() ? desc.newInstance() : null;
	        } catch (Exception ex) {
	            throw (IOException) new InvalidClassException(
	                desc.forClass().getName(),
	                "unable to create instance").initCause(ex);
	        }

> 该obj,就是我们反序列化要得到的对象，即readObject()返回的对象。<br/>
> ```isInstantiable```：如果一个Serializable/externalizable的类可以在运行时被实例化，那么该方法就返回true。
> ```desc.newInstance()```：该方法通过反射的方式调用无参构造方法新建一个对象

所以：

> 反序列化会通过反射调用无参的构造创建一个新的对象

该方法关键代码片段二：

	 if (obj != null &&
	            handles.lookupException(passHandle) == null &&
	            desc.hasReadResolveMethod())
	        {
	            Object rep = desc.invokeReadResolve(obj);
	            if (unshared && rep.getClass().isArray()) {
	                rep = cloneArray(rep);
	            }
	            if (rep != obj) {
	                handles.setObject(passHandle, obj = rep);
	            }
	        }

> ```hasReadResolveMethod```： 如果实现了Serializable或者externalizable接口的类中包含readResolve,则返回true<br/>
> ```invokeReadResolve```： 通过反射的方式调用要被反序列化的类的readResolve方法<br/>
> ```if (rep != obj) {
	                handles.setObject(passHandle, obj = rep);
	            }```：
>如果readResolve得到的对象和desc.newInstance()调用无参构造得到的对象不同，那么使用readResolve方法中返回的Object。

#### 结论一 ####

	由上可得，我们在自己的单例中增加readResolve方法，返回当前单例的实例，即可防止反序列化得到对象不一致问题。如下：

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

		private Object readResolve() {
			return singleton;
		}

	}
