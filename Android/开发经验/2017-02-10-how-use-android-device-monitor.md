---
layout: post
title:  Android Device Monitor不显示App进程信息问题
date:   2017-02-10
categories: Android 
tag: android
---

 
#### 问题现象 ####

	
![ADM现象截图](../res/img/adm_question.png)

	如图所示，CTRL + SHIFT + A 输入Android Device Monitor并打开，可以看到连接了两部手机，但都没有显示我想要调试的进程信息。

> 去查看Hierarchy View，提示如下：		
> 
-  Unable to get view server version from device;
-  Unable to get view server protocol version from device
-  Unable to get the focused window from device
-  Unable to debug device
 	 

#### 原因 ####

	For security reasons HierarchyViewer does NOT work on production builds. It works only with userdebug and engineering builds (this includes the emulator.)
 

#### 解决方法 ####

##### 方法一 #####

	使用模拟器，推荐

##### 方法二 #####

	app不签名，也可以查看到

##### 方法三 #####

	经常会引起OOM等问题，但是还是贴出来，不推荐使用：

	[ViewServer](https://github.com/romainguy/ViewServer/blob/master/viewserver/src/main/java/com/android/debug/hv/ViewServer.java)



#### 名词解释 ####

	DDMS：Dalvik Debug Monitor Server