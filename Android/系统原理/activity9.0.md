# Activity 9.0

[TOC]



##  1. 入口(Activity)

`android/platform/frameworks/base/master/./core/java/android/app/Activity.java`

> 简述：启动一个活动，在`Activity$startActivity`  ---> `Activity$startActivityForResult` --->  `Instrumentation$execStartActivity`. 得到一个`ActivityResult`。

我们经常调用`startActivity(intent)`来开启一个活动。（注：为保证流程的连贯性，扩展可以略过）

### 1.1 startActivity 

> startActivity: 启动一个新的活动。当活动退出，你不会收到任何信息。该实现重写了基类(`Context$startActivity`)版本，提供了启动活动相关的信息。由于这些增加的信息，不再需要`FLAG_ACTIVITY_NEW_TASK`启动标记了。如果未指定，新的活动被添加到调用者的任务栈中。

```java
@Override
public void startActivity(Intent intent, @Nullable Bundle options){
    if(options != null){
        startActivityForResult(intent, -1, options);
    }else{
        startActivityForResult(intent, -1);
    }
}
```



#### 1.1.1 扩展 ：ActivityOptions

先来看一个方法：

```java
public void overridePendingTransition(int enterAnim, int exitAnim){
    try {
        ActivityManager.getService().overridePendingTransition(mToken, enterAnim, exitAnim);
    }catch (RemoteException e){
    }
}
```

应用开发中，上述方法用来设置Activity的转场动画。如果没有指定，系统默认如下：

`ActivityOptions$makeCustomAnimation`

```java 
public static makeCustomAnimation(Context context,
int enterResId, int exitId, Handler handler, OnAnimationStartedListener listener) {
    ActivityOptions opts = new ActivityOptions();
    opts.mPackageName = context.getPackageName();
    opts.mAnimationType = ANIM_CUSTOM;
    opts.mCustomEnterResId = enterResId;
    opts.mCustomExitResId = exitResId;
    opts.setOnAnimationStartedListener(listener);
    return opts;
}
```

在材料设计中，还可看到更多漂亮的转场动画，比如联系人列表进入详情的头像伸缩变换转场动画。点击一个条目，头像会扩大为大图到详情的活动页面，从详情返回列表，大图又缩小为小图。系统实现如下：

```java
private static ActivityOptions makeThumbnailAnimation(View source, Bitmap thumbnail,
int startX, int startY, OnAnimationStartedListener listener, boolean scaleUp) {
    ActivityOptions opts = new ActivityOptions();
    opts.mPackageName = source.getContext().getPackageName();
    opts.mAnimationType = scaleUp ? ANIM_THUMBNAIL_SCALE_UP : ANIM_THUMBNAIL_SCALE_DOWN;
    opts.mThumbnail = thumbnail;
    int[] pts = new int[2];
    source.getLocationOnScreen(pts);
    opts.mStartX = pts[0] + startX;
    opts.mStartY = pts[1] + startY;
    opts.setOnAnimationStartedListener(source.getHandler(), listener);
    return opts;
}
```

注意，这是`private`方法，配合`ActivityOptionsCompat`使用。

[官方教程](<https://developer.android.com/training/transitions/start-activity.html>)

### 1.2 startActivityForResult

```java
pbulic void startActivityForResult(@RequiresPermission Intent intent, int requestCode, @Nullable Bundle options){
    // app冷启动的首个Activity，mParent为null
    if(mParent == null){
        // options 为null的时候，使用系统默认，如1.1.1所述
        options = transferSpringboardActivityOptions(options);
        // * 关键方法，这里返回一个ActivityResult（描述了活动执行的结果，并返回给原始活动）
        Instrumentation.ActivityResult ar = 
            mInstrumentation.execStartActivity(
            	this,mMainThread.getApplicationThread(),mToken,this,
            	intent,requestCode,options);
        if(ar != null){
            // mMainThread 是ActivityThread的一个实例，在ActivityThread中调用performLaunchActivity时候，反射生成Activity的实例，然后调用Activity$attach方法，把MMainThread实例传入Activity
            mMainThread.sendActivityResult(
            	mToken, mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
        }
        // 请求码不小于0的话，在活动退出时候，会被返回给`onActivityResult()`方法
        if(requestCode >= 0){
            // 如果这次启动正在请求一个响应结果，系统可以阻止活动可见，直到收到结果。
            // 在`onCreate`, `onResume`方法中`startActivityForResult` 设置这个`requestCode`，会保证活动不可见并且避免页面闪烁。
            // 只有在请求到结果时才能完成这些，因为，这样保证了无论发生什么都可以在活动结束后获取到信息。
            mStartedActivity = true;
        }
        // 取消输入事件，开始转场动画
        cancelInputsAndStartExitTransition(options);
    }else{
        if(options != null){
            mParent.startActivityFromChild(this, intent, requestCode, options);
        }else{
            mParent.startActivityFromChild(this, intent, requestCode);
        }
    }
}
```

#### 1.2.1 扩展：mMainThread的附着流程

mMainThread是主线程的实例，在一个新的活动实例创建后附着。

系统在`ActivityThread$PerformLaunchActivity`中，使用`Instrumentation$newActivity`反射生成`Activity`实例，然后调用`Activity$attach`把当前线程实例传给生成的最新`Activity`。

```java
public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    String pkg = intent != null && intent.getComponent() != null ? intent.getComponent().getPackageName() : null;
    return getFactory(pkg).instantiateActivity(cl, className, intent);
}  

```

`Activity$newActivity` ---> `AppcomponentFactory$instantiateActivity`如下：

```java
public @NonNull Activity instantiateActivity(@NonNull ClassLoader cl, @NonNull String className, @NonNull Intent intent) throws InstaniationException, IllegalAccessException, ClassNotFoundExcption {
    return (Activity)cl.loadClass(cl).newInstance();
}
```

`instantiateActivity()`允许应用程序重写活动的创建。可用来生成依赖注入或类加载器修改这些类。

#### 1.2.2 扩展：在DecorView上cancelInput

取消输入，指的是取消`DecorView`上的点击和长按事件。

a) Activity

