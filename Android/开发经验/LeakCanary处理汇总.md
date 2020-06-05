一：引入

``` debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.3' ```
和以前不同，不需要在`Application`中初始化。
我猜测是和`AutoSize`一样使用了`ContentProvider`进行初始化，验证ok。

另外我猜测使用了`ActivityLifeCycleCallback`注册生命周期，源码中并未实现此接口，根据其文档说明，Hook了Activity的各个生命周期，此处未验证。

二：通常引起泄漏的原因

1. 添加一个`Fragment`实例到回退栈中，在`Fragment.onDestoryView()`中却没有清除`Fragment$view`字段的引用。详见[Stackoverflow](https://stackoverflow.com/questions/59503689/could-navigation-arch-component-create-a-false-positive-memory-leak/59504797#59504797)
   一种情形，一个Activity配合多个Fragment，`addToBackStack` 行为会导致如下结果：view已经被销毁，Fragment在OnCreated状态等待复用，直到退出该Activity。 
   因此，必须在OnDestroyView中取消对视图的引用，因为这表明该视图不再被Fragment系统使用，并且可以将其安全地进行垃圾回收。
2. 将Activity实例存储为对象中的Context字段，该对象在由于配置更改而导致的活动重新创建后仍然存在。
3. 注册了监听，广播接收者，RxJava的订阅者，忘记解除注册。

三：实战

1. 静态Context
    MapUtil中使用了静态变量Context，我在调用页面结束的时候onDestroy中将该变量置为null。
2. DialogFragment的OnCancelListener/onDismissListener
 >
 
    a thread waiting on a blocking queue will leak the last dequeued object as a stack local reference . 
     
     So when a HandlerThread becomes idle, it keeps a local reference to the last message it received. 
     
     that message then gets recycled and can be used again. 
     
     as long as all message are recycled after being used, this won’t be a problem , 
     
     because these references are cleared when being recycled. 
     
     However, dialogs create when a message needs to be sent. 
     
     These Message templates holds references to the dialog listeners , 
     
     which most likely leads to holding a reference onto the activity in someway.
     
     Dialogs never recycle their template Message, assuming these Message instances will get GCed when the dialog is GCed. 
     
     the combination of the these two things creates a hingh potential for memory leaks as soon as you use dialogs. 
     
     these memory leaks might be temporary, but some handler threads sleep for a long time.
     
     To fix this, you could post empty messages to the idle handler threads from time to time. 
     
     this won’t be easy because you cannot access all handler threads, but a library that is widely used should consider doing this for its own handler threads. 
 
我的处理方式：
   1. 在调用dialog.dissmiss()之后，把OnCancelListener/onDismissListener置为null。
   
   2. Looper.myLooper().quitSafely();
    
其他处理方式：       
[该blog替代了DialogFragment中的handler](https://www.jianshu.com/p/742279658ee0)
    
    然而源码中明确表示：
>
    <p><em>Note: DialogFragment own the {@link Dialog#setOnCancelListener
            Dialog.setOnCancelListener} and {@link Dialog#setOnDismissListener
            Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em>
另外：
   
    还有人在DialogFragment的onCreateView中设置setOnCancelListener(null)        
            
3. Handler leak
    静态内部类  + 软引用存储上下文
4. Thread + RunOnUiThread 泄漏
    在onDestory中调用ExecutorService.shutDown()，或者Handler.removeCallback(runnable)
 
5. InputManager的部分字段{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"}造成内存泄漏
    反射这些字段，设置imm为null。 详见
    

> onSaveInstance 把数据存放在Binder事务缓冲区中。目前Binder事务缓冲区为1MB，由进程中正在处理的所有事务共享。
>
>对于 savedInstanceState 的具体情况，应将数据量保持在较小的规模，因为只要用户可以返回到该 Activity，系统进程就需要保留所提供的数据（即使 Activity 的进程已终止）。
>我们建议您将保存的状态保持在 50k 数据以下。 
>
>Binder 事务缓冲区的大小固定有限，目前为 1MB，由进程中正在处理的所有事务共享。
>由于此限制是进程级别而不是 Activity 级别的限制，因此这些事务包括应用中的所有 binder 事务，
>例如 onSaveInstanceState，startActivity 以及与系统的任何互动。超过大小限制时，将引发 TransactionTooLargeException。 
>
>
