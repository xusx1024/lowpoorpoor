---
title: 访问者模式
date: 2017-06-20
tags: 设计模式
categories: Design Pattern
---

#### 定义 ####

访问者模式(Visitor Pattern):提供一个作用于某对象结构中的各元素的操作表示，它使我们可以在不改变各元素的类的前提下定义作用于这些元素的新操作。访问模式是一种对象行为型模式。
  
#### 模式结构 ####

- Visitor：抽象访问者，声明了一个或者多个方法操作，形成所有的具体访问者角色必须实现的接口
- ConcreteVisitor：具体访问者，实现抽象访问者所声明的接口，也就是抽象访问者所声明的各个访问操作
- Node：抽象节点，声明一个接受操作，接受一个访问者对象作为一个参数
- ConcreteNode：具体节点，实现了抽象节点所规定的接受操作
- ObjectStructure：结构对象，可以遍历结构中的所有元素，如果需要，提供一个高层次的接口让访问者对象可以访问每一个元素；如果需要，可以设计成一个复合对象或者一个聚集，如List或Set
 
![类图](/images/visitor_pattern_class_diagram.png)

#### 时序图 ####

![时序图](/images/visitor_pattern_sequence_diagram.png)

#### 代码 ####
	
[GitHub](https://github.com/xusx1024/DesignPatternDemoCode/tree/master/VisitorPattern)

#### 分析 ####

个人理解，这是个多对多的关系。每个访问者可以封装固定的操作去访问所有的节点。每个节点都有被访问时相应调用的方法。
对象构造管理访问者和节点
客户端传给对象构造不同的访问者，然后得到数据。
客户端只需要关心自己提供对应的访问者，可以使用工厂生产，不必关心具体细节。

##### 优点 #####

- 增加新的访问操作很方便
- 将访问行为集中到访问者中，而不是分散在一个个元素类，职责明确
- 让用户在不修改现有元素类层次结构的情况下，定义作用于该层次结构的操作

##### 缺点 #####

-  增加新的元素节点很困难。每一次增加，都要在抽象访问者中增加新的抽象操作。
-  访问者模式要求访问者对象访问并调用每一个元素对象的操作，这意味着元素对象需要暴露自己的内部操作和内部状态，否则访问者无法访问
 
#### 分派 ####

变量被声明时的类型叫做变量的静态类型，有些人又把静态类型叫做明显类型；而变量所引用的对象的真实类型又叫做变量的实际类型。

	List list = new ArrayList();
 
根据对象的类型而对方法进行的选择，就是分派，分派又分为两种：静态分派、动态分派。

##### 静态分派 #####

发生在编译时期，分派根据静态类型信息发生，`方法重载`就是静态分派。
	
	package VisitorPattern;
	
	public class People {
	
		void ride(Horse h) {
			System.out.println("ride horse");
		}
	
		void ride(WhiteHorse h) {
			System.out.println("ride White Horse");
		}
	
		void ride(BlackHorse h) {
			System.out.println("ride Black Horse");
		}
	
		public static void main(String[] args) {
			Horse wh = new WhiteHorse();
			Horse bh = new BlackHorse();
	
			People p = new People();
			p.ride(wh);
			p.ride(bh);
		}
	
	}
	
	class Horse {
	}
	
	class WhiteHorse extends Horse {
	}
	
	class BlackHorse extends Horse {
	}

>打印结果：
>ride horse
ride horse

两次对ride()方法传入对是不同对参数，虽然具有不同的真实类型，但是它们的静态类型都是一样的，重载方法的分派是根据静态类型进行的，这个分派过程在编译时期就完成了。

##### 动态分派 #####

发生在运行时期，动态分派动态地置换掉某个方法。java通过重写支持动态分派。
	
	package VisitorPattern;
	
	public class Demo {
	
		public static void main(String[] args) {
			Horse1 h = new WhiteHorse1();
			h.eat();
		}
	
	}
	
	class Horse1 {
		public void eat() {
			System.out.println("horse eatting");
		}
	}
	
	class WhiteHorse1 extends Horse1 {
		@Override
		public void eat() {
			System.out.println("WhiteHorse1 horse eatting");
		}
	}
	
	class BlackHorse1 extends Horse1 {
		@Override
		public void eat() {
			System.out.println("BlackHorse1 horse eatting");
		}
	}

>打印结果：
>WhiteHorse1 horse eatting

Java编译器在编译时期并不总是知道哪些代码会被执行，因为编译器仅仅知道对象的静态类型，而不知道对象的真实类型；而方法的调用则是根据对象的真实类型，而不是静态类型。

#### 宗量 ####

一个方法所属的对象叫做方法的接收者；
方法的接收者与方法的参数统称为方法的宗量。

根据分派可以基于多少种宗量，可以将面向对象的语言划分为单分派语言和多分派语言。单分派语言根据一个宗量的类型进行对方法的选择，多分派语言根据多于一个的宗量的类型对方法进行选择。

Java是动态的单分派语言，因为动态分派仅仅会考虑到方法的接收者类型。
Java又是静态的多分派语言，因为他对重载方法的分派会考虑到方法的接收者类型以及方法的所有参数的类型。

在一个支持动态单分派的语言里面，有两个条件决定了一个请求会调用哪一个操作：一是请求的名字，二是接收者的真实类型。单分派限制了方法的选择过程，使得只有一个宗量可以被考虑到，这个宗量通常就是方法的接收者。在Java语言里面，如果一个操作是作用于某个类型不明的对象上面，那么对这个对象的真实类型测试仅会发生一次，这就是动态的单分派的特征。

