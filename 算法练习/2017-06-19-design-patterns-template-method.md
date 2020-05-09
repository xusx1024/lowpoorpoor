---
title: 模版方法模式
date: 2017-06-19
tags: 设计模式
categories: Design Pattern
---

#### 定义 ####

模版方法模式：定义一个操作中算法的框架，而将一些步骤延迟到子类中。模版方法模式使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤。
  
#### 模式结构 ####

-  Abstract Template(抽象模版)：定义一个或多个抽象操作，以便让子类实现，这些抽象操作叫做基本操作，这是一个顶级逻辑的组成步骤。定义并实现一个模版方法，这个模版方法一般是一个具体方法，它给出了一个顶级逻辑的骨架，而逻辑的组成步骤在相应的抽象操作中，推迟到子类实现。顶级逻辑也有可能调用一些具体方法。
-  Concrete Template(具体模版)：实现父类定义的一个或多个抽象方法，可以有多个具体模版，同事可以有不同的实现，从而使顶级逻辑的实现各不相同。
 
![类图](/images/template_method_pattern_class_diagram.png)

#### 代码 ####
	
	package TemplateMethodPattern;
	
	/**
	 * 抽象模版
	 * @author sxx.xu
	 *
	 */
	public abstract class AbstractTemplate {

		/**
		 * 模版方法
		 */
		public void templateMethod(){
			abstractMethod();
			hookMethod();
			concreteMethod();
		}
		
		/**
		 * 子类必须实现
		 */
		protected abstract void abstractMethod();
		/**
		 * 钩子方法，子类可以实现
		 */
		protected void hookMethod(){}
		/**
		 * 子类不必关心
		 */
		private final void concreteMethod(){
			
		}
	}

 
********************

	package TemplateMethodPattern;
	/**
	 * 具体模版
	 * @author sxx.xu
	 *
	 */
	public class ConcreteTemplate extends AbstractTemplate {
	
		@Override
		protected void abstractMethod() {
			 
	
		}
	
		/**
		 * 具体模版可选择实现
		 */
		@Override
		protected void hookMethod() {
			super.hookMethod();
		}
	}

#### 分析 ####

该模式的方法可以分为两大类：模版方法和基本方法。

##### 模版方法 #####

- 定义在抽象类中，把基本操作方法组合在一起形成一个总算放或一个总行为的方法
- 可以有多个模版方法，每个模版方法都可以调用任意多个基本方法


##### 基本方法 #####

- 抽象方法：由抽象类声明，具体子类实现
- 具体方法：抽象类声明并实现，子类并不实现或置换
- 钩子方法：由抽象类声明并实现，子类可以扩展:
	- 通常该方法在抽象类中为空实现作为默认实现，叫做“Do Nothing Hook”
	- 命名应当以do开始，Java中。例如在HttpServlet类中，doGet(),doPost()


##### 优点 #####

- 父类形式化定义一个算法，而由它的子类来实现细节，子类在实现详细的处理算法时并不会改变算法中步骤的执行次序
- 一种代码复用技术，鼓励我们恰当使用继承来实现代码复用
- 可以实现一种反向控制结构，通过子类覆盖父类的钩子方法来决定某一特定步骤是否需要执行

##### 缺点 #####

-  如果父类中可变的基本方法过多，系统庞大，设计抽象，此时，可以配合桥接模式来进行设计。

 

