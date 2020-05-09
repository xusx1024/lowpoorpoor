[TOC]

# 小议Android的内存

## 内存总览
内存是应用在运行过程中临时数据存储的介质，因此叫做手机运行内存，也即RAM。
内存可以和PC内存条来对比理解。PC端，内存条分为DDR3，DDR4等系列。类似的，手机的内存，考虑到体积&功耗，采用LPDDR RAM。目前(2018)主流的运行内存有LPDDR3，LPDDR4以及LPDDR4X。
我们经常说内存不足了，那么是不是内存的容量越大越好呢？
如果现在有4G LPDDR4 和6G LPDDR3的手机，你会选择大的吗？
请先看如下三块内存的参数对比表后再做结论：

|      类型|      时钟频率|带宽      |工作电压      |
| ---- | ---- | ---- | ---- |
| LPDDR3     |  800MHZ    |  12.8GB/s    |      1.2V|
|      LPDDR4|   1600MHZ   |     25.6GB/s |      1.1V|
|      LPDDR4X|   1600MHZ   |      25.6GB/s|      0.6V|

我们LPDDR4比LPDDR3的性能整整高出一倍，而4X比4省电。
另外，内存不是孤立的，他跟操作系统系统版本，应用生态都有关系。比如1GB内存下的Android9.0系统要比Android4.0系统流畅。相同内存下规范封闭的IOS生态，要比野蛮开放的Android优秀的多。

##  [内存管理](https://developer.android.com/topic/performance/memory-overview) 

Android Runtime使用paging(内存分页)与memory-mapping(内存映射)的机制来管理内存。这意味着任何修改的内存-无论是通过分配新对象还是访问mmaped pages 的内容 - 都会存在于RAM中，并且不可page out.释放这些内存的唯一方式是释放应用持有的这些引用，使得GC可以回收他们。有一个例外：任何没有修改的mmaped的文件，比如说代码，如果系统在别处想要使用，那就可以被page out.

### 垃圾回收

托管内存环境（如ART或Dalvik虚拟机）会跟踪每个内存分配。 一旦确定程序不再使用一块内存，它就会将其释放回堆中，而无需程序员的任何干预。 回收托管内存环境中未使用内存的机制称为垃圾回收。 垃圾收集有两个目标：在程序中查找将来无法访问的数据对象; 并回收这些对象使用的资源。

Android的内存堆是一代的，意味着它根据所分配对象的预期生命和大小来跟踪不同的分配桶。 例如，最近分配的对象属于Young代。 当一个对象长时间保持活动状态时，它可以被提升为老一代，然后是永久代。

每个堆生成都有自己的专用上限，可以占用对象所占用的内存量。 每当一代开始填满时，系统就会执行垃圾收集事件以试图释放内存。 垃圾收集的持续时间取决于它收集的对象生成以及每一代中有多少活动对象。

即使垃圾收集速度非常快，它仍然会影响应用程序的性能。 您通常不会控制代码中何时发生垃圾收集事件。 系统具有一组用于确定何时执行垃圾收集的标准。 满足条件后，系统将停止执行该进程并开始垃圾回收。 如果在像动画或音乐播放这样的密集处理循环中间发生垃圾收集，则会增加处理时间。 这种增加可能会推动您的应用程序中的代码执行超过建议的16ms阈值--高效，流畅的帧渲染。

此外，您的代码流可能会执行各种工作，这些工作会强制垃圾收集事件更频繁地发生，或使其持续时间超过正常范围。 例如，如果在alpha混合动画的每个帧期间在for循环的最里面部分分配多个对象，则可能会使用大量对象污染内存堆。 在这种情况下，垃圾收集器会执行多个垃圾收集事件，并可能降低应用程序的性能。

### 共享内存
Android系统通过下面几个方式在不同Process中实现共享RAM:
1. Android应用的进程都是从一个叫做Zygote的进程fork出来的。Zygote进程在系统启动并且载入通用的framework的代码与资源(比如活动主题)之后开始启动。为了启动一个新的程序进程，系统会fork Zygote进程生成一个新的进程，然后在新的进程中加载并运行应用程序的代码。这使得大多数的RAM pages被用来分配给framework的代码，同时使得RAM资源能够在应用的所有进程之间进行共享。
2. 大多数static的数据被mmapped到一个进程中。这不仅仅使得同样的数据能够在进程间进行共享，而且使得它能够在需要的时候被paged out。常见的static数据包括：
	-   Dalvik代码(放置在预链接的.odex文件中，可以直接映射 -- 可达到Dalvik代码是共享的) 
	-   app Resource(系统将资源表设计为可以映射的结构，并且与APK的zip条目对齐 -- 可达到静态共享)
	-   存在于.so中的本地代码
3. 大多数情况下，Android通过显式的分配共享内存区域(例如ashmem或者gralloc)来实现动态RAM区域能够在不同进程之间进行共享的机制。例如，Window Surface在App与Screen Compositor(屏幕合成器)之间使用共享的内存，Cursor Buffers在Content Provider与Clients之间共享内存。