```java
// Activity$cancelPendingInputsAndStartExitTransition
private void cancelPendingInputsAndStartExitTransition(Bundle options) {
    // I: mWidow.peekDecorView() 通过mWindow(PhoneWindow的实例)获取View的一个实例DecorView
    final View decor = mWindow != null ? mWindow.peekDecorView() : null;
    if (decor != null){
        decor.cancelPendingInputEvents();
    }
    if (options != null && !isTopOfTask()) {
        mActivityTransitionState.startExitOutTransition(this, options);
    }
}
```

 `cancelPendingInputsAndStartExitTransition()`方法中出现了比较重要的角色：`mWindow`.

在`attach()`方法中有：`mWindow = new PhoneWindow(this, window, activityConfigCallback);`

`attach()`又是在哪里调用的？在`ActivityThread$performLaunchActivity()`.

在`Activity$onCreate()`中，我们经常`setContentView()`,具体的实现就是在`PhoneWindow$setContentView`中

`PhoneWindow`会在`setContentView`的时候检测`DecorView`的实例是否存在，如果否，则使用`installDecor`创建。



b) View(DecorView)

```java
// View$onCancelPendingInputEvents
public void onCancelPendingInputEvents() {
    removePerformClickCallback();
    cancelLongPress();
    mPrivateFlags3 |= PFLAG3_CALLED_SUPER;
}
```

`DecorView`是`FrameLayout`的子类。

`DecorView`初始化调用过程：

- `Activity$setContentView`或`Activity$addContentView`
- `PhoneWindow$installDecor`  ---> `PhoneWindow$generateDecor` --->`PhoneWindow$generateLayout()` --->`DecorView$onResourcesLoaded`

## 2.启动前监控(Instrumentation)

`android/platform/frameworks/base/master/./core/java/android/app/Instrumentation.java`

从`Activity` 到`Instrumentation`。

### 2.1 execStartActivity 

```java
/**
* 
* @param who 开启当前活动的上下文
* @param contextThread 开启当前活动的上下文的主线程
* @param token 开启当前活动的内部令牌，由系统识别，可以为空
* @param target 开启从而获取`ActivityResult`的活动，如果当前不是从活动调用的该方法，可以为空
* @param intent 开启活动的具体意图
* @param requestCode 此次请求结果的识别码，小于0说明调用者不期望请求结果
* @param option 附加选项，见1.1.1
* 
* @return 返回包含想要的数据的`ActivityResult`，默认返回空
* 
*/
public ActivityResult execStartActivity(
    Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) {
    IApplicationThread whoThread = (IApplicationThread) contextThread;
    // 来源，应用当前展示内容的来源。默认返回`null`，即来源为：当前活动的包名。如果不为空，配合`Intent.EXTRA_REFERRER`使用
    Uri referrer = target != null ? target.onProvideReferrer() : null;
    if (referrer != null) {
        intent.putExtra(Intent.EXTRA_REFERRER, referrer);
    }
    // ActivityMonitor 配合Instrumentation测试框架使用
    if (mActivityMonitors != null) {
        synchronized (mSync) {
            final int N = mActivityMonitors.size();
            for (int i = 0; i < N; i++){
                final ActivityMonitor am = mActivityMonitor.get(i);
                ActivityResult result = null;
                if (am.ignoreMatchingSpecificIntents()) {
                    result = am.onStartActivity(intent);
                }
                if (result != null) {
                    am.mHits++;
                    return result;
                } else if (am.mathch(who, null, intent)) {
                    am.mHits++;
                    if (am.isBlocking()) {
                        return requestCode >= 0 ? am.getResult : null;
                    }
                    break;
                }
            }
        }
    }
    try {
        // 当`ACTION_SEND`,`ACTION_SEND_MULTIPLE`,`ACTION_CHOOSER`时，迁移`EXTRA_STREAM`到`ClipData`。ClipData作用是在剪贴板展示剪切的数据
        intent.migrateExtraStreamToClipData();
        // 准备打开文件选择器或者剪贴板，这是7.0之后的FileProvide功能。比如启用打开相册的意图时。
        intent.papreToLeaveProcess(who);
        // 进入ActivityManagerService进行下一步工作
        int result = ActivityManager.getService()
            .startActivityAsUser(whoThread, who.getBasePackageName(), intent, intent.resolveTypeIfNeeded(who.getContentResolver()), token, resultWho, requestCode, 0, null, options, user.getIndentifier());
        // 检查ActivityResult的返回代码和ActivityManager定义的那些错误常量是否匹配，并抛出响应的异常
        checkStartActivityResult(result, intent);
    } catch (RemoteException e) {
        throw new RuntimeException("Failure from system", e);
    }
    return null;
}
```

