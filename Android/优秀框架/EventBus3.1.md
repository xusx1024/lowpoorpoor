##  EventBus 3 全解

[TOC]

### 使用

一个基于观察者模式的事件发布/订阅框架. 用于模块间通信和解耦, 使用方便,性能高.

#### 基本使用

##### 1. gradle导入依赖库

   ```
   implementation 'org.greenrobot:eventbus:3.1.1'
   ```

##### 2. 定义事件类

```java
   public class MessageEvent {
   
   public final String message;
   
   	public MessageEvent(String message) {
   		this.message = message;
   	}
   }
```

##### 3. 标记订阅方法. 当自定义事件发布后,此方法会被调用.
```
   // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onMessageEvent(MessageEvent event) {
   		Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
   }
         
   // This method will be called when a SomeOtherEvent is posted
   @Subscribe
   public void handleSomethingElse(SomeOtherEvent event) {
       doSomethingWith(event);
   }
```

##### 3.1 注册

```
      @Override
      public void onStart() {
      	super.onStart();
      	EventBus.getDefault().register(this);
      }
```

##### 3.2 注销

```
      @Override
      public void onStop() {
      	super.onStop();
      	EventBus.getDefault().unregister(this);
      }
```

##### 4. 发送事件

```
      EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
```

##### 5. 混淆

```
      -keepattributes *Annotation*
      -keepclassmembers class * {
          @org.greenrobot.eventbus.Subscribe <methods>;
      }
      -keep enum org.greenrobot.eventbus.ThreadMode { *; }
       
      # Only required if you use AsyncExecutor
      -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
          <init>(java.lang.Throwable);
      }
```
#### Android平台最佳实践

​      使用`Subscriber Index` , 在编译时为注册类和订阅构建了一个索引, 这样不必在运行时通过反射寻找订阅方法了. 对比入下图:

![](http://cdn.xushengxing.info/event_bus_subscriber_index.png)

##### 生成索引, `gradle`配置如下:

###### 1.  借助`AnnotationProcessor` 

   ```
   android {
       defaultConfig {
           javaCompileOptions {
               annotationProcessorOptions {
                   arguments = [ eventBusIndex : 'com.example.myapp.MyEventBusIndex' ]
               }
           }
       }
   }
    
   dependencies {
       implementation 'org.greenrobot:eventbus:3.1.1'
       annotationProcessor 'org.greenrobot:eventbus-annotation-processor:3.1.1'
   }
   ```

###### 2. 如果使用`kotlin` , 使用`kapt`替代`annotationProcessor`

   ```
   apply plugin: 'kotlin-kapt' // ensure kapt plugin is applied
    
   dependencies {
       implementation 'org.greenrobot:eventbus:3.1.1'
       kapt 'org.greenrobot:eventbus-annotation-processor:3.1.1'
   }
    
   kapt {
       arguments {
           arg('eventBusIndex', 'com.example.myapp.MyEventBusIndex')
       }
   }
   ```

###### 3. 如果`gradle`版本小于`2.2`, 可以使用`android-apt`

   ```
   buildscript {
       dependencies {
           classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
       }
   }
    
   apply plugin: 'com.neenbedankt.android-apt'
    
   dependencies {
       compile 'org.greenrobot:eventbus:3.1.1'
       apt 'org.greenrobot:eventbus-annotation-processor:3.1.1'
   }
    
   apt {
       arguments {
           eventBusIndex "com.example.myapp.MyEventBusIndex"
       }
   }
   ```

> 截止2019年9月20日, `gradle`配置用第一种就可以, 如果有特殊情况, 参考2, 3. 编译成功后, 会在如下目录生成`.\app\build\generated\source\apt\debug\com\example\myapp\MyEventBusIndex.java`  , 如果没报错, 也没生成, 删掉`build` 文件夹, 再来一次.

##### 使用索引

方式一 :

```
EventBus eventBus = EventBus.builder().addIndex(new MyEventBusIndex()).build();
```

方式二 :

```
EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
// Now the default instance uses the given index. Use it like this:
EventBus eventBus = EventBus.getDefault();
```

##### 索引库

如果项目的依赖库中使用了索引, 可以添加多个索引:

```
EventBus eventBus = EventBus.builder()
    .addIndex(new MyEventBusAppIndex())
    .addIndex(new MyEventBusLibIndex()).build();
```

### 原理

![](<https://raw.githubusercontent.com/greenrobot/EventBus/master/EventBus-Publish-Subscribe.png>)



#### 流程图

##### 注册

把事件和订阅者对应起来, 一个事件可以有多个订阅者, 一个订阅者可以订阅多个事件.

 ![](http://cdn.xushengxing.info/EventBus_register.png)

##### 发送事件

根据事件, 找到订阅者,并执行订阅者对应的方法. 

![](http://cdn.xushengxing.info/EventBus_post.png)



##### 注销

注销是移除订阅的操作. 

根据注册的步骤可知:

```
typesBySubscriber = HashMap<订阅者, List<事件>>
subscriptionsByEventType = HashMap<事件, List<订阅者>>
```

有这样两个变量维持所有的事件和订阅者的对应关系, 所以循环找到传入的订阅者, 调用`Map.remove()`.

### 设计模式

#### 单例

一个`Double Check`:

```
    public static EventBus getDefault() {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }
```

#### 构建者

```
    EventBus(EventBusBuilder builder) {
        logger = builder.getLogger();
        subscriptionsByEventType = new HashMap<>();
        typesBySubscriber = new HashMap<>();
        stickyEvents = new ConcurrentHashMap<>();
        mainThreadSupport = builder.getMainThreadSupport();
        mainThreadPoster = mainThreadSupport != null ? mainThreadSupport.createPoster(this) : null;
        backgroundPoster = new BackgroundPoster(this);
        asyncPoster = new AsyncPoster(this);
        indexCount = builder.subscriberInfoIndexes != null ? builder.subscriberInfoIndexes.size() : 0;
        subscriberMethodFinder = new SubscriberMethodFinder(builder.subscriberInfoIndexes,
                builder.strictMethodVerification, builder.ignoreGeneratedIndex);
        logSubscriberExceptions = builder.logSubscriberExceptions;
        logNoSubscriberMessages = builder.logNoSubscriberMessages;
        sendSubscriberExceptionEvent = builder.sendSubscriberExceptionEvent;
        sendNoSubscriberEvent = builder.sendNoSubscriberEvent;
        throwSubscriberException = builder.throwSubscriberException;
        eventInheritance = builder.eventInheritance;
        executorService = builder.executorService;
    }
```

#### 观察者

不多说了.看上面原理图.

### 数据结构

`ThreadLocal`

 [CopyOnWriteArrayList](<https://my.oschina.net/9thshotsun/blog/3107232>)

### 问题

##### 不支持跨进程

[试试HermesEventBus](https://github.com/Xiaofei-it/HermesEventBus)

##### 事件环路

把接收事件专门封装成一个子模块，同时考虑避免出现事件环路。

##### 事件满天飞,维护困难

使用`RxJava`这样就可以使用`RxBus`了 :D

#####  事件的区分只能通过类名来区分

##### 事件只能单向广播, 无法获取接收者的处理结果, 如果处理完了再post消息,出现事件环路

##### 没有独占消息, 都是多对多的

##### 注解 + 显示注册, 一般来说已经加上注解了, 就不再需要显示注册了

##### sticky事件需要缓存, 如果粘性事件过多, 过多或过于频繁的申请粘性监听, 肯定会引起效率问题









### liveData

好东西, 和`Android`组件的生命周期紧密联系, 不需要手动处理生命周期, 因此不会造成内存泄露.

说实话整个`Android Architecture Components`都挺不错的, 就是出来的略晚了一些. 和现在流行的主流框架有些相持, 假以时日, 会越来越流行的.



