Android操作系统，在给应用分配内存时，是按需分配的，根据应用内存需求的增长，不断的分配内存，但是会设置一个上限值。直到应用请求的内存不能够再满足，这就是OOM爆发之地了，也是最坏的情况。也许你的应用没有OOM，但是卡顿，发热，其实是GC再拼命工作来弥补内存不足的问题，已经非常危险了。



### 分配和回收内存

每一个进程的Dalvik heap都反映了使用内存的占用范围。这就是通常逻辑意义上提到的Dalvik Heap Size，它可以随着需要进行增长，但是增长行为会有一个系统为它设定的上限。

逻辑上讲的Heap Size和实际物理意义上使用的内存大小是不对等的，当系统检查应用的堆使用时，会计算出一个叫做PSS的值，Proportional Set Size(PSS)记录了和别的进程共享的脏的和干净的页，但是页的数量只有共享该RAM的应用程序数的比值。系统认为PSS总数是屋里内存占用量。

Dalvik heap与逻辑上的heap size不吻合，这意味着Android并不会去做heap中的碎片整理用来关闭空闲区域。Android仅仅会在heap的尾端出现不使用的空间时才会做收缩逻辑heap size大小的动作。但是这并不是意味着被heap所使用的物理内存大小不能被收缩。在垃圾回收之后，Dalvik会遍历heap并找出不使用的pages，然后使用madvise把那些pages返回给kernal。因此，成对的allocations与deallocations大块的数据可以使得物理内存能够被正常的回收。然而，回收碎片化的内存则会使得效率低下很多，因为那些碎片化的分配页面也许会被其他地方所共享到。

### 限制应用内存

为了维持多任务的功能环境，Android为每一个app都设置了一个硬性的heap size限制。准确的heap size限制随着不同设备的不同RAM大小而各有差异。如果你的app已经到了heap的限制大小并且再尝试分配内存的话，会引起OutOfMemoryError的错误。

在一些情况下，你也许想要查询当前设备的heap size限制大小是多少，然后决定cache的大小。可以通过getMemoryClass()来查询。这个方法会返回一个整数，表明你的app heap size限制是多少megabates。

### 切换应用

当用户切换不同应用，Android会把那些非前台的进程放到LRU cache中。例如，当用户刚开始启动了一个应用，这个时候为它创建了一个进程，但是当用户离开这个应用，这个进程并没有离开。系统会把这个进程放到cache中，如果用户后来回到这个应用，系统重用该进程，从而实现app之间的快速切换。

如果应用有缓存进程并且保留了当前不再使用的内存，尽管没有在使用之中，会影响到系统的整体性能。当系统开始进入低内存状态时，他会根据进程最近最少未被使用原则杀死LRU cache中的进程。系统也会考虑保留最多内存的进程，并且可用终止他们以释放内存。

> 注意：当系统在LRU chache中大杀四方时，它主要是自下而上。系统也会考虑那个进程消费的内存更多，从而杀死他们提供更多内存。你的应用在LRU cache列表中，消费的内存排行越小，越有利于留在cache中，就可以快速的恢复。

### GC机制
https://source.android.google.cn/devices/tech/dalvik/gc-debug
当前商业虚拟机的垃圾收集都采用“分代收集”算法。分代收集就是，根据Java对象存活周期的不同，将内存划分为几块。然后根据各个年代的特点用最适当的收集算法。

## 内存问题

### 异常

OOM，内存分配失败，内存不足被系统终结，系统重启

### 卡顿

java内存不足，频繁GC，导致卡顿。ART的GC性能是Dalvik的5-10倍。

## 内存监控

1. 采集方式

   前台用户，可以每5分钟采集一次PSS，java堆，图片总内存。建议采样部分用户，按照用户抽样，而不是按次数。比如一个用户命中采集，一天内都要持续采集。

2. 计算指标

   - 内存异常率，可以反映内存占用的异常情况，如果出现新的内存使用不当或内存泄漏的场景，这个指标会有所上涨。其中PSS值通过`Debug.MemoryInfo`拿到。

     ```
     内存 UV 异常率 = PSS 超过400MB的 UV / 采集 UV
     ```

   - 触顶率：可以反映java内存的使用情况，如果超过85%最大堆限制，GC会变得更加频繁，容易造成OOM和卡顿。

     ```
     内存 UV 触顶率 = Java 堆占用超过最大限制的 85%的 UV / 采集 UV
     ```

     是否触顶可以通过下面的方法计算得到。

     ```
     long javaMax = runtime.maxMemory();
     long javaTotal = runtime.totalMemory();
     long javaUsed = javaTotal - runtime.freeMemory();
     float proportiom = (float)javaUsed / javaMax;
     ```

     一般客户端只上报，后台计算。

