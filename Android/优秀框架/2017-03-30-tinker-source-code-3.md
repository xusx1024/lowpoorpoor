---
layout: post
title:  Tinker学习(三) 核心的算法BSDiff、BSPatch
date:   2017-03-30
categories: Android 
tag: [算法,hotfix]
---
 

#### 简介 ####

BSDiff，用于服务端比较二进制文件并生成差异补丁；<br>
BSPatch，将旧文件和补丁文件合并生成新的文件。

比较差异使用的算法是`suffix sort`后缀排序算法。其中：<br>

- C实现可以参照：[Colin Percival](https://github.com/cperciva)的[bsdiff](https://github.com/cperciva/bsdiff)
- Java实现可以参照：[Joe Desbonnet](https://github.com/jdesbonnet)的[jbdiff](https://github.com/jdesbonnet/jbdiff)<br>

Tinker的`com.tencent.tinker.bsdiff`实现基本上和Joe Desbonnet的实现并无两样。


#### BSDiff基本步骤 ####

1. 将old文件中所有子字符串形成一个字典 
2. 对比old文件和new文件，产生diff string 和 extra string
3. 将diffstring和extra string以及相应的控制字用zip压缩成一个patch包。
 
所有差量更新算法的瓶颈，时间复杂度为O(nlogn)，空间复杂度为O(n)，n为old的文件长度，BSDiff采用了Fast suffix sorting方法生成后缀数组<br>
关于后缀数组概念，需要懂得非比较排序算法：<br>

- [非比较排序之计数排序](http://xusx1024.com/2017/04/11/counting-sort/)
- [非比较排序之桶/箱排序](http://xusx1024.com/2017/04/10/bucket-sort/)
- [非比较排序之基数排序](http://xusx1024.com/2017/04/10/radix-sort/)

需要懂得生成后缀数组算法：<br>

- [基于倍增算法实现后缀数组](http://xusx1024.com/2017/04/11/suffix-sort-baseon-prefix-doubleing/)
- [基于DC3算法实现后缀数组](http://xusx1024.com/2017/04/13/suffix-sort-baseon-dc3/)