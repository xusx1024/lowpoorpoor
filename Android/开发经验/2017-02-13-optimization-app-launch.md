---
layout: post
title:  Android app 启动优化
date:   2017-02-13
categories: Android 
tag: android
---
 


#### 基本概念 ####
    Android Application与其他移动平台有两个重大不同点：

    1.每个Android App都在一个独立空间里，意味着其运行在一个单独的进程中，拥有自己的VM，被系统分配一个唯一的user ID.

    2.Android App由很多不同组件(四大组件)组成，这些组件还可以启动其他App的组件，因此，Android App并没有一个类似程序入口的main()方法。



    Android进程与Linux进程一样。默认情况下，每个apk运行在自己的Linux进程中。另外，默认一个进程里面只有一个线程-主线程。这个主线程中有一个Looper实例，通过调用Looper.loop()从Message队列里面取出Message来做相应的处理。

#### 进程启动策略 ####

    简单的说，进程在其需要时被启动。任意时候，当用户或其他组件调取你apk中的任意组件时，如果你的apk没有运行，系统会为其创建一个新的进程并启动。通常，这个进程会持续运行直到被系统杀死。关键是：进程是在被需要的时候才创建。

#### Android系统的启动 ####

    与众多基于Linux内核的系统类似，Android系统启动时，bootloader启动内核和init进程，init进程分裂出更多名为"daemons"的底层的Linux进程(守护进程)。诸如android debug daemon,USB daemon等。这些守护进程处理底层硬件相关的接口。

    随后，init进程会启动一个非常有意思的进程：“Zygote”。这是一个Android平台的非常基础的进程。这个进程初始化了第一个VM，并且预加载了framework和众多App所需要的通用资源。然后它开启一个Socket接口来监听请求，一旦收到新的请求，Zygote会根据请求，基于自身预加载的VM来孵化出一个新的VM创建一个新的进程。

    启动Zygote之后，init进程会启动runtime进程。Zygote会孵化出一个超级管理进程：System Server。 SystemServer会启动所有系统核心服务，例如Activity Manger Service,硬件相关的Service等。到此，系统准备好启动它的第一个App进程：Home进程了。


#### App的启动 ####

![app launch](../res/img/Application_launch.jpg)

>
click事件会调用```startActivity(Intent)```，会通过Binder IPC机制，最终调用到ActivityManagerService.该Service会执行如下操作：
>
- 第一步通过PackageManager的resolveIntent()收集这个intent对象的指向信息
- 指向信息被存储在一个intent对象中
- 下面重要的一步是通过```grantUriPermissionLocked()```方法来验证用户是否有足够的权限去调用该Intent对象指向的Activity
- 如果有权限，ActivityManagerService会检查并在新的task中启动目标activity
- 现在，是时候检查这个进程的ProcessRecord是否存在了
- 如果PrecessRecord为null，ActivityManagerService会创建新的进程来实例化目标activity。

##### 创建进程 #####

> ActivityManagerService调用```startProcessLocked()```方法来创建新的进程，该方法通过前面讲到的socket通道传递参数给Zygote进程。Zygote孵化自身，并调用```ZygoteInit.main()```方法来实例化ActivityThread对象并最终返回新进程的pid.
> ActivityThread随后依次调用```Looper.prepareLoop()```和```Looper.loop()```来开启消息循环。

流程图如下：
![process creation](../res/img/process_creation.jpg)

##### 绑定Application #####

> 接下来要做的就是将进程和指定的Application绑定起来。这个是通过上一节的ActivityThread对象中调用```bindApplication()```方法完成的。该方法发送一个BIND_APPLICATION的消息到消息队列中，最终通过```handleBindApplication()```处理该消息。然后调用```makeApplication()```方法来加载App的classes到内存中。
 流程如下：

![bind application](../res/img/bind_application.jpg)
	
##### 启动Activity #####

