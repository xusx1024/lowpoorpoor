---
layout: post
title:  Tinker学习(二)标准java虚拟机动态加载代码示例
date:   2017-03-30
categories: Android 
tag: hotfix
---
 

虽说dalvik和标准jvm类加载机制不尽相同，但是此例对于理解我们将要学习的Tinker，感觉仍有益处。<br/>
 
自定义classLoader类：

	import java.io.ByteArrayOutputStream;
	import java.io.File;
	import java.io.FileInputStream;
	import java.nio.ByteBuffer;
	import java.nio.channels.FileChannel;
	
	public class MyClassLoader extends ClassLoader {
	
		@SuppressWarnings("deprecation")
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
	
			String classPath = MyClassLoader.class.getResource("/").getPath();
			String fileName = name.replace(".", "/") + ".class";
			File classFile = new File(classPath, fileName);
	
			if (!classFile.exists()) {
				throw new ClassNotFoundException(classFile.getPath() + " 不存在");
			}
	
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteBuffer bf = ByteBuffer.allocate(1024);
			FileInputStream fis = null;
			FileChannel fc = null;
	
			try {
				fis = new FileInputStream(classFile);
				fc = fis.getChannel();
				while (fc.read(bf) > 0) {
					bf.flip();
					bos.write(bf.array(), 0, bf.limit());
					bf.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					fc.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
	
			}
	
			return defineClass(bos.toByteArray(), 0, bos.toByteArray().length);
		}
	
	}

改变的类：
	
	public class Person {
	
		public void sayHello() {
			System.out.println("hello world!");
		}
	}


测试的类：
	 
	import java.lang.reflect.Method;
	
	public class StartUp {
	
		public static void main(String[] args) {
			int i = 0;
	
			while (true) {
				MyClassLoader mcl = new MyClassLoader();
				System.out.println(mcl.getParent());
				try {
					Class<?> personClass = mcl.findClass("Person");
	
					Object person = personClass.newInstance();
					Method sayHelloMethod = personClass.getMethod("sayHello");
					sayHelloMethod.invoke(person);
					System.out.println(i++);
	
					Thread.sleep(3000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


修改person类里的打印String，会发现我们的main方法里的线程，总是打印我们修改后的文字。