#### 2.1.1 扩展：android.util.Singleton

在`execStartActivity()`，看到`ActivityManager.getService()`，获取`ActivityManagerService`的单例实例。代码：

```java
public abstract class Singleton<T> {
    private T mInstance;
    protected abstract T create();
    public final T get() {
        synchronized (this){
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}
```

```java
private static final Singleton<IActivityManager> IActivityManagerSingleton = 
    new Singleton<IActivityManager>() {
  		@Override
    protected IActivityManager create() {
        final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
        final IActivityManager am = IActivityManager.Stub.asInterface(b);
        return am;
    }
};
```

## 3. 初始化ActivityStarter(ActivityManagerService)

从`Instrumentation$execStartActivity` 到`ActivityManagerService$startActivity`

### 3.1`startActivityAsUser`：

```java
/**
*
* @param caller
* @param callingPackage
* @param intent
* @param  resolveType
* @param resultTo
* @param resultWho
* @param requestCode
* @param startFlags
* @param profilerInfo
* @param bOptions
* @param userId
* @param validateIncomingUser
*
*/
public final int startActivityAsUser (IApplicationThread caller, String callingPackage, Intent 	intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId, boolean validateIncomingUser) {
    // 执行非隔离调用。
    enforceNotIsolatedCaller("startActivity");
    userId = mActivityStartController.checkTargetUser(userId, validateIncomingUser, Binder.getCallingPid(), Binder.getCallingUid, "startActivityAsUser");
    
    return mActivityStartController.obtainStarter(intent, "startActivityAsUser")
        // 设置ApplicationThread
        .setCaller(caller)
        .setCallingPackage(callingPackage)
        .setResolvedType(resolvedType)
        .setResultTo(resultTo)
        .setResultWho(resultWho)
        .setRequestCode(requestCode)
        // 这里是0，默认是0
        .setStartFlags(startFlags)
        // 探查app时的设置，此时为空，默认为空
        .setProfilerInfo(profilerInfo)
        // 见1.1.1
        .setActivityOptions(bOptions)
        // 应该等待开启活动的请求的结果
        .setMayWait(userId)
        .execute();
}
```

#### 3.1.1 扩展：应用沙盒

`Android`平台利用基于用户的`Linux`保护机制来识别和隔离应用资源。`Android``为每个应用分配独一无二的用户ID（UID）`，并在各自的进程中运行。可将应用分开，保护应用和系统免受恶意应用的攻击。

`Android`利用`UID`设置一个内核级应用沙盒。默认情况下，应用不能彼此交互，而且对操作系统的访问权限会受到限制。

由于应用沙盒位于内核层面，因此该安全模型扩展到了原生代码和系统应用。

#### 3.1.2 扩展：`UserHandler`

在设备上表示用户。`3.1.1`说了，应用默认是隔离的，可以使用`shareUid`，来共享资源。`UserHandle`主要涉及`android`中让人眼花的各种`id`管理：

- UserId：android设备上的多用户的用户id
- UID：跟应用进程相关，一旦安装到设备就不会改变，每个用户的每个应用的uid都不一样
- APPID：跟包名有关，包名相同appid不同。即使用户不同，同一个应用appid是一样的

