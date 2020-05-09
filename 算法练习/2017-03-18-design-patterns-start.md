---
layout: post
title:  设计模式开篇
date:   2017-03-18
categories: Design Pattern
tag: 设计模式
---
 

#### 设计模式 ####

设计模式是软件模式的一部分。软件模式即是软件开发的总体指导思路或参照样板。<br/>

软件模式包括：

- 设计模式
- 架构模式
- 分析模式
- 过程模式


#### 学习设计模式的层次 ####

- 能在白纸上画出所有的模式结构和时序图
- 能用代码实现；如果代码都没有写过，是用不出来的，即看得懂不会用
- 灵活运用至项目中

在线画图工具安利：[https://www.processon.com/diagrams](https://www.processon.com/diagrams)
winPC画图工具安利：[http://astah.net/](http://astah.net/)

#### 面向对象设计原则 ####

  | 名称 | 定 义 |使用频率|
 |:-------------:|:-----:|:-----:|
|单一职责原则 (Single Responsibility Principle, SRP)	|一个类只负责一个功能领域中的相应职责|	★★★★☆|
|开闭原则 (Open-Closed Principle, OCP)	|软件实体应对扩展开放，而对修改关闭	|★★★★★|
|里氏代换原则 (Liskov Substitution Principle, LSP)|	所有引用基类对象的地方能够透明地使用其子类的对象	|★★★★★|
|依赖倒转原则 (Dependence Inversion Principle, DIP)|	抽象不应该依赖于细节，细节应该依赖于抽象	|★★★★★|
|接口隔离原则 (Interface Segregation Principle, ISP)	|使用多个专门的接口，而不使用单一的总接口	|★★☆☆☆|
|合成复用原则 (Composite Reuse Principle, CRP)	|尽量使用对象组合，而不是继承来达到复用的目的	|★★★★☆|
|迪米特法则 (Law of Demeter, LoD)|	一个软件实体应当尽可能少地与其他实体发生相互作用	|★★★☆☆|

#### 设计模式分类 ####

##### 创建型模式(5) #####

创建型模式对类的实例化过程进行了抽象，能够将软件模块中对象的创建和对象的使用分离。为了使软件的结构更加清晰，外界对于这些对象只需要知道他们共同的接口，而不清楚其具体的实现细节，使整个系统的设计更加符合单一职责原则。

- [单例模式](http://xusx1024.com/2017/02/11/design-patterns-singleton-1/)
- [建造者模式](http://xusx1024.com/2017/03/03/design-patterns-builder-1/)
- [原型模式](http://xusx1024.com/2017/03/18/design-patterns-prototype-1/)
- [工厂方法模式](http://xusx1024.com/2017/05/24/design-patterns-factory-method/)
- [抽象工厂模式](http://xusx1024.com/2017/05/25/design-patterns-abstract-factory/)
 	
##### 行为型模式(11) #####

行为型模式是对在不同的对象之间划分责任和算法的抽象化。行为型模式不仅仅关注类和对象的结构，而且重点关注他们之间的相互作用。

- [策略模式](http://xusx1024.com/2017/05/25/design-patterns-strategy-pattern/)
- [模版方法模式](http://xusx1024.com/2017/06/19/design-patterns-template-method/)
- [观察者模式](http://xusx1024.com/2017/06/09/design-patterns-observer/)
- [迭代器模式](http://xusx1024.com/2017/06/15/design-patterns-iterator/)
- [责任链模式](http://xusx1024.com/2017/05/31/design-patterns-chain-of-responsibility/)
- [命令模式](http://xusx1024.com/2017/06/14/design-patterns-command/)
- [备忘录模式](http://xusx1024.com/2017/06/16/design-patterns-memento/)
- [状态模式](http://xusx1024.com/2017/05/26/design-patterns-state-pattern/)
- [访问者模式](http://xusx1024.com/2017/06/20/design-patterns-visitor/)
- [中介者模式](http://xusx1024.com/2017/06/13/design-patterns-mediator/)
- [解释器模式](http://xusx1024.com/2017/06/02/design-patterns-interpreter/)

##### 结构型模式(7) #####

描述如何将类或对象结合在一起形成更大的结构，就像搭积木，可以通过简单积木的组合形成复杂的、功能更为强大的结构。结构型模式可以分为类结构型模式和对象结构型模式：类结构型模式关心类的组合，一般只存在继承和实现。对象结构型模式关系类与对象的组合，通过关联关系使一个类中定义另一个类的实例对象。
根据“合成复用”原则，在系统中尽量使用关联关系来替代继承关系。

- [适配器模式](http://xusx1024.com/2017/06/21/design-patterns-adapter/)
- [桥接模式](http://xusx1024.com/2017/06/22/design-patterns-bridge/)
- [装饰模式](http://xusx1024.com/2017/06/23/design-patterns-decorator/)
- [外观模式](http://xusx1024.com/2017/06/26/design-patterns-facade/)
- [享元模式](http://xusx1024.com/2017/06/27/design-patterns-flyweight/)
- [代理模式](http://xusx1024.com/2017/06/28/design-patterns-proxy/)
- [组合模式](http://xusx1024.com/2017/06/26/design-patterns-composite/)
 
#### 参考 ####

- [图说设计模式](http://design-patterns.readthedocs.io/zh_CN/latest/)
- [设计模式](https://quanke.gitbooks.io/design-pattern-java/)
- 《Android源码设计模式解析与实战》
- [工匠若水](http://blog.csdn.net/yanbober/article/category/3148699)
- [卡奴达摩](http://blog.csdn.net/zhengzhb/article/category/926691)
- [特种兵—AK47](http://blog.csdn.net/column/details/loveyun.html)