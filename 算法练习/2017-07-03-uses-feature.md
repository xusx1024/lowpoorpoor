---
title: AndroidManifest.xml中的uses-feature标签
date: 2017-07-03
tags: android
categories: Android 
---

#### 引子 ####

如果你有一个装有android 1.5的设备，你可能会注意到一些高版本的应用没有在手机上的Android Market中显示。这必定是应用使用了<uses-feature>的结果。


#### 语法 ####
	
	<uses-feature
	  android:name="string"
	  android:required=["true" | "false"]
	  android:glEsVersion="integer" />

##### android:name #####

以描述字符串形式指定应用使用的单一硬件或软件功能。详见:[HERE](https://developer.android.google.cn/guide/topics/manifest/uses-feature-element.html?hl=zh-cn#features-reference)
	
	<uses-feature android:name="android.hardware.bluetooth" />
	<uses-feature android:name="android.hardware.camera" />

##### android:required #####

- android:required="true":当设备不具有该指定功能时，应用无法正常工作，或设计为无法正常工作
- android:required="false"：如果设备具有该功能，应用会在必要时优先使用该功能，但应用设计为不使用该指定功能也可正常工作
- 默认为true

	 		<uses-feature 
	        android:name="android.hardware.camera"
	        android:required=false />

##### android:glEsVersion #####

应用需要的OpenGL ES版本。高16位表示主版本号，低16位表示次版本号。例如2.0 --> 0x00020000;3.2 --> 0x00030002

	<uses-feature android:glEsVersion="0x00020000" required="true"/>

- 如果指定多个，将使用数值最高的
- 如果没有指定，系统假定只需要OpenGL ES 1.0
 
#### 注意 ####

- <uses-feature> 声明的用途是将您的应用依赖的硬件和软件功能集通知任何外部实体
- 一般而言，您始终都应确保为应用需要的所有功能声明 <uses-feature> 元素
- Android 系统本身在安装应用前不会检查设备是否提供相应的功能支持
- 其他服务（如 Google Play）或应用可能会在处理您的应用或与其交互的过程中检查它的 <uses-feature> 声明
- 如果minSdkVersion<=4或者targetSdkVersion<=4,googlePlay将不对该标签做任何过滤
- 蓝牙、相机、麦克风、定位、wifi、触屏、USB
- 输入法、多媒体、屏幕界面(小控件、壁纸等)






https://developer.android.google.cn/guide/topics/manifest/uses-feature-element.html?hl=zh-cn#required