```java
public final class UserHandle implements Parcelable {
    /**
    * @hide Range of uids allocated for a user.
    * 每个用户可以有十万个uid
    */
    public static final int PER_USER_RANGE = 100000;
    
    // 根据Userid 生成uid。加入A用户userId = 0； B为1.appId为10080
    public static int getUid(@UserIdInt int userId, @AppIdInt int appId) {
        if (MU_ENABLE) {
            // A  --> 10080
            // B  --> 100000 + 10080
            return userId * PER_USER_RANGE + (appId % PER_USER_RANGE);
        } else {
            return appId;
        }
    }
              
    // 传入uid获取userid。例如A用户的UID是10080，B用户的UID就是100000 + 10080。
    // 每个用户有十万个uid。即两个用户直接相差100000.
    public static @UserIdInt int getUserId(int uid) {
        if(MU_ENABLE) {
            // A / PER_USER_RANGE = 10080 / 100000 = 0
            // B / PER_USER_RANGE = (100000 + 10080) / 100000 = 1
            return uid / PER_USER_RANGE;
        } else {
            return UserHandle.USER_SYSTEM;
        }
    }
    // 例如A用户的UID是10080，B用户的UID就是100000 + 10080
    public static @AppIdInt int getAppId(int uid) {
        // A % PER_USER_RANGE = 10080 % 100000 = 10080
        // B % PER_USER_RANGE = (100000 + 10080) % 100000 = 10080
        // 因此说包名相同，appid必定相同，跟用户无关。
        return uid % PER_USER_RANGE;
    }
}
```

## 4.启动模式&任务栈ActivityStarter

`ActivityStarter$execu` --> `startActivityMayWait` --> `startActivty` --> `startActivityUnchecked`

> 概述：计算启动模式的标记；创建或复用任务栈

### 4.1 startActivityUnchecked

这个方法接近220行，我们看下重点：

```java
/**
* @parmas 这些参数在前面的调用过程中大都有说明
* @return 返回ActivityManager定义的err code,如果START_SUCCESS = 0
*/
private int startActivityUnchecked(...) {
    // 初始化一些状态
    setInitialState(...);
    // 计算启动模式
    computeLaunchingTaskFlags();
    // 计算源stack，如果源正在结束，和它关联的task也可能为空，就需要用NEW_TASK再建新的task
    compupteSourceStack();
    // 决定这个新活动是否应该插入已存在的任务栈中。不应该的话，返回空。应该的话返回这个任务栈相关的新活动的ActivityRecord。
    ActivityRecord reusedActivity = getReusableIntentActivity();
   
    // ......
    
    // 有可复用的Activity
    if (resuedActivity != null) {
        // 根据启动模式，复用或清除活动
    }
    
    // 开启活动的活动不存在了，重点启动
    if (mStartActivity.packageName == null) {
        // ......
        return START_CLASS_NOT_FOUND;
    }
    
    
    // 如果要启动的活动和当前栈顶的一样，根据启动模式检测是否需要启动。
     final ActivityRecord top = topStack.topRunningNonDelayedActivityLocked(mNotTop);
     final boolean dontStart = top != null && mStartActivity.resultTo == null
                && top.realActivity.equals(mStartActivity.realActivity)
                && top.userId == mStartActivity.userId
                && top.app != null && top.app.thread != null
                && ((mLaunchFlags & FLAG_ACTIVITY_SINGLE_TOP) != 0
                || isLaunchModeOneOf(LAUNCH_SINGLE_TOP, LAUNCH_SINGLE_TASK));
    if (dontStart) {
        // ......
        //不需要重新启动，传一个new intent 过去
        deliverNewIntent(top);
    }
    
    // Should this be considered a new task?
        int result = START_SUCCESS;
        if (mStartActivity.resultTo == null && mInTask == null && !mAddingToTask
                && (mLaunchFlags & FLAG_ACTIVITY_NEW_TASK) != 0) {
            newTask = true;
            // 新的任务栈
            result = setTaskFromReuseOrCreateNewTask(taskToAffiliate, topStack);
        } else if (mSourceRecord != null) {
            // 使用源活动的任务栈
            result = setTaskFromSourceRecord();
        } else if (mInTask != null) {
            // 使用目标活动的任务栈
            result = setTaskFromInTask();
        } else {
            // This not being started from an existing activity, and not part of a new task...
            // just put it in the top task, though these days this case should never happen.
            // 这不是被存在的活动启动的，也不是新任务栈的一部分，把活动放到栈顶吧，尽管这永远不应该发生的
            setTaskToCurrentTopOrCreateNewTask();
        }
        if (result != START_SUCCESS) {
            return result;
        }
    // ActivityStack$startActivityLocked 创建预览窗口
    mTargetStack.startActivityLocked(mStartActivity, topFocused, newTask, mKeepCurTransition,
                mOptions);
        if (mDoResume) {
            final ActivityRecord topTaskActivity =
                    mStartActivity.getTask().topRunningActivityLocked();
            if (!mTargetStack.isFocusable()
                    || (topTaskActivity != null && topTaskActivity.mTaskOverlay
                    && mStartActivity != topTaskActivity)) {
                // If the activity is not focusable, we can't resume it, but still would like to
                // make sure it becomes visible as it starts (this will also trigger entry
                // animation). An example of this are PIP activities.
                // Also, we don't want to resume activities in a task that currently has an overlay
                // as the starting activity just needs to be in the visible paused state until the
                // over is removed.
                // 如果活动不可聚焦就不能恢复焦点了。这仍然可以保证活动可见。比如画中画的活动。同时，不要恢复焦点的活动上现在有覆盖层，保持活动可见和暂停状态，知道覆盖层移除，比如非全屏的提示框。
                mTargetStack.ensureActivitiesVisibleLocked(null, 0, !PRESERVE_WINDOWS);
                // Go ahead and tell window manager to execute app transition for this activity
                // since the app transition will not be triggered through the resume channel.
                // 直接通知窗口管理器执行活动间的动画，因为通过恢复焦点通道去通知执行转场动画已经不会被触发了。
                mService.mWindowManager.executeAppTransition();
            } else {
                // If the target stack was not previously focusable (previous top running activity
                // on that stack was not visible) then any prior calls to move the stack to the
                // will not update the focused stack.  If starting the new activity now allows the
                // task stack to be focusable, then ensure that we now update the focused stack
                // accordingly.
                // 如果目标活动栈不是上一个获取焦点的（栈中前一个栈顶正在运行的活动不可见），前一个移动活动栈的调用不会更新这个已经获取焦点的活动栈。
                if (mTargetStack.isFocusable() && !mSupervisor.isFocusedStack(mTargetStack)) {
                    mTargetStack.moveToFront("startActivityUnchecked");
                }
                mSupervisor.resumeFocusedStackTopActivityLocked(mTargetStack, mStartActivity,
                        mOptions);
            }
        } else if (mStartActivity != null) {
            mSupervisor.mRecentTasks.add(mStartActivity.getTask());
        }
            return START_SUCCESS;
}
```

