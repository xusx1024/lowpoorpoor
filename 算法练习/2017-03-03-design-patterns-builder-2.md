---
layout: post
title:  建造者设计模式(二) Android中的应用
date:   2017-03-03
categories: Design Pattern
tag: 设计模式
---


#### AlertDialog.Builder ####

如何应用窗口主题，各种参数合法性检查，上下文切换，窗口创建内部流程，我们都略过，只看其参数的构建即可。<br/>
那么AlertDialog中的参数，如何传递？<br/>

AlertDialog 中有一个AlertController的实例。<br/>
AlertController中有个AlertParams的静态类。<br/>
在AlertDialog中有一个静态类Builder，该Builder中持有AlertParams的一个实例。<br/>
Builder中的方法都把属性传入了AlertParams。<br/>
在Builder的create()方法，调用了AlertParams的applay，把参数传回了AlertController中。而AlertController的实例被AlertDialog持有。<br/>

示意图如下：

![示例图](/images/alertDialog.png)
#### Uri.Builder ####



Uri.Builder; 
Notification.Builder;
ContentProviderOperation.Builder;
 