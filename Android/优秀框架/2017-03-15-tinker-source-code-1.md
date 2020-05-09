---
layout: post
title:  Tinker学习(一)热修复基础知识
date:   2017-03-15
categories: Android 
tag: hotfix
---
  

#### Dalvik ####
 
Dalvik虚拟机实则也算是一个Java虚拟机，只不过它执行的不是class文件，而是dex文件。Dalvik VM是基于寄存器的，而JVM是基于栈的；Dalvik有专属的`文件执行格式dex（dalvik executable）`，而JVM则执行的是java字节码。Dalvik VM比JVM速度更快，占用空间更少。

通过Dalvik的字节码我们不能直接看到原来的逻辑代码，这时需要借助如[Apktool](https://ibotpeaches.github.io/Apktool/install/)或dex2jar+jd-gui工具来帮助查看。但是，注意的是最终我们修改APK需要操作的文件是.smali文件，而不是导出来的Java文件重新编译（况且这基本上不可能）。

当 Android 系统安装一个应用的时候，有一步是对 Dex 进行优化，这个过程有一个专门的工具来处理，叫 DexOpt。DexOpt 是在第一次加载 Dex 文件的时候执行的。这个过程会生成一个 ODEX 文件，即 Optimised Dex。执行 ODEX 的效率会比直接执行 Dex 文件的效率要高很多。

但是在早期的 Android 系统中，DexOpt 有两个问题。<br/>（一）：DexOpt 会把每一个类的方法 id 检索起来，存在一个链表结构里面，但是这个链表的长度是用一个 short 类型来保存的，导致了方法 id 的数目不能够超过65536个。当一个项目足够大的时候，显然这个方法数的上限是不够的。<br/>（二）：Dexopt 使用 LinearAlloc 来存储应用的方法信息。Dalvik LinearAlloc 是一个固定大小的缓冲区。在Android 版本的历史上，LinearAlloc 分别经历了4M/5M/8M/16M限制。Android 2.2和2.3的缓冲区只有5MB，Android 4.x提高到了8MB 或16MB。当方法数量过多导致超出缓冲区大小时，也会造成dexopt崩溃。
<br/>
#### ART ####

即Android Runtime，google为了替代Dalvik专门为Android研发的。Android KK为开发者推出，L版本正式上线。比替代品更高效省电，执行的是本地机器码，依靠Ahead-Of-Time (AOT)机制.

- 系统性能大幅提升
- App启动、运行更快
- 减少每次启动的编译增加电池续航
- 存储占用更大
- 安装时间更长

还有一个地方需要注意的是，应用程序的安装发生在两个时机，第一个时机是系统启动的时候，第二个时机系统启动完成后用户自行安装的时候。在第一个时机中，系统除了会对/system/app和/data/app目录下的所有APK进行dex字节码到本地机器码的翻译之外，还会对/system/framework目录下的APK或者JAR文件，以及这些APK所引用的外部JAR，进行dex字节码到本地机器码的翻译。这样就可以保证除了应用之外，系统中使用Java来开发的系统服务，也会统一地从dex字节码翻译成本地机器码。也就是说，将Android系统中的Dalvik虚拟机替换成ART运行时之后，系统中的代码都是由ART运行时来执行的了，这时候就不会对Dalvik虚拟机产生任何的依赖。
<br/>
#### Dalvik vs ART ####

Dalvik虚拟机执行的是dex字节码，ART虚拟机执行的是本地机器码。这意味着Dalvik虚拟机包含有一个解释器，用来执行dex字节码。当然，Android从2.2开始，也包含有JIT（Just-In-Time），用来在运行时动态地将执行频率很高的dex字节码翻译成本地机器码，然后再执行。通过JIT，就可以有效地提高Dalvik虚拟机的执行效率。但是，将dex字节码翻译成本地机器码是发生在应用程序的运行过程中的，并且应用程序每一次重新运行的时候，都要做重做这个翻译工作的。因此，即使用采用了JIT，Dalvik虚拟机的总体性能还是不能与直接执行本地机器码的ART虚拟机相比。

在计算机的世界里，与JIT相对的是AOT。AOT进Ahead-Of-Time的简称，它发生在程序运行之前。我们用静态语言（例如C/C++）来开发应用程序的时候，编译器直接就把它们翻译成目标机器码。这种静态语言的编译方式也是AOT的一种。

ART虚拟机并不要求开发者将自己的应用直接编译成目标机器码。这样，将应用的dex字节码翻译成本地机器码的最恰当AOT时机就发生在应用安装的时候。
 
![示例图](../res/img/dalvik&art.png)

<br/>
#### dex（dalvik executable） ####

Dex字节码,一种对class文件优化的产物,传统Class文件是一个Java源码文件会生成一个.class文件，而Android是把所有Class文件进行合并，优化，然后生成一个最终的class.dex,目的是把不同class文件重复的东西只需保留一份,如果我们的Android应用不进行分dex处理,最后一个应用的apk只会有一个dex文件。<br/>


.dex格式是专为Dalvik设计的一种压缩格式，适合内存和处理器速度有限的系统。执行的是字节码，它是依靠Just-In-Time (JIT)机制去解释字节码<br/>

odex（optimised dex）,优化后的dex。

#### 主流修复方案：Native hook ####

以[AndFix](https://github.com/alibaba/AndFix)最著名。原理如下图：
 
![示例图](../res/img/andfix-yuanli.png)

其修复过程如图：
 
![示例图](../res/img/andfix-guocheng.png)

这套方案直接使用`dalvik_replaceMethod`替换class中方法的实现。由于它并没有整体替换class，而field在class中的相对地址在class加载时已确定，所以AndFix无法支持新增或者删除field的情况(通过替换init与clinit只可以修改field的数值)。

#### 主流修复方案：基于android dex分包 ####

详细原理介绍[传送](https://mp.weixin.qq.com/s?__biz=MzI1MTA1MzM2Nw==&mid=400118620&idx=1&sn=b4fdd5055731290eef12ad0d17f39d4a)。

我用自己的话总结一下：<br/>
1.分包方案中，把修改的补丁dex，插入dexElements数组的最前面，这样根据classLoader找寻一个类的机制，理论上会优先找寻补丁dex里的修改类，从而完成修复；<br/>
2.如果修复好的类和引用该类的类不在同一个dex中则会引发错误，需要防止dexopt给修改后的类增加`CLASS_ISPREVERIFIED`标志，具体的说明可以参考上述文章；需要注意，dexopt是优化dex的，如果给所有的类都做非preverify，势必会引起一些性能上的损耗。<br/>
3.上述问题发生在dalvik虚拟机，如果是art上，插桩对代码的执行效率没什么影响，因为art直接执行本地机器码。但是如果补丁中的类出现修改类变量或者方法，可能会导致内存地址错乱，为了解决该问题，需要将修改类的父类、引用类、都加入补丁包中，补丁包会急剧变大。

#### 主流修复方案：基于Instant Run的冷插拔原理的Dex替换 ####

- 热插拔：无需重启app，无需重建页面，适用与方法修改，变量修改；
- 温插拔：需要重建页面，典型代表：修改了Resource文件；
- 冷插拔：需要重启app，代表：涉及结构性变化的，比如修改了继承规则，方法签名等。


Tinker的思路，使用差异dex文件和旧的dex文件，生成修复后的，新的dex文件，从而达到完全使用新的dex的修复效果。