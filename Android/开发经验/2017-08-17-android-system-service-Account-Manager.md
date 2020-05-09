---
layout: post
title:  Android系统服务：AccountManager
date:   2017-08-17
categories: Android System Framework
tag: android
---
 

#### 前面 ####
  
以`AccountManager`为例，read the fucking code.
#### 协议 ####

可以看到该类遵循Apache协议。也就是说允许商用，私用，分发，修改，专利授权，附件协议，作者不允许商标使用，不承担责任。

详细的各种协议区分，请看：[here](http://xusx1024.com/2017/08/22/license-introduce/)

#### package ####

类所在的包：`package android.accounts;` 

- AbstractAccountAuthenticator
- Account
- AccountAuthenticatorActivity
- AccountAuthenticatorResponse
- AccountManagerCallback
- AccountManagerFuture
- AccountsException
- AuthenticatorDescription
- AuthenticatorException
- NetworkErrorException
- OnAccountsUpdateListener
- OperationCanceledException


#### 类说明 ####

This class provides access to a centralized registry of the user's online accounts.  
The user enters credentials (username and password) once per account, granting applications access to online resources with "one-click" approval.

此类提供了用户在线账户集中注册的访问。
用户为每个账户输入用户密码作为证书，准许应用通过一次授权访问在线资源。

<p>Different online services have different ways of handling accounts and authentication, so the account manager uses pluggable <em>authenticator</em> modules for different <em>account types</em>. 
Authenticators (which may be written by third parties) handle the actual details of validating account credentials and storing account information.  
For example, Google, Facebook, and Microsoft Exchange each have their own authenticator.

不同的线上服务处理账户和认证有不同的方式，因此账户管理者为不同的账户类型应用可插拔的认证模块。
验证者(也许是第三方写入)处理具体的账户证书细节，并且存储账户信息。
例如：Google，Facebook,Microsoft Exchange有各自的验证者。

<p>Many servers support some notion[概念] of an <em>authentication token</em>, which can be used to authenticate a request to the server without sending the user's actual password.  
(Auth tokens are normally created with a separate request which does include the user's credentials.) 
AccountManager can generate auth tokens for applications, so the application doesn't need to handle passwords directly. 
Auth tokens are normally reusable and cached by AccountManager, but must be refreshed periodically[定期]. 
It's the responsibility of applications to <em>invalidate</em> auth tokens when they stop working so the AccountManager knows it needs to regenerate them.

许多服务支持认证令牌的概念，可以无需用户确切的密码，即可向服务端认证一个请求。
(授权令牌通常创建在包含用户证书的一个单独的请求里。)
账户管理者可以为应用生成授权令牌，因此应用无需直接操作密码。
授权令牌通常是可重用的并由账户管理者缓存，但必须定期刷新。
当授权令牌停止工作时，应用的职责是废止这些授权，账户管理者依此得知需要重新生成。

<p>Applications accessing a server normally go through these steps:

<ul>
<li>Get an instance of AccountManager using {@link #get(Context)}.

<li>List the available accounts using {@link #getAccountsByType} or {@link #getAccountsByTypeAndFeatures}.  
Normally applications will only be interested in accounts with one particular <em>type</em>, which identifies the authenticator.  
Account <em>features</em> are used to identify particular account subtypes and capabilities[功能].  
Both the account type and features are authenticator-specific strings, and must be known by the application in coordination with its preferred authenticators.

<li>Select one or more of the available accounts, possibly by asking the user for their preference.  
If no suitable accounts are available, {@link #addAccount} may be called to prompt the user to create an account of the appropriate type.

<li><b>Important:</b> If the application is using a previously remembered account selection, it must make sure the account is still in the list of accounts returned by {@link #getAccountsByType}.  Requesting an auth token for an account no longer on the device results in an undefined failure.

<li>Request an auth token for the selected account(s) using one of the {@link #getAuthToken} methods or related helpers.  
Refer to the description of each method for exact usage and error handling details.

<li>Make the request using the auth token.  The form of the auth token, the format of the request, and the protocol used are all specific to the service you are accessing.  
The application may use whatever network and protocol libraries are useful.

<li><b>Important:</b> If the request fails with an authentication error, it could be that a cached auth token is stale and no longer honored by the server.  
The application must call {@link #invalidateAuthToken} to remove the token from the cache, otherwise requests will continue failing!  
After invalidating the auth token, immediately go back to the "Request an auth token" step above.  
If the process fails the second time, then it can be treated as a "genuine" authentication failure and the user notified or other appropriate actions taken.
</ul>

应用访问服务一般有以下几个步骤：
<ul>
<li>使用`get(Context)`获取账户管理者的实例
<li>使用`getAccountsByType`或者`getAccountsByTypeAndFeatures`列出可用的账户。
通常应用只会被一个特定类型所吸引，该类型可用于识别验证者。
账户特征用来识别特定的账户子类型和功能。
账户类型和特征是验证者特定的字符串，必须由应用程序与其首选验证着协调知道。
<li>选一或多个可用账户，可能通过询问用户的偏好。
如果没有适合的账户可用，指示用户调用`addAccount`创建一个适当类型的账户。
<li><b>重要：</b>如果应用程序正在使用以前记录的账户选择，一定要确保此账户仍在`getAccountsByType`返回的账户列表里。
为不再在设备上的账户请求授权令牌，会导致未定义的错误。
<li>为选择的账户请求授权令牌使用`getAuthToken`方法，或者相关的帮助者。
参考各个方法的描述，以确定用例使用、错误处理细节。
<li>使用授权令牌发起请求。令牌的形式，请求的格式，使用的协议，这些都由你正在访问的服务指定。
应用程序可以使用任何网络和协议库都是可用的。
<li><b>重要：</b>如果请求失败出现了授权错误，可能是缓存的令牌陈旧，不再由服务器兑现。
应用程序必须调用`invalidateAuthToken`去移除缓存的令牌，否则请求就会持续失败！
废止授权令牌之后，可以直接重复上面的步骤。
如果第二次操作也失败，就可以被对待为真实身份认证失败，用户需要被通知或采取别的行动.
</ul>

<p>Some AccountManager methods may need to interact with the user to prompt for credentials, present options, or ask the user to add an account.
The caller may choose whether to allow AccountManager to directly launch the necessary user interface and wait for the user, or to return an Intent which the caller may use to launch the interface, or (in some cases) to install a notification which the user can select at any time to launch the interface.
To have AccountManager launch the interface directly, the caller must supply the current foreground {@link Activity} context.

一些账户管理者方法也许需要与用户交互来提示凭据，呈现选项，或要求用户添加一个账户。
调用者可以选择允许账户管理者直接登录必要的用户接口并且等待用户返回，也可以返回一个可以用来登录用户接口的意图，也可以展示一个通知，用户可以在任意时间选择去登录该接口。
要让账户管理者直接登录接口，调用者必须提供当前前台活动的上下文。

<p>Many AccountManager methods take {@link AccountManagerCallback} and {@link Handler} as parameters.  
These methods return immediately and run asynchronously. 
If a callback is provided then {@link AccountManagerCallback#run} will be invoked on the Handler's thread when the request completes, successfully or not.
The result is retrieved by calling {@link AccountManagerFuture#getResult()} on the {@link AccountManagerFuture} returned by the method (and also passed to the callback).  
This method waits for the operation to complete (if necessary) and either returns the result or throws an exception if an error occurred during the operation.  
To make the request synchronously, call {@link AccountManagerFuture#getResult()} immediately on receiving the future from the method; no callback need be supplied.

<p>许多账户管理者持有`AccountManagerCallback`和`Handler`实例作为参数。
这些方法会异步执行并且立即返回。
如果一个回调被提供，在请求完成，成功或失败时，`AccountManagerCallback`将会在Handler的线程中被调用。
结果检索调用的方法是`AccountManagerFuture#getResult()`。
如果有必要，此方法等待操作的完成并且无论是返回结果或是在操作过程中产生错误并抛出异常。
为了异步执行请求，在接受到方法的未来时，直接调用` AccountManagerFuture#getResult()`；不需要提供回调。

<p>Requests which may block, including {@link AccountManagerFuture#getResult()}, must never be called on the application's main event thread.  
These operations throw {@link IllegalStateException} if they are used on the main thread.

请求也许会阻塞，包括`AccountManagerFuture#getResult()`,绝对禁止在应用程序的主线程调用。
否则，这些操作会抛出`IllegalStateException`异常。

#### 变量 ####

##### 静态常量 #####

- `ERROR_CODE_REMOTE_EXCEPTION`:远程异常
- `ERROR_CODE_NETWORK_ERROR`:网络错误
- `ERROR_CODE_CANCELED`:取消
- `ERROR_CODE_INVALID_RESPONSE`:无效的响应
- `ERROR_CODE_UNSUPPORTED_OPERATION`:不支持的操作
- `ERROR_CODE_BAD_ARGUMENTS`:错误的参数
- `ERROR_CODE_BAD_REQUEST`:错误的请求
- `ERROR_CODE_BAD_AUTHENTICATION`:错误的授权
- `ERROR_CODE_BAD_AUTHENTICATION`:错误的已注册用户
- `ERROR_CODE_MANAGEMENT_DISABLED_FOR_ACCOUNT_TYPE`:当前账户类型管理失效错误
- `KEY_ACCOUNT_NAME`:传值常量键，用于获取账户名
- `KEY_ACCOUNT_TYPE`:传值常量键，用于获取账户类型值
- `KEY_AUTHTOKEN`:传值常量键，用于获取授权令牌
- `KEY_INTENT`:传值常量键，用于启动相关的用户接口页面
- `KEY_PASSWORD`:传值常量键，用于获取密码
- `KEY_ACCOUNTS`:传值常量键，用于获取账户
- `KEY_ACCOUNT_AUTHENTICATOR_RESPONSE`:传值常量键，用于获取账户授权响应
- `KEY_ACCOUNT_MANAGER_RESPONSE`:传值常量键，用于获取账户管理者响应
- `KEY_AUTHENTICATOR_TYPES`:传值常量键，用于获取授权类型
- `KEY_AUTH_FAILED_MESSAGE`:授权失败信息
- `KEY_AUTH_TOKEN_LABEL`:授权令牌标签
- `KEY_BOOLEAN_RESULT`:结果布尔值
- `KEY_ERROR_CODE`:错误码
- `KEY_ERROR_MESSAGE`:错误消息
- `KEY_USERDATA`:用户数据
- `KEY_LAST_AUTHENTICATED_TIME`:上次授权时间
- `KEY_CALLER_UID`:调用者的UID
- `KEY_CALLER_PID`:调用者的PID
- `KEY_ANDROID_PACKAGE_NAME`:Android包名
- `KEY_NOTIFY_ON_FAILURE`:授权失败时是否提示，boolean值
- `ACTION_AUTHENTICATOR_INTENT`:`android.accounts.AccountAuthenticator`
- `AUTHENTICATOR_META_DATA_NAME`:`android.accounts.AccountAuthenticator`
- `AUTHENTICATOR_ATTRIBUTES_NAME`:授权者的name属性

#### 构造方法 ####

	 public AccountManager(Context context, IAccountManager service) {
        mContext = context;
        mService = service;
        mMainHandler = new Handler(mContext.getMainLooper());
     }

不会用到，获取一个账户管理者的实例，使用如下方法即可：
	
	 public static AccountManager get(Context context) {
	        if (context == null) throw new IllegalArgumentException("context is null");
	        return (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
	    }
	
#### 类结构 ####

`public class AccountManager{ ... }`

public的类，没有实现任何接口。

##### 内部类-AmsTask #####

`private abstract class AmsTask extends FutureTask<Bundle> implements AccountManagerFuture<Bundle> { ... }`

看类修饰符：
- abstract ： 说明该类是一个模板类，必定有抽象方法，需要被子类化，实现具体的操作，
- private：子类化应该是在外部类中
符合Java编码原则：使用函数对象代替策略。使类和成员的可访问性最小化。

该类继承自一个可取消的异步计算，实现了一个处理计算结果的回调。

发现`AccountManagerFuture` 和`Future`两个接口的行为是一样的，一个在`android.accounts`里，另一个在`java.util.concurrent`.

AmsTask的构造方法为public，由于父类`FutureTask`的构造，必须要求传递`Callable`实例，如果没有这个实例，就会`NullPointerException`，所以直接在此截断抛出了。
因此，虽然传递了Callable，却禁止使用了，使用的还是Future。

	  public AmsTask(Activity activity, Handler handler, AccountManagerCallback<Bundle> callback) {
            super(new Callable<Bundle>() {
                public Bundle call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });

            mHandler = handler;
            mCallback = callback;
            mActivity = activity;
            mResponse = new Response();
        }

-  abstract void doWork()  由函数对象来具体实现操作
-  AccountManagerFuture<Bundle> start() 开始执行`doWork()`
-  void set(Bundle bundle) 父类方法，把结果设置给指定的Future，除非Future已经有结果或者已经被取消
-  Bundle getResult()、Bundle getResult(long timeout, TimeUnit unit)、Bundle internalGetResult(Long timeout, TimeUnit unit) 获取处理结果
> getResult最后都调用了`internalGetResult`方法。大概流程如下：
> - 判断线程是否执行结束
> - 判断时间是否为null或已结束
> - 获取结果
> - 异常后取消线程执行

确保当前线程不在主线程的代码：

	private void ensureNotOnMainThread() {
        final Looper looper = Looper.myLooper();
        if (looper != null && looper == mContext.getMainLooper()) {
            final IllegalStateException exception = new IllegalStateException(
                    "calling this from your main thread can lead to deadlock");
            Log.e(TAG, "calling this from your main thread can lead to deadlock and/or ANRs",
                    exception);
            if (mContext.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.FROYO) {
                throw exception;
            }
        }
    }

跟踪到`FutureTask`的`get`方法里，这里返回父类的一个Object实例：`outcome`存放我们想要的结果对象。

在`get`里还有许多操作，比如判断线程执行的状态：
- NEW         
- COMPLETING  
- NORMAL      
- EXCEPTIONAL 
- CANCELLED   
- INTERRUPTING
- INTERRUPTED 

我看到在操作线程状态的时候，使用了该类：`private static final sun.misc.Unsafe U = sun.misc.Unsafe.getUnsafe();`，用来保证原子操作，确定一个线程是`RUNNER`,`WAITER`.
这是一个JDK私有API，在Java9的时候可能移除，并使用`java.util.concurrent.LockSupport`实现。下面摘抄一段说明：

> 原子访问是`sun.misc.Unsafe`被广泛应用的特性之一，特性包括简单的put、get操作(带有volatile语义或不带有volatile语义)以及比较并交换(compare and swap，CAS)操作。
	
##### AmsTask的内部类-Response #####

AmsTask中有一个public的Response的引用。

操作一：`onResult`

> 响应有结果，跳转至用户提供的Activity，或是重试，再次调用`doWork()`，否则把响应数据bundle设置回父类的`outcome`里

操作二：`onError`

> 取消线程的执行

##### 内部类-BaseFutureTask #####

可以看到这是一个抽象类，我们只要知道什么是需要子类实现的，什么是已经实现的。

	 protected void postRunnableToHandler(Runnable runnable) {
            Handler handler = (mHandler == null) ? mMainHandler : mHandler;
            handler.post(runnable);
        }

把Runnable加入消息队列，可以当前线程的handler执行。

`abstract void doWork()`  由函数对象来具体实现操作
`abstract T bundleToResult(Bundle bundle)` 由函数对象来具体实现操作

##### BaseFutureTask的内部类-Response #####

- onResult 调用外部类的`bundleToResult`
- onError 中断线程，抛出异常

##### 内部类-Future2Task #####

* BaseFutureTask的子类，同时也是个抽象类，实现了`internalGetResult`，具体的贱`AmsTask$internalGetResult`

##### 内部类-GetAuthTokenByTypeAndFeaturesTask #####

`AmsTask`的子类，顾名思义，根据类型和特征获取授权令牌的任务。

`doWork()`的作用：
- 根据类型和特征尝试获取已授权账户
- 未获取到账户，如果提示添加账户的activity为空，则把`KEY_ACCOUNT_NAME`,`KEY_ACCOUNT_TYPE`,`KEY_AUTHTOKEN`置为空，并返回
- 未获取到账户，如果提示添加账户的activity不为空，调用`addAccount`方法，这是一个Future，可以中断和等待结果的子线程
- 如果只获取到一个账户，获取AuthToken，放入响应
- 如果获取到了多个账户，启动`android.accounts.ChooseAccountActivity`,选择一个后，获取AuthToken，放入响应

#### 主要方法 ####

##### sanitizeResult #####

	  public static Bundle sanitizeResult(Bundle result) {
	        if (result != null) {
	            if (result.containsKey(KEY_AUTHTOKEN)
	                    && !TextUtils.isEmpty(result.getString(KEY_AUTHTOKEN))) {
	                final Bundle newResult = new Bundle(result);
	                newResult.putString(KEY_AUTHTOKEN, "<omitted for logging purposes>");
	                return newResult;
	            }
	        }
	        return result;
	    }

如果返回的结果bundle中包含`KEY_AUTHTOKEN`，就把这个值变成“为了日志的目的而省略”，是对结果做一次“消毒”

##### IAccountManager相关 #####

- getPassword
- setPassword
- clearPassword
- getUserData
- setUserData
- getAuthenticatorTypes：此处返回一个数组，一个账户可以有多重类型，不需要权限
- getAuthenticatorTypesAsUser：需要权限，或者相同的用户
- getAccounts：需要权限
- getAccountsAsUser
- getAccountsForPackage：调用者可用的账户列表
- updateAppPermission：仅被系统页面调用，不包含于SDK中
- getAuthTokenLabel：获取与认证者相关联的用户友好标签，返回一个Future
- hasFeatures：需要权限，指定的账户是拥有指定的权限。可用在任意线程，但返回结果不可用于主线程。
- getAccountsByTypeAndFeatures：需要权限，根据类型和功能获取账户列表
- addAccount
- addSharedAccount
- addAccountExplicitly：直接添加一个账户。这个方法不会刷新最近一次的时间戳，需要主动`notifyAccountAuthenticated`。但是，如果该方法触发了`addAccount()`或者`addAccountAsUser`，就不需要刷新时间戳，可以自动刷新了。可在主线程调用此方法。需要API22以上。
- notifyAccountAuthenticated：通知系统账户刚刚被授权。可被其他程序用来验证账户。只有在授权成功方可调用。主线程不安全的。
- renameAccount：先移除已有的账户，再添加重命名后的账户。
- getPreviousName：获取改名前的名字，可能返回null，主线程安全的。
- removeAccount：如果有，就从AccountManager删除账户，不会从服务器上删除。API22之前调用需要权限。
- removeAccountAsUser
- invalidateAuthToken：从AccountManager的缓存中移除授权。API22之前需要权限。
- peekAuthToken：从缓存中取授权令牌，如果没有，生成一个新的，不会链接服务器。
- setAuthToken
- getAuthToken
- getAuthTokenByFeatures
- blockingGetAuthToken：会阻塞，不要在主线程调用，API22之前需要权限
- copyAccountToUser
- confirmCredentialsAsUser：
- updateCredentials
- editProperties：提供一个用户修改授权设置的机会。
- ensureNotOnMainThread：确保不在主线程
- postToHandler
- convertErrorToException
- newChooseAccountIntent：提供给用户一个可选账户的列表

#### DEMO ####

[AccountManagerDemo](https://github.com/xusx1024/AccountManagerDemo)