#### 4.1.1 扩展：ActivityStack、TaskRecord、ActivityRecord、ActivityStackSupervisor关系

![ActivityStack、TaskRecord、ActivityRecord、ActivityStackSupervisor关系图](http://cdn.xushengxing.info/ams_relations.jpg)

- 一个ActivityRecord对应一个Activity信息，一个Activity可能有多个ActivityRecord，因为Activity可以被启动多次
- 一个TaskRecord有多个ActivityRecord，先进后出，称之为任务栈
- ActivityStack 管理多个TaskRecord
- ActivityDisplay主要有Home Stack，App Stack两个栈
- 一般情况下，没有分屏以及虚拟屏的情况下，ActivityDisplay，ActivityStackSupervisor都是系统唯一

#### 4.1.2  ActivityStack$startActivityLocked

启动模式和任务栈确定了。要先显示一个starting window作为预览窗口。

这里涉及到WindowManager，WindowManagerService。

## 5.正式启动(ActivityStackSupervisor & ActivityStack)

`ActivityStackSupervisor$resumeFocusedStackTopActivityLocked` --> `ActivityStack$resumeTopActivityUncheckedLocked` -->  `ActivityStack$resumeTopActivityInnerLocked`

---> `ActivityStackSupervisor$startSpecificActivityLocked`  -->                    `ActivityStackSupervisor$realStartActivityLocked`

### 5.1 `ActivityStack$resumeTopActivityInnerLocked`

```java
 private boolean resumeTopActivityInnerLocked(ActivityRecord prev, ActivityOptions options) {
        // ... 系统没在启动中并且系统也不是已启动，返回false
        
       // 找到前台栈中，栈顶第一个非finishing状态的Activity，如果没有找到返回false
       final ActivityRecord next = topRunningActivityLocked(true /* focusableOnly */)
       // top running之后的任意处于初始化状态并且有显示starting window，移除starting window
       mStackSupervisor.cancelInitializingActivities();
        if (!hasRunningActivity) {
            // There are no activities left in the stack, let's look somewhere else.
            // 【* 5.1.1】当前栈没有活动了，找到下一个合适的，可以获取焦点的栈启动，如果还没找到，就启动桌面 5.1.1
            return resumeTopActivityInNextFocusableStack(prev, options, "noMoreActivities");
            // ...
            // 下一步判断：如果栈顶是要恢复焦点的活动，啥都不做。
            // 如果正在睡眠，没有待获取焦点的活动，并且栈顶活动是暂停状态，这是我们想要的状态。
            // 确保拥有此活动的用户是启动了的。如果没有，就保持那样吧，别人应该会带来另一个用户的活动到栈顶的。
            // 【* 5.1.2】暂停上一个活动，这是阻塞的，在暂停完成之前，什么也不会做的。
            boolean pausing = mStackSupervisor.pauseBackStacks(userLeaving, next, false);
        	if (mResumedActivity != null) {
            if (DEBUG_STATES) Slog.d(TAG_STATES,
                    "resumeTopActivityLocked: Pausing " + mResumedActivity);
            pausing |= startPausingLocked(userLeaving, false, next, false);
        	}
        	// ...
        	if (next.app != null && next.app.thread != null) {
                
        	}else {
                // 新的Activity
                mStackSupervisor.startSpecificActivityLocked(next, true, true);
        	}
        }
}
```

```java

void startSpecificActivityLocked(ActivityRecord r,boolean andResume, boolean checkConfig) {
     if (app != null && app.thread != null) {
         // 【* 5.2】启动Activity
         realStartActivityLocked(r, app, andResume, checkConfig);
     } else {
         // 【* 5.1.3】 创建新进程 
         mService.startProcessLocked(...);
     }        
}
```



#### 5.1.1 启动桌面

- `ActivityManagerService$startHomeActivityLocked`，根据`HomeIntent`生成一个新的`ActivityInfo`
- 下一步来到`ActivityStarterController$startHomeActivity`，在这里，调用`ActivityStackSupervisor$moveHomeStackTaskToTop`把桌面任务栈移至顶部，然后生成一个`ActivityStarter`来执行
- 这一次由于没有设置`userid`，所以不会像上面那样走`startActivityMayWait`，而是走`startActivity`

```java
 boolean startHomeActivityLocked(int userId, String reason) {
        if (mFactoryTest == FactoryTest.FACTORY_TEST_LOW_LEVEL
                && mTopAction == null) {
            // We are running in factory test mode, but unable to find
            // the factory test app, so just sit around displaying the
            // error message and don't try to start anything.
            return false;
        }
        Intent intent = getHomeIntent();
        ActivityInfo aInfo = resolveActivityInfo(intent, STOCK_PM_FLAGS, userId);
        if (aInfo != null) {
            intent.setComponent(new ComponentName(aInfo.applicationInfo.packageName, aInfo.name));
            // Don't do this if the home app is currently being
            // instrumented.
            aInfo = new ActivityInfo(aInfo);
            aInfo.applicationInfo = getAppInfoForUser(aInfo.applicationInfo, userId);
            ProcessRecord app = getProcessRecordLocked(aInfo.processName,
                    aInfo.applicationInfo.uid, true);
            if (app == null || app.instr == null) {
                intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
                final int resolvedUserId = UserHandle.getUserId(aInfo.applicationInfo.uid);
                // For ANR debugging to verify if the user activity is the one that actually
                // launched.
                final String myReason = reason + ":" + userId + ":" + resolvedUserId;
                mActivityStartController.startHomeActivity(intent, aInfo, myReason);
            }
        } else {
            Slog.wtf(TAG, "No home screen found for " + intent, new Throwable());
        }

        return true;
    }
```

#### 5.1.2 暂停上一个Activity

- 获取到`ClientLifecycleManager`的实例，并调用`scheduleTransaction`
- 下一步调用`ClientTransaction$schedule`方法，`transaction.getClient()`是`IApplicationThread`类型的，`ActivityThread$ApplicationThread`实现了该接口。这里隐隐然，我们就能猜到又要用到`ActivityThread$H`来处理了
- 下一步我们看到`ApplicationThread`果然实现了该方法，但是具体的执行调用是`ActivityThread.this.scheduleTransaction(transaction);`
- 下一步，其实真正实现该方法的是`ActivityThread`的父类：`ClientTransactionHandler`，在这里发送消息到`ActivityThread$H`中去：`sendMessage(ActivityThread.H.EXECUTE_TRANSACTION, transaction);`
- 下一步，调用`TransactionExecutor$executeLifecycleState`，根据`LifecycleItem`的内容来转换状态，即我们一开始生成的`PauseActivityItem`
- 下一步，执行`PauseActivityItem$execute`, `PauseActivityItem$postExecute`，根据`execute`的参数`client`，发现最终执行的是`ActivityThread$performPauseActivity`，其中，如果需要保存状态，调用`callActivityOnSaveInstanceState`
- 下一步，调用`Instrumentation$callActivityOnPause`，然后调用`Activity$performPause`
- 最终，看到了熟悉的`onPause()`

```java
// 开始暂停当前获取焦点的活动。如果活动已经暂停或者没有获取焦点，这就是错误的调用了。
final boolean startPausingLocked(boolean userLeaving, boolean uiSleeping,
            ActivityRecord resuming, boolean pauseImmediately) {
 // ...
 
         if (prev.app != null && prev.app.thread != null) {
            if (DEBUG_PAUSE) Slog.v(TAG_PAUSE, "Enqueueing pending pause: " + prev);
            try {
                EventLogTags.writeAmPauseActivity(prev.userId, System.identityHashCode(prev),
                        prev.shortComponentName, "userLeaving=" + userLeaving);
                mService.updateUsageStats(prev, false);

                mService.getLifecycleManager().scheduleTransaction(prev.app.thread, 		                              prev.appToken, PauseActivityItem.obtain(prev.finishing, userLeaving,
                                prev.configChangeFlags, pauseImmediately));
            }  
        }  
    // ...
}            
```

#### 5.1.3 新建进程 

- `ActivityStackSupervisor$startSpecificActivityLocked`进入`ActivityManagerService$startProcessLocked`，与`ActivityRecord`类似地，生成一个`ProcessRecord`对象

- 由`startProcessLocked`进入`Process$start`，这里注意一个参数：`String entryPoint = "android.app.ActivityThread";`

- 上一步的`entryPoint`组装成`argsForZygote`，同时由`openZygoteSocketIfNeeded`生成一个`ZygoteState`用于进程间进行`Socket`通信，这两个对象交由`ZygoteProcess$zygoteSendArgsAndGetResult`

- 下一步我们去`ZygoteInit`看看，上面我们`openZygoteSocketIfNeeded`时，创建的`socket`的名字是:`zygote`。在`ZygoteInit$main`中，我们同样发现：`String socketName = "zygote";`，`zygoteServer.registerServerSocketFromEnv(socketName);`，同时在这里为`zygote`命令连接注册了`socket`服务

- 在`Zygote$main`中，我们看到，有`forkSsytemServer`，顾名思义，这是启动系统服务进程的，暂时忽略，继续看下去：`caller = zygoteServer.runSelectLoop(abiList);` 

- 下一步，看到`runSelectLoop`中维护了`ZygoteConnection`的一个`ArrayList`，用来存放`ActivityManagerService`发来的请求数据，接着调用：

  ```java
    ZygoteConnection connection = peers.get(i);
    final Runnable command = connection.processOneCommand(this);
  ```

- 在`ZygoteConnection$processOneCommand`中，先去获取`pid`: `pid = Zygote.forkAndSpecialize(...)`，如果`pid == 0` 表示是子进程，处理函数：`handleChildProc`，否则表示交由父进程处理: `handleParentProc`

- 从`handleChildProc`中来到`ZygoteInit$childZygoteInit`，跟入一看，来到`RuntimeInit$findStaticMain`，在这里，根据类名，及我们上面的`entryPoint`变量的值，这里是`android.app.ActivityThread`，反射运行其`main`方法

  

### 5.2  Activity启动

a) 在 5.1.3中，我们走到了`ActivityThread$main`方法中，这里的主要工作：

