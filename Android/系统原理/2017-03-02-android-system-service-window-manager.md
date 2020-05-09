---
layout: post
title:  Android系统服务：WindowManager
date:   2017-03-02
categories: Android System Framework
tag: android
---
 

#### 开发中使用 ####

- 获取屏幕宽高
- 代码设置全屏显示
- 保持屏幕常亮
- 悬浮view(来电显示提醒，浮层引导，应用内悬浮按钮)

#### 需要的类 ####

- `com.android.server.wm.WindowManagerService`
- `android.view.WindowManager`
- `android.view.Window`
 
 
  Window是一个抽象类，具体实现是PhoneWindow。不管是Activity、Dialog、Toast它们的视图都是附加在Window上的，因此Window实际上是View的直接管理者。 
WindowManager是外界访问Window的入口，通过WindowManager可以创建Window，而Window的具体实现位于WindowManagerService中，WindowManager和WindowManagerService的交互是一个IPC过程。

Window的添加、更新、移除是通过WindowManager接口管理的，在WindowManagerImpl中，调用了WindowManagerGlobal里的具体实现。

#### flag 参数 ####
Flags参数表示Window的属性，它有很多选项，通过这些选项可以控制Window的显示特性。
FLAG_NOT_FOCUSABLE: 表示Window不需要获取焦点，也不需要接收各种输入事件，此标记会同时启用FLAG_NOT_TOUCH_MODAL,最终事件会直接传递给下层的具有焦点的Window。
FLAG_NOT_TOUCH_MODAL: 在此模式下，系统会将当前Window区域以外的单击事件传递给底层的Window，当前Window区域以内的单击事件则自己处理。这个标记很重要，一般来说都需要开启此标记，否则其他Window将无法收到单击事件。
FLAG_SHOW_WHEN_LOCKED: 开启此模式可以让Window显示在锁屏的界面上。

#### type 参数 ####
   
Type参数表示Window的类型，Window有三种类型，分别是应用Window、子Window和系统Window。应用类Window对应着一个Activity。子Window不能单独存在，它需要附属在特定的父Window之中，比如常见的一些Dialog就是一个子Window。系统Window是需要声明权限才能创建的Window，比如Toast 和系统状态栏这些都是系统Window。Window是分层的，每个Window都有对应的z-ordered，层级大的会覆盖在层级小的Window的上面，这和HTML中的z-index的概念是完全一致的。在三类Window中，应用Window的层级范围是1-99，子Window的层级范围是1000-1999，系统Window的层级范围是2000-2999，这些层级范围对应着WindowManager.LayoutParams的type参数。如果想要Window位于所有Window的最顶层，那么采用较大的层级即可。很显然系统Window的层级是最大的，而且系统层级有很多值，一般我们可以选用TYPE_SYSTEM_OVERLAY或者TYPE_SYSTEM_ERROR,如果采用TYPE_SYSTEM_ERROR，只需要为type参数指定这个层级即可：
mLayoutParams.flags = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
同时声明权限：

	<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 
选择UI和状态最简单的Toast源码，避免陷入过深的逻辑代码。
实现的Window的demo，应该有两种类型。
 
Framework定义了三种窗口类型，三种类型的定义在WindowManager类中。
第一种为应用窗口。所谓的应用窗口是指该窗口对应一个Activity,由于加载Activity是 由 AmS完成的，因此，对于应用程序来讲，要创建一个应用类窗口，只能在Activity内部完成。
第二种是子窗口。所谓的子窗口是指，该窗口必须有一个父窗口，父窗口可以是一个应用类型窗口，也可以是任何其他类型的窗口。
第三类是系统窗口。系统窗口不需要对应任何Activity,也不需要有父窗口。对于应用程序而言，理论上是无法创建系统窗口的，因为所有的应用程序都没有这个权限，然而系统进程却可以创建系统窗口。

<br/>
注意：MIUI系统需要在设置->其他应用管理->应用信息->权限管理,中打开“显示悬浮窗”才能显示。


<br/>

window 诱导用户输入QQ号及密码：https://yq.aliyun.com/ziliao/160722

<br/>
视图是如何附加在Window上的,Window有是如何管理这些视图的.



http://bugly.qq.com/bbs/forum.php?mod=viewthread&tid=555






