[TOC]

# Flutter  初探



## 原生接口调用库

- [AlertManager](https://github.com/flutter/plugins/tree/master/packages/android_alarm_manager) 提醒服务
- [Android的Intent](Android的Intent)
- [系统电量](https://github.com/flutter/plugins/tree/master/packages/battery)
- [系统网络连接状态](https://github.com/flutter/plugins/tree/master/packages/connectivity)
- [设备信息](https://github.com/flutter/plugins/tree/master/packages/device_info)
- [设备中选取或者拍摄照片](https://github.com/flutter/plugins/tree/master/packages/image_picker)
- [App安装包的版本等信息](https://github.com/flutter/plugins/tree/master/packages/package_info)
- [获取常用文件路径](https://github.com/flutter/plugins/tree/master/packages/path_provider)
- [App图标添加快捷方式](https://github.com/flutter/plugins/tree/master/packages/quick_actions)
- [设备的加速度和陀螺仪传感器](https://github.com/flutter/plugins/tree/master/packages/sensors)
- [App KV存储功能](https://github.com/flutter/plugins/tree/master/packages/shared_preferences)
- [启动URL，包括打电话、发短信和浏览网页等功能](https://github.com/flutter/plugins/tree/master/packages/url_launcher)
- [播放视频文件或者网络流的控件](https://github.com/flutter/plugins/tree/master/packages/video_player)



Flutter 视图树的创建和管理，布局渲染原理，布局渲染优化

Flutter 线程管理

DartVM机制

Flutter MVP

Flutter 原生调用

Flutter 与原生性能对比

Flutter 打包

Flutter 热修复

Flutter 加固

Flutter 宽高适配

## Flutter 内存管理

Futter Framework使用Dart语言开发，所以App进程中需要一个Dart运行环境（VM），和Android Art一样，Flutter也对Dart源码做了AOT编译，直接将Dart源码编译成了本地字节码，没有了解释执行的过程，提升执行性能。这里重点关注Dart VM内存分配(Allocate)和回收(GC)相关的部分。

和Java显著不同的是Dart的"线程"(Isolate)是不共享内存的，各自的堆(Heap)和栈(Stack)都是隔离的，并且是各自独立GC的，彼此之间通过消息通道来通信。Dart天然不存在数据竞争和变量状态同步的问题，整个Flutter Framework Widget的渲染过程都运行在一个isolate中。

Image内存方面，Android 6.0 和 7.0都是Java部分的内存在增长，而Android 8.0则是Native部分的内存在增长。

Flutter Image使用的内存既不属于Java虚拟机内存也不属于Native内存，而是Graphics内存。

Graphics: 图像缓冲区队列向屏幕显示像素（包括GL Surface，GL 纹理等）所使用的内存。

那么至少Flutter Image所使用的内存不会是Java虚拟机内存，这对不少Android设备都是一个好消息，这意味着使用Flutter Image没有OOM的风险，能够较好的利用Native内存。

使用Image的时候，建立一个内存缓存池是个好习惯，Flutter Framework提供了一个ImageCache来缓存加载的图片，但它不同于Android Lru Cache，不能精确的使用内存大小来设定缓存池容量，而是只能粗略的指定最大缓存图片张数。

## 混合开发，Flutter启动速度

FlutterView的首帧渲染耗时较高，在Debug版本大概会黑屏2秒，release版本会好很多。观察CPU曲线，仍然是比较耗时的过程。闲鱼团队的做法是，先建立一个1*1像素的首帧，预先加载。(这是指在混合应用中的实践)

```dart
final WindowManager wm = mFakeActivity.getWindowManager();
final FrameLayout root = new FrameLayout(mFakeActivity);
     
//一个像素足矣
FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(1, 1);
root.addView(flutterView,params);
WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
wlp.width = 1;
wlp.height = 1;
wlp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
wlp.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
wm.addView(root,wlp);

final FlutterView.FirstFrameListener[] listenerRef = new FlutterView.FirstFrameListener[1];
	listenerRef[0] = new FlutterView.FirstFrameListener() {
    	@Override
       public void onFirstFrame() {
       	//首帧渲染完后取消窗口
         	wm.removeView(root);
          flutterView.removeFirstFrameListener(listenerRef[0]);
       }
   	};

flutterView.addFirstFrameListener(listenerRef[0]);
String appBundlePath = FlutterMain.findAppBundlePath(mFakeActivity.getApplicationContext());
flutterView.runFromBundle(appBundlePath, null, "main", true);
```

## Debug版本使用release Engine

Flutter Engine的Debug版本和Release版本存在很大的性能差异，所以我们测试最好使用Release版本，但是，Release版本的Apk又不能使用Android profiler来观察内存，所以我们需要在Debug版本的Apk中打包一个Release版本的Flutter Engine, 可以修改flutter tool中的flutter.gradle来实现：

```
//不做判断，强制改为打包release版本的engine
	/**
     * Returns a Flutter build mode suitable for the specified Android buildType.
     *
     * Note: The BuildType DSL type is not public, and is therefore omitted from the signature.
     *
     * @return "debug", "profile", "dynamicProfile", "dynamicRelease", or "release" (fall-back).
     */
    private static String buildModeFor(buildType) {
        // if (buildType.name == "profile") {
        //     return "profile"
        // } else if (buildType.name == "dynamicProfile") {
        //     return "dynamicProfile"
        // } else if (buildType.name == "dynamicRelease") {
        //     return "dynamicRelease"
        // } else if (buildType.debuggable) {
        //     return "debug"
        // }
        return "release"
    }
```

## hot reload 原理

## hotfix 实现

在Android端，把Flutter编译生成的产出替换成最新的即可。这样在iOS端，不能这么干，这样无法两端无法一致。





>  年终总结，内存，崩溃，flutter，cjson，c二叉树，红黑树，网站