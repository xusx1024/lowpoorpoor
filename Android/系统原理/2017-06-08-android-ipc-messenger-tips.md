---
title: 使用Messenger进行进程间通信注意事项
date: 2017-06-08 15:49:35
tags: android
categories: Android 
---

#### 需求 ####

![需求示意图](../res/img/auth_sequence_diagram.png)

看时序图比较简单，进程间通信，把APPID传过去，把授权码带回来
本来懒得费事，一个`startActivityForResult`即可结束战斗
后来发现，我的SDK里没有UI，也就没有处理`onActivityResult`的地方了，我想过开启一个no ui的Activity，后来还是不要这么简单粗暴了。

#### Messenger的使用 ####
此处不是我想略，网上教程很多，看个比较完善简单的：[Android 基于Message的进程间通信 Messenger完全解析](http://blog.csdn.net/lmj623565791/article/details/47017485)
代码有些地方不适合新的Android版本，但是也够用了。

#### 注意事项 ####

##### 需求问题解决 ####
SDK发送至目标app的一个远程服务，该服务启动授权activity，获取code后，也绑定式启动该服务，找到SDK的信使，把code回传。
其中，通信双方其实是SDK和授权页，远程服务在授权app中，起到一个中间桥梁的作用。
kotlin代码如下：

    var remoteMsg: Messenger? = null//全局变量，用于保存SDK端的信使，等待授权页的结果回来后，使用其发送msg
    private val mMessenger = Messenger(object : Handler() {
        override fun handleMessage(msgfromClient: Message) {
            val msgToClient = Message.obtain(msgfromClient)//返回给客户端的消息
            try {
                when (msgfromClient.what) {
                    REMOTE_CLIENT -> {//远程访问-SDK
                        remoteMsg = msgfromClient.replyTo
                        var intent: Intent = Intent()
                        val bundle: Bundle = msgfromClient.obj as Bundle
                        val appID: String = bundle.get("APPID") as String

                        //开启授权页Activity
                        intent.setClass(this@AuthMessengerService, AuthActivity::class.java)
                        intent.putExtra("appID", appID)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    LOCAL_CLIENT -> {//本地访问-授权页
                        val bundle: Bundle = msgfromClient.obj as Bundle
                        msgToClient.obj = bundle//拿到授权页信使带来的code
                        remoteMsg!!.send(msgToClient)//使用SDK端的信使发送，即发送给SDK端
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

            super.handleMessage(msgfromClient)
        }
    })


根据代码，可以看到SDK和service是双向通信，授权页和service是单向通信。因此授权页通信完成后立刻unbind即可。

##### 单向双向 #####
	msgFromLocalClient.replyTo = mMessenger//单向通信,不需要回传信使
这句话决定了是否需要信使带回结果，如果需要，在mMessenger 里处理。在示例blog中类似代码可以参考。

##### 多向通信 ####
在这个里[Remote Messenger Service Sample](https://developer.android.com/reference/android/app/Service.html#RemoteMessengerServiceSample)可以看到多个客户端互相通信的官方示例。
官方示例应该只适应于多对一，如果多对多的话，自个儿实现AIDL比较靠谱。

##### 隐式启动service #####

 5.0之后需要：

		Intent intent = new Intent();
		intent.setAction(action);
		intent.setPackage(pkgName);
		context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);

##### 魅族手机无法启动 #####
设置app权限，允许后台运行即可。话说这手机牌子坑爹。


##### Can't marshal non-Parcelable objects across processes #####
msg里没几个参数，如果要传的东西多，就放入Bundle里面，然后赋值给msg.obj。或者你的自定义对象是Parcelable的也可以直接放入msg.obj。
 
 
##### MainActivity has leaked ServiceConnection #####
页面关闭时，记得unbind