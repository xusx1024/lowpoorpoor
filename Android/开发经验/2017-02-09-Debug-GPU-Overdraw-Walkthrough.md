---
layout: post
title:  Android过度绘制优化
date:   2017-02-09
categories: Android
tag: android
---

 

#### what ####
 

##### 过渡绘制 #####
 

	过渡绘制(OverDraw)，屏幕上的某个像素，在同一帧内被重绘多次。如果App中过渡绘制问题严重，会浪费大量CPU、GPU资源，直接表现为App卡顿。

##### 开启 #####

	
	开发者选项 - 调试GPU过渡绘制

##### 解释 #####


![示例图](../res/img/color-key-for-debug-GPU-overdraw-output.png)

- True color: No overdraw
- Blue: Overdrawn once
- Green: Overdrawn twice
- Pink: Overdrawn three times
- Red: Overdrawn four or more times

#### how ####

	

1.  >去掉window默认背景。在Activity中，使用setContentView绑定布局时，android会自动填充一个背景，如果我们App有自己的页面底色，因此不需要默认分配的背景。
	>
    	修改页面的主题：
		<item name="android:windowBackground">@android:color/transparent</item>
    	<item name="android:windowBackground">@null</item>
	>		
		代码中修改：
		getWindow().setBackgroundDrawable(null)
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

	>	 null和transparent差别不大，如果有activity右滑返回功能，null,在某些机型(Nexus6p)上，会出现错乱, 建议使用transparent。 

2. >移除不必要的背景，xml布局编码时，防止背景重复，比如ListView的背景和item的背景都是白色，那么可以选择去掉其中一个。一般情况下，背景重复会引起大部分重绘问题。

3. >选择适当父布局。LinearLayout、ReleativeLayout，最常用的两种布局。RL表达能力强，LL易用，层级多，所以在层级相同的情况下，使用LL，尽量不要使用AbsoluteLayout。

4. >对于那些过于复杂的自定义的View(通常重写了onDraw方法)，我们可以通过[canvas.clipRect()](https://developer.android.com/reference/android/graphics/Canvas.html)来帮助系统识别那些可见的区域。这个方法可以指定一块矩形区域，只有在这个区域内才会被绘制，其他的区域会被忽视。我们还可以使用[canvas.quickreject()](https://developer.android.com/reference/android/graphics/Canvas.html)来判断是否没和某个矩形相交，从而跳过那些非矩形区域内的绘制操作。

5. >使用merge标签，减少布局嵌套层次。

6. >使用动态inflate或者ViewStub替代setVisibility。

7. >对于使用Selector当背景的Layout(比如ListView的Item,会使用Selector来标记点击,选择等不同的状态),可以将normal状态的color设置为”@android:color/transparent”,来解决对应的问题。

8. >使用.9图，透明区域会被android的2D渲染器优化。

9. >好的展示设计和交互。使UI宽而浅而不是浅而深。


#### 标准 ####

		
	  过度绘制不可避免
	
	  尽量控制在绿色及其以下
	
	  尽可能避免出现粉红及以上，如果不可避免，尽可能减少

	  不允许出现超过屏幕1/4的红色区域
   