---
layout: post
title:  Android开发中特别的api
date:   2017-04-21
categories: Android 
tag: android
---
 
#### 前言 ####
 android库很大，开发中会遇到一些不常用的api，记录在此。
 

#### ActionBar相关 ####

- setHomeButtonEnabled这个小于4.0版本的默认值为true的。但是在4.0及其以上是false，该方法的作用：决定左上角的图标是否可以点击。没有向左的小图标。 true 图标可以点击  false 不可以点击
- actionBar.setDisplayHomeAsUpEnabled(true)    // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
- actionBar.setDisplayShowHomeEnabled(true)   //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，对应id为Android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
- actionBar.setDisplayShowCustomEnabled(true)  // 使自定义的普通View能在title栏显示，即actionBar.setCustomView能起作用，对应ActionBar.DISPLAY_SHOW_CUSTOM
- actionBar.setDisplayShowTitleEnabled(true)   //对应去掉ActionBar是否显示


#### 判断Activity是否仍在运行 ####

	if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
        return;
    }

#### 开关使用ToggleButton或者Switch ####


 

 
 