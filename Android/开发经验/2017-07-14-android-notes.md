---
title: android - 消除非受检的警告
date: 2017-07-14
tags: android
categories: Android 
---

#### using setjavascriptenabled can introduce xss vulnerabilities into your application ####

在WebView中，如果你启用js，kotlin代码如下：

	webview.settings.javaScriptEnabled = true

会得到题目的警告提示。

原因是：启用js可能不安全，如果你真的需要，好好检查这些js。
消除：

	@SuppressLint("SetJavaScriptEnabled")

####  `WebView.addJavascriptInterface` should not be called with minSdkVersion < 17 for security reasons: JavaScript can use reflection to manipulate application ####

在WebView中，如果添加JS调用Java代码，kotlin代码如下：

	webview.addJavascriptInterface(JsObject(), "onClick")

会得到题目的警告提示。

原因题目说的很清楚，“JavaScript可以通过反射操作应用”

官方的说明：[JavaScript安全不](https://developer.android.com/reference/android/webkit/WebView.html#addJavascriptInterface%28java.lang.Object,%20java.lang.String%29)

一篇不错的[blog](http://blog.csdn.net/leehong2005/article/details/11808557)

可以确定的是4.2，也就是17，JELLY BEAN之前，可以获取到SD读写权限，然后操作你的联系人、短信、etc

这个问题相当的经典。一般的应用估计都不会修复它。但如果应用用户太大，不得不修复。


#### 加载JS的时机 ####

引申问题，你有一段JS，何时加载？

- onLoadResource
- doUpdateVisitedHistory
- onPageStarted
- onPageFinished
- onReceivedTitle
- onProgressChanged

#### setRenderPriority(android.webkit.WebSettings.RenderPriority)' is deprecated ####

源码中：

> It is not recommended to adjust thread priorities, and this will not be supported in future versions.

#### webView 加载缓慢 ####

	 if (Build.VERSION.SDK_INT >= 19) {//4.4，KK
	            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
	        }
	        else {
	            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	        }

#### This Handler class should be static or leaks might occur (anonymous android.os.Handler) ####

我们在使用Handler发送消息时，往往使用内部类来实现Handler，这样可能导致内存泄露。

原因：

handler实例做为一个嵌套类，确切的说是做为一个内部类时，默认持有外部类的引用，

假设外部类为`SampleActivity`，并且Handler里有延时执行的任务，并且Activity提前finish

这样，由于activity的引用还在Handler的延时任务里，GC判定，该Activity虽然finish，但是不可回收，此时就发生了内存泄露

解决：

Handler使用静态内部类

Handler类中使用弱引用维护对activity的引用：
	
	private static class MyHandler extends Handler {
	    private final WeakReference<SampleActivity> mActivity;
	
	    public MyHandler(SampleActivity activity) {
	      mActivity = new WeakReference<SampleActivity>(activity);
	    }
	
	    @Override
	    public void handleMessage(Message msg) {
	      SampleActivity activity = mActivity.get();
	      if (activity != null) {
	        // ...
	      }
	    }
	  }
	
	  private final MyHandler mHandler = new MyHandler(this);

[E文传送](http://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html)
[译文传送](http://www.jianshu.com/p/1b39416f1508)


#### Warning:(168, 72) Unchecked assignment: 'java.util.List' to 'java.util.List<com.xxx.greendao.xxBean>'. Reason: 'queries' has raw type, so result of list is erased ####

只能添加指定类型，不会有警告：

	  ArrayList<String> arr = new ArrayList<String>();

如果添加了String之外的类型，会有编译时警告：

	  ArrayList<String> arr = new ArrayList();

这就是raw type，可以添加Object类型的对象，此时会提示`Unchecked assignment...`，

	  ArrayList arr = new ArrayList<String>();

考察如下DEMO，可以编译通过，但是运行异常：
	
	import java.util.ArrayList;
	
	public class TestRawType {
	
		public static void main(String[] args) {
			  ArrayList arr = new ArrayList<String>();
			  
			  arr.add("qwer");
			  arr.add(1234);
			  arr.add(new Integer(2));
			  
			  System.out.println(arr.toString());
			
			  for(Object o : arr){
				  System.out.println((String)o);
			  }
		}
	
	}


> 控制台输出：[qwer, 1234, 2]
qwer
Exception in thread "main" java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
	at TestRawType.main(TestRawType.java:17)


更加详细的解释，参考《effective java》 2nd 第23条：请不要在新代码中使用原生态类型


####  Static member 'android.support.v4.app.FragmentActivity.RESULT_OK' accessed via instance reference ####

原因：通过实例引用了某个类的静态变量，如下：

	getActivity().RESULT_OK

修改：
	
	Activity.RESULT_OK;

`update @ 2017年7月14日18:50:52`


####  Dangling Javadoc comment ####

原因：悬空的注释（在你导出文档时，可能会丢失）










