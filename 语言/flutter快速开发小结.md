​		开发flutter项目了，我是Android出身，开发时候，都是由Android和Java类比着去寻找flutter和Dart中对应的控件和语法。

###  flutter中的mvp

​		flutter是UI框架，但又不仅仅是绘制ui。涉及到交互和数据更新的情况下，在app时候还是需要分层，可以理解为在flutter中应用MVP。

​		首先： 页面可以大致分为三类：

		1. 静态展示页面
  		2. 比较少的交互或数据变化的页面
  		3. 较多交互或数据变化的页面

针对这三类，我们有如下处理：

|    静态页面     |    较少交互    |     较多交互      |            更多交互             |
| :-------------: | :------------: | :---------------: | :-----------------------------: |
| StatelessWidget | StatefulWidget |  StatefulWidget   | StatelessWidget/StatefulWidget  |
|   不需要分层    |  使用setState  | 使用StreamBuilder | RxDart + Bloc，高级SteamBuilder |

[参考文章：Flutter中如何利用StreamBuilder和BLoC来控制Widget状态](https://www.jianshu.com/p/024b19dea138)

### flutter和本地交互 - 插件开发

flutter可以称为client，native可以称为host。

client通过host可以做两件事情：

1. 调用平台独有的api：比如电量状态，权限申请等。
2. 调用平台独有的依赖库：比如微信登录，分享，支付等。

这里只说Android和Flutter通信的方式。

1. flutter页面初始化传值，简单传递字符串，无返回值
2. EventChannel，数据单向传递，无返回值
3. MethodChannel，数据双向传递，有返回值
4. BasicMessageChannel， 数据双向传递，有返回值，支持数据类型最多。

[参考文章：一篇看懂Android与Flutter之间的通信](https://cloud.tencent.com/developer/article/1450592)

### flutter中的异步

###  flutter中的页面跳转-路由

###  flutter中的签名&打包

###  flutter中的数据库

### 好文

下面的文章，多读读，有助于理解flutter的实现原理和设计理念。

- [深入理解Flutter引擎线程模式](https://mp.weixin.qq.com/s/hZ5PUvPpMlEYBAJggGnJsw) 
- [Dart 异步编程详解之一文全懂](https://juejin.im/post/5cdbf2e3f265da035632570e#heading-11)
- [flutter核心原理](https://book.flutterchina.club/chapter14/)
- [Effective Dart](https://dart.dev/guides/language/effective-dart)



shunyoushouka://splash



double --> float

byte

short 

int 

long 

boolean

character