3. GC监控

   在实验室或者内部适用环境，可以通过Debug.startAllocCounting来监控java内存分配和GC情况，需要注意的是对性能会有影响，已经被标记为deprecated。

   通过监控可以拿到内存分配的次数，大小，GC发起次数等信息。

   ```
   long allocCount = Debug.getGloableAllocCount();
   long allocSize = Debug.getGloableAllocSize();
   long gcCount = Debug.getGloableGcInvocationCount();
   ```

   在Android 6.0 之后，系统可以拿到更加精准的GC信息。

   ```
   // 运行的 GC 次数
   Debug.getRuntimeStat("art.gc.gc-count");
   // GC 使用的总耗时，单位是毫秒
   Debug.getRuntimeStat("art.gc.gc-time");
   // 阻塞式 GC 的次数
   Debug.getRuntimeStat("art.gc.blocking-gc-count");
   // 阻塞式 GC 的总耗时
   Debug.getRuntimeStat("art.gc.blocking-gc-time");
   
   ```

   需要特别注意阻塞式GC的次数和耗时，这会暂停应用线程，可能导致应用卡顿。可以更加细粒度地分应用场景统计。比如启动，登录等关键场景。

### java内存分配追踪

[调查RAM的使用情况](https://developer.android.com/studio/profile/investigate-ram?hl=zh-cn)

- Android Profiler
- MAT
- LeakCanary

### Native内存分配追踪
 [调试本地内存使用](https://source.android.com/devices/tech/debug/native-memory)

## 内存优化

误区
- 内存占用越少越好
- Native内存不需要考虑

 ### 设备分级
良好的架构设计需要针对低端设备：

- [设备分级](https://github.com/facebook/device-year-class)。对于低端设备，关闭复杂动画，甚至关闭某些功能，使用565图片，用更小的缓存内存
- 统一的缓存管理。可以适当的使用内存，在系统内存紧张时(OnTrimMemory)，要立刻归还；
- 进程模型。空进程也会占用10M内存，因此，减少应用启动的进程数，减少常驻进程，有节操的保活
- 安装包大小。代码，资源，图片，so库的体积，跟占有内存关系很大。例如Facebook lite，QQ lite

 ### Bitmap

- 统一图片库

  图片内存优化的前提是收拢图片的调用，这样可以进行整体的控制。不只是图片格式，图片压缩算法，图片框架等的统一配置调用，`Bitmap.createBitmap` `BitmapFactory` 相关接口也有收拢调用。

- 统一监控

  - 大图片

    需要注意图片是否大于view的宽高，甚至是屏幕的宽高。在开发阶段，检测到不合规的图片使用，应该立即弹出对话框提示图片所在的Activity和堆栈。在灰度或者线上环境，将信息上报到后台，统计这些图片占的比例，即“超宽率”。

  - 重复图片

    图片的像素数据完全一致，但是却存在多个不同的对象。

- 图片总内存

  通过收拢图片调用，可以统计应用所有图片占用的内存，在线上可以按不同的系统，屏幕分辨率等维度去分析图片内存的占用情况。

 ### OOM

内存泄漏主要分两种情况，一种是同一个对象泄漏，另一种就是每次都泄漏新的对象，并可能出现成百上千个无用的对象。

很多内存泄漏都是框架设计不合理导致，比如单例满天飞，MVC中Controler声明周期长于View。

内存监控：

- java内存泄漏。建立类似LeakCanary自动化检测方案，至少做到Activity，Fragment的泄漏检测。开发过程中，希望出现泄漏的时候可以弹出对话框，让开发者更加容易去发现和解决问题。线上的话，可以对生成的Hprof内存快照文件做一些优化，裁剪Hprof文件，7zip压缩上传，后台分析。
- OOM监控。[美团方案Probe](https://static001.geekbang.org/con/19/pdf/593bc30c21689.pdf)。在发生OOM时候生成Hprof内存快照，然后在app再次启动时候，开启单独线程对这个文件进一步进行分析。
- Native 内存泄漏。根据我们内存监控模块的总结，做些尝试，现在还没有很好的方案。
- 针对无法重编so的情况，使用[PLT Hook](https://github.com/kubo/plthook)库的内存分配函数，重定向到自己的实现后记录分配的内存地址，大小，来源so库路径等信息，定期扫描分配与释放是否配对，对于不配对的分配输出我们记录的信息。
- 针对可重编so的情况，通过GCC的“-finstrument-functions”参数给所有函数插桩，桩中模拟调用栈入栈出栈操作；通过ld的“-wrap”参数拦截内存分配和释放函数，重定向到自己实现的后记录分配的内存地址，大小，来源so库路径以及插桩记录的调用栈此刻的内容，定期扫描分配和是否是否配对，对于不配对的分配输出我们记录的信息。

开发过程中，使用Android Profiler和MAT 配合使用。

日常线上，要做到成体系化，使用APM平台。



## 练习

- 请架构一个图片统一调用框架
- 请使用[HA-HA](https://github.com/square/haha)快速判断内存中是否存在重复图片，并输出图片的信息和调用堆栈信息
- 请尝试裁剪Hprof文件，以方便上传至后台分析
- 请仿照Android Studio实现自定义的Allocation Tracker