```java
public static void main(String[] args) {
    // ...
    // 为主线程准备looper
    Looper.prepareMainLooper();
    // 把主线程添加到ActivityManagerService
    ActivityThread thread = new ActivityThread();
    thread.attach(false, startSeq);
    // 开启无线循环
    Looper.loop();
    // ...
}
```

b) 在`ActivityThread$attach`中：

```java
private void attach(boolean system, long startSeq) {
    // ...
    if (!system) {
     // 非系统应用
         final IActivityManager mgr = ActivityManager.getService();
          try {
               mgr.attachApplication(mAppThread, startSeq);
          } catch (RemoteException ex) {
              throw ex.rethrowFromSystemServer();
          }
        BinderInternal.addGcWatcher(new Runnable() {
            //此处为GC策略：当已使用内存大于总内存的3/4时，通知AMS释放资源
            if (dalvikUsed > ((3*dalvikMax)/4)) {
                mgr.releaseSomeActivities(mAppThread);
            }
        }
        
    } else {
        // 系统应用，直接调用其Application的onCreate方法
            ContextImpl context = ContextImpl.createAppContext(this, getSystemContext().mPackageInfo);
            mInitialApplication = context.mPackageInfo.makeApplication(true, null);
            mInitialApplication.onCreate();  
    }
     // 最后为根空间添加配置改变的回调
     ViewRootImpl.ConfigChangedCallback configChangedCallback = ...

    // ...
}
```



