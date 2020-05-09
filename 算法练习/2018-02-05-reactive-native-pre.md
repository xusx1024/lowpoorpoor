---
layout: post
title:  Hybrid预研
date:   2018-02-05
categories: Android
tag: [android,杂项]
---


#### 目录 ####

* [HybridApp简介](#Hybrid简介)
* [Hybrid方案](#Hybrid方案)
* [react native](#react-native)
* [weex](#weex)
* [VasSonic](#VasSonic)
* [android友好度]()
* [业务分析]()

#### HybridApp简介 ####

混合模式移动应用.指介于web-app,native-app之间,并且兼具二者优势的app.

如果native-view和web-view交替展示,此时app主题是native,web为补充,该归类于native.

如果同一view内,同时包括native-view和web-view,互相之间层叠,该归类于Hybrid.

如果是以web-view 为主,穿插native功能,归类于web-app.

|           |Native App|Web App|Hybrid App|
| :---      | :--- | :--- |:---|
|原生功能体验 |优|差|良|
|渲染性能     |快|慢|快|
|访问底层    |支持|不支持|支持|
|更新复杂度       |高|低|低|
|社区       |成熟|成熟|有局限|
|上手难度|难|易|易|
|开发成本|高|低|低|
|开发周期|长|短|短|
|跨平台|否|是|是|

#### Hybrid方案 ####
 

说起来选方案,紧跟大厂app,错不了!

 |公司|框架|
 |:---|:---|
 |Facebook|react native|
 |阿里巴巴|weex|
 |腾讯|VasSonic|

最火的是rn啦,但是at两大国内巨头,也不能不考虑.

rn和weex的资源和社区现在都比较丰富了.VasSonic是我在搜集资料是看到,以前从未有过耳闻,不知道

其社区建设的如何.同时,考虑at巨头里面的不少开源项目都是kpi压力之下的大作,不知道其支持程度如

何.再加上Facebook自带国外光环,让我等还未站起来的程序员不得不仰视.

这样一个单方面考虑,就不由得往rn靠拢了呢.

其实这只是一个单方面原因,最重要的是哪个的易上手,漏洞少,资源丰富了.

##### react native #####

[React Native 中文网](https://reactnative.cn/)

##### weex #####

[weex](https://github.com/apache/incubator-weex)

##### VasSonic #####

[VasSonic](https://github.com/Tencent/VasSonic)
