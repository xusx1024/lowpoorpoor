---
layout: post
title:  Android学习路线图
date:   2017-04-01
categories: Android
tag: android
---



#### 程序设计 ####


|  知识点  ||链接或书籍|进阶|
| :------ | :------ | :------ | :------ |
|__java__|基本语法(如继承、异常、引用、泛型等)|[Java核心技术 卷I](https://book.douban.com/subject/25762168/)（适合入门）|[Effective Java中文版](https://book.douban.com/subject/3360807/)（如何写好的Java代码）<br>[Java解惑](https://book.douban.com/subject/5362860/)（介绍烂Java代码是什么样的）
|  | 多线程、并发 |[Java并发编程实战](https://book.douban.com/subject/10484692/)<br>（系统全面的介绍了Java的并发，如何设计支持并发的数据结构）|  |
||Java 7|[Java程序员修炼之道](https://book.douban.com/subject/24841235/) （详细的介绍Java 7 的新特性）||
||Java 8|[写给大忙人看的Java SE 8](https://book.douban.com/subject/26274206/)<br>[函数式编程思维](https://book.douban.com/subject/26587213/)||
||Java虚拟机|[深入理解Java虚拟机](https://book.douban.com/subject/24722612/) （并不是那么难，Java程序员都该看看）||
||性能优化|[Java性能优化权威指南](https://book.douban.com/subject/25828043/) （后面的章节好像用处不大，前面有些部分还是值得看）||
|__算法与数据结构__|算法时间复杂度、空间复杂度的基本认知；<br> 熟悉常用数据结构：链表、队列、散列表、树等；<br>递归、分支等基本思想；<br>常用算法应用：排序、查找、比较等<br>|[数据结构与算法分析](https://book.douban.com/subject/1139426/) （涵盖面比较全、示例是Java语言）<br>[算法设计与分析基础](https://book.douban.com/subject/26337727/)（实用主义的典型、偏算法设计<br>[编程珠玑](https://book.douban.com/subject/3227098/)（实践型算法数据||
|__操作系统__|对Linux/OS的基本认知<br>Linux的常用命令|[鸟哥的Linux私房菜](https://book.douban.com/subject/4889838/)<br>[Linux内核设计与实现(原书第3版)](https://book.douban.com/subject/6097773/)（很精炼的语言描述清楚了内核算法）||
|__网络__|Http/Https<br>TCP/IP|[图解HTTP](https://book.douban.com/subject/25863515/)<br>[图解TCP/IP](https://book.douban.com/subject/24737674/)|[TCP/IP详解](https://book.douban.com/subject/1088054/)|
|__Android__|四大组件（服务、广播、ContentProvider、页面容器）<br>基础UI组件（ListView、ViewPager）<br>异步任务机制（AsyncTask、Handler、线程池）<br>布局优化（层级、绘制、碎片化处理）<br>图片加载（Bitmap、缓冲区）|[UniversalMusicePlayer](https://github.com/googlesamples/android-UniversalMusicPlayer)(通过学习一个音乐播放器的代码能很快了解四大组件)<br>[Android Training官方课程](http://hukai.me/android-training-course-in-chinese/index.html)<br>[Android一些重要知识点解析整理](https://github.com/FX-Max/Point-of-Android)<br>[Android UI/UX库](https://github.com/wasabeef/awesome-android-ui)<br>（各类常用组件及扩展组件的集合）[Picasso](http://square.github.io/picasso/)、[Glide](https://github.com/bumptech/glide)（两个图片加载库）<br>[The Google I/O 2016 Android App](https://github.com/google/iosched)(Google大会官方的App，适合学习各类实现)<br>[Android开发技术前线](http://www.devtf.cn/)（定期翻译、发布国内外Android优质的技术、开源库、软件架构设计、测试等文章）|[第三方库集合](https://github.com/wasabeef/awesome-android-libraries)|

#### 软件工程 ####


|  知识点  ||链接或书籍|进阶|
| :------ | :------ | :------ | :------ |
|__基础工具__|IDE、Git、Maven|[AndroidStudio](https://developer.android.com/studio/index.html)<br>[Git权威指南中文手册](http://iissnan.com/progit/html/zh/ch1_0.html)||
|__软件质量__|代码整洁<br>代码质量<br>代码重构|[编写可读代码的艺术](https://book.douban.com/subject/10797189/)（来自Google工程师，专注于代码可读性）<br>[代码整洁之道](https://book.douban.com/subject/4199741/)（使用面向对象+敏捷开发原则编写清晰可维护的代码）<br>[重构-改善既有代码的设计](https://book.douban.com/subject/4262627/)（学习改善已有代码）<br>[重构手册](https://book.douban.com/subject/1173730/)（改善代码的实际操作）||
|__设计模式__|23种常见设计模式|[大话设计模式](https://book.douban.com/subject/2334288/)<br>[Head First设计模式](https://book.douban.com/subject/2243615/)(两本入门级的设计模式书籍)|[设计模式-可复用面向对象软件的基础](https://book.douban.com/subject/1052241/)（设计模式在实际中的应用）|
|__敏捷开发__||[解析极限编程](https://book.douban.com/subject/1790225/)<br>[敏捷开发的艺术](https://book.douban.com/subject/4037534/)|[敏捷软件开发-原则、模式与实践](https://book.douban.com/subject/5348122/)|
|__专业开发__|序员职业素养<br>更高效、更实效|[程序员的是职业素养](https://book.douban.com/subject/11614538/)<br>[程序员修炼之道-从小工到专家](https://book.douban.com/subject/5387402/)||
|__思考人生__||[黑客与画家](https://book.douban.com/subject/6021440/)(硅谷创业之父Paul Craham 的文集，主要介绍黑客及优秀程序员的爱好和动机)||


#### 鸣谢 ####

[一个老鸟发的公司内部整理的 Android 学习路线图 Markdown 版本](https://www.diycode.cc/topics/122)
 <br>
[另一份 Android 开发学习路线图](https://www.diycode.cc/topics/117)