c) 下面，来到了`ActivityManagerService$attachApplication`，传入当前的主线程实例，然后来到`attachApplicationLocked`方法，在这里，看到了中国人民的老朋友`ActivityStackSupervisor`的实例：

```java
private final boolean attachApplicationLocked(IApplicationThread thread,
                                              int pid, int callingUid, long startSeq) {
    // ...
    // 先根据pid判断一番，ProcessRecord是否存在，如果进程不存在，通知系统回收pid，返回false
    ProcessRecord app;
    if (app == null) {
        if (pid > 0 && pid != MY_PID) {
           killProcessQuiet(pid);
           //TODO: killProcessGroup(app.info.uid, pid);
        } else {
            thread.scheduleExit();
        }
    }
    // 如果应用程序的记录和前一个进程关联着，清除一下
    if (app.thread != null) {
       handleAppDiedLocked(app, true, true);
    }
    
    // 这里涉及到ContentProvider的生成，略
    // 这里涉及profiler的信息生成，用于Android Profiler，略
    // 调用ApplicationThread的handleBindApplication方法
    thread.bindApplication(...);
    // 更新LRU中进程的次序
    updateLruProcessLocked(app, false, null);
    // 这里进入ActivityStackSupervisor
    mStackSupervisor.attachApplicationLocked(app)
    // ...
}
```

d) 我们看下`ActivityStackSupervisor$realStartActivityLocked`

```java
final boolean realStartActivityLocked(ActivityRecord r, ProcessRecord app,
       boolean andResume, boolean checkConfig) throws RemoteException {
           // ...
    	   // Create activity launch transaction. 
    	   // 这里我们发现上面那句话，“创建活动登录合约”，然后看到了LaunchActivityItem，根据我们追踪PauseActivityItem的经验，二者类似
    	   // ...
       }
```