> 经过前两个步骤之后，系统已经拥有了该application的进程。后面的调用顺序就是普通的从一个已经存在的进程中启动一个新进程的activity了。
> 实际调用方法是```realStartActivity()```，它会调用application线程对象中的```sheduleLaunchActivity()```发送一个LAUNCH_ACTIVITY消息到消息队列中，通过```handleLaunchActivity()```来处理该消息。

 假设点击的是一个视频浏览的App，其流程如下：

![start activity](../res/img/start_activity.jpg)
##### Activity显示 #####

> Activity被创建出来后，会依次加载主题样式Theme中的windowBackground等属性，以及配置Activity层级上的一些属性，再inflate布局，当onCreate、onStart、onResume方法都走完，最后才进行setContentView的measure、layout、draw显示在界面上，此时可以看到App的第一帧画面了。

如图：
![display activity](../res/img/display_activity.png)

#### 测量App的启动时间 ####

	activity的启动时执行的方法：onCreat()、onStart()、onResume()的生命周期结束了，应用也不算是完全启动，还需要等View树全部构建完毕，一般认为，setContentView中的View全部显示结束了，算作是应用完全启动了。

##### Display Time #####

	api19之后，Android系统Log中增加了ActivityManager:Displayed [packageName/activityName]: time。所以，在Android Monitor中使用 ActivityManager:Displayed filter，查看启动首页的时间，这个时间是Activity启动到Layout全部显示的时间。

图为抓取微信开启时间：
![wechat display time](../res/img/display_time.png)

##### ADB命令查看启动时间 #####

`adb shell am start -W [packageName]/[packageName.MainActivity]`

![adb see launch time](../res/img/adb_launch_time.png)
>
- ThisTime：最后一个启动的Activity的启动耗时；
- TotalTime:到达当前页面的所有Activity的启动耗时；
- WaitTime：ActivityManagerService启动App的Activity时的总时间，包括当前Activity的onPause()和目的Activity的启动。

`adb shell screenrecord --bugreport /sdcard/ScreenRecord.mp4`

>
这是一个录屏命令，和Android Monitor的Screen Recorder Options的功能一样，`bugreport`参数决定了视屏左上角会增加录制视频的时间和当前画面所在的帧数。个人感觉这玩意儿比较鸡肋。


##### 代码中自定义上报启动时间 #####

由于我们在一个App启动的开始，会做一些预操作，比如：
- 加载第三方黑盒SDK
- 网络、图片等框架的构造
- 业务数据预请求

所以ActivityManager:Displayed并不能精确我们App完全的启动时间。我们可以在API>=19的版本，在这些预操作做完之后手动调用`reportFullyDrawn`。这样Log中会增加一条日志：

![reportFullyDrawn time](../res/img/report_full_drawn.png)

#### 优化点 ####

##### 背景Theme #####
	
	当系统加载一个Activity的时候，onCreate()是一个耗时过程，为增加用户体验，系统会优先绘制一些初始界面：根据当前Activity的Theme来绘制，当Activity加载完毕后，才会替换为真正的界面。代码如下：

>	
	<layer-list xmlns:android="http://schemas.android.com/apk/res/android"
	            android:opacity="opaque">
	<!-- android:opacity="opaque"防止在启动的时候出现背景的闪烁 -->
	    <item android:drawable="@android:color/darker_gray"/>
	    <item>
	        <bitmap
	            android:gravity="center"
	            android:src="@mipmap/ic_launcher"/>
	    </item>
	</layer-list>

>
	<style name="StartStyle" parent="AppTheme">
        <item name="android:windowBackground">@drawable/start_window</item>
    </style>


##### 异步初始化 #####
	
	根据业务，利用多线程，IntentService等来异步和延迟初始化的操作 

##### 资源优化 #####

>	
- 布局宽而浅，不要窄而深
- 	使用.9
- 	tinyPNG
- 	混淆





#### 鸣谢 ####

[Launch-Time Performance ](https://developer.android.com/topic/performance/launch-time.html)

[Android Application启动流程分析](http://www.jianshu.com/p/a5532ecc8377)

[一触即发 App启动优化最佳实践](https://segmentfault.com/a/1190000007406875#articleHeader9)

在线检测App：[nimbledroid](https://nimbledroid.com/) 
	