e) 然后`ActivityThread$handleLaunchActivity` --> `ActivityThread$performLaunchActivity`

```java
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    // ...
    // 反射生成Activity实例
    Activity activity = null;
	java.lang.ClassLoader cl = appContext.getClassLoader();
    activity = mInstrumentation.newActivity(cl, component.getClassName(), r.intent);
    // 设置base 上下文，关联进程，主线程
    activity.attach(...);
    // 调用Instrumentation的创建方法
    mInstrumentation.callActivityOnCreate(...)
    // ...
}
```

f) 然后`Activity$performCreate`，这里我们看到了熟悉的`onCreate`.我们知道`TransactionExecutor` 执行所有的合约`executeCallbacks(transaction)`,`executeLifecycleState(transaction)`，在生命周期的转换里，具体实现如下`TransactionExecutorHelper$getLifeCyclePath`：

```java
public IntArray getLifecyclePath(int start, int finish, boolean excludeLastState) {
   // ...
   			// 清除生命周期IntArray，这是Android自定义的数据结构
   		   mLifecycleSequence.clear();
   		   // 下面就是Activity各个生命周期对应的关系了
    	   // 如果终止状态大于等于开始状态，那么从开始状态的下一个状态累加放入生命周期序列里
           if (finish >= start) {
            // just go there
            for (int i = start + 1; i <= finish; i++) {
                mLifecycleSequence.add(i);
            }
        } else { // finish < start, can't just cycle down
               // 以暂停开始，以恢复焦点结束，那么下一个状态是恢复焦点
            if (start == ON_PAUSE && finish == ON_RESUME) {
                // Special case when we can just directly go to resumed state.
                mLifecycleSequence.add(ON_RESUME);
            } else if (start <= ON_STOP && finish >= ON_START) {
                // Restart and go to required state.
				// 开始状态小于stop，结束状态大于start，那么resume，pause状态加入
                // Go to stopped state first.
                for (int i = start + 1; i <= ON_STOP; i++) {
                    mLifecycleSequence.add(i);
                }
                // restart状态加入
                mLifecycleSequence.add(ON_RESTART);
                // Go to required state 加入全状态
                for (int i = ON_START; i <= finish; i++) {
                    mLifecycleSequence.add(i);
                }
            } else {
                // Relaunch and go to required state
				// 加入全状态，即重启
                // Go to destroyed state first.
                for (int i = start + 1; i <= ON_DESTROY; i++) {
                    mLifecycleSequence.add(i);
                }
                // Go to required state
                for (int i = ON_CREATE; i <= finish; i++) {
                    mLifecycleSequence.add(i);
                }
            }
        }
   // ...
}
```

```java
    public static final int UNDEFINED = -1;
    public static final int PRE_ON_CREATE = 0;
    public static final int ON_CREATE = 1;
    public static final int ON_START = 2;
    public static final int ON_RESUME = 3;
    public static final int ON_PAUSE = 4;
    public static final int ON_STOP = 5;
    public static final int ON_DESTROY = 6;
    public static final int ON_RESTART = 7;
```

g) 根据上一步，可以知道`create`之后是`start`, `resume`两个状态。这里流程和`pause`, `launch`类似。至此，Activity启动完毕。





## 6 Activity的结束

我们曾经分析过`pause`状态，也知道接下来是`stop`, `desotry`状态，那么这两个状态是在哪里调用的？

在`ActivityThread$handleResumeActivity`中，我们处理了新活动的获取焦点，在这个方法最后一句：

`Looper.myQueue().addIdleHandler(new Idler());`

发现这个`Idler`调用了`ActivityManagerService$activityIdle`，然后调用了`ActivityStackSupervisor$activityIdleInternalLocked`:

```java
 if (r.finishing) {
     stack.finishCurrentActivityLocked(r, ActivityStack.FINISH_IMMEDIATELY, false,
             "activityIdleInternalLocked");
 } else {
     stack.stopActivityLocked(r);
 }
```

可以看到，`stop`,`finish`都有了。

接下来我们预想的`StopActivityItem`, `FinishActivityItem`合约也出现了，这样又一次调用到`ActivityThread`的各种相关方法。实际追踪发现其实是`DestoryActivityItem。`

这里多提一句，我们在一开始调用`startActivity` --> `startActivityForResult`，期望能够得到一个`ActivityResult`。这个结果，后来在`ActivityThread$sendActivityResult`中，通过`ActivityResultItem`管理转发，这也是个合约，和`DestoryActivityItem`都是`ClientTransactionItem`的子类。链路如下：

`ActivityThread$sendActivityResult` --> `ActivityResultItem$execute` --> `ActivityThread$handleSendResult` --> `ActivityThread$deliverResults` --> `Activity$dispatchActivityResult` --> `Activity$onActivityResult`





