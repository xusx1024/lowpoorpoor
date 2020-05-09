[TOC]

# Android项目插件化开发

### 插件化
以支付宝蚂蚁森林为代表的城市服务，都用到了插件化技术。当用户第一次启用某个功能模块，会有很直观的感受：短暂的延迟，这是因为该模块是动态加载，初始化需要一定的时间，某些次级重要性的模块需要从远程获取，一般是从服务器上下载。
插件化就是将项目分割成多个模块，两大类：宿主和插件。宿主模块必须是Android系统可用识别安装apk文件。每个插件模块则可用是一个apk或者zip，jar这样的打包集合。
### 优点

#### 开发角度
- 并发开发。
	不同模块开发人员，可以同步开发，并且互不影响。在支付宝，微信，我们经常能看到第三方厂商的服务接入，使用的便是大厂提供的统一接口标准。当然，现在微信在推小程序，插件化方案在微信已经被搁置了。
- 动态发布新功能，在线热修复。
	避免应用称为手机上的巨无霸，按需下发功能。如果对应插件导致异常，可以直接替换新的修复包。
- 解决65535方法数爆栈。
	
#### 产品测试角度

- 功能模块的解耦，便于独立测试，减少回归工作量
- 灰度测试，结合数据上报，根据不同用户的使用习惯，灰度下发产品A/B test，验证产品经理关于A/B设想哪个更适合用户。

#### 用户角度
- 更小的安装包体积 。小的安装包使得用户迅速下载安装，节省流量的同时，可以便于用户体验应用的核心功能。
- 更快的启动速度。在Android 5.0 之后，手机的VM统一为ART虚拟机。采用了AOT(Ahead of time)模式 进行预加载整个应用。用户在第一次安装的时候，可能会等待很长时间，就是因为AOT在优化apk的dex文件。安装包很小，自然AOT所花费的时间也会减小。把模块的加载时间和整个应用的预加载时间剥离开来，带来更好的用户体验。
- 更优的内存使用。宿主小，占用的内存就小，插件模块按需加载，存放在RAM中， 可以根据磁盘使用情况，插件使用的频率周期，设置清除标准，不再占用更多空间，用户从而可用安装更多应用。
- 在前几年的市场，经常会有RAM不足，软件无法安装的提示，现在我们身边似乎是不见这种低端机器了。其实只不过是市场转移了，在45678线城市，东南亚，拉美，非洲等市场，根据facebook的调查，这些低端设备市场还非常庞大。插件化是一个很不错的解决方案。当然，大厂会根据低端设备开发lite版本，比如facebook-lite，qq-lite，今日头条-lite等等，那又另当别论了。 	

### GameSDK中应用

#### 业务分析
- SDK原有功能为登录和支付，这是核心功能
- 礼包功能为上海子公司运营提出的需求
- 我们项目组可能会加其他的需求
- 二手项目

#### 原理 
- 插件化实现思想就是给插件包一个类加载器和资源管理器，这样在宿主的上下文环境下，加载代理控件，执行插件中的文件。如果想要更多了解，可以先了解以下相关知识：
	- JVM ClassLoader类加载机制和dex加载
	- DexClassLoader，PathCLassLoader两种Android类加载器的区别和使用场景
	- 反射，生成AssetManager，用于加载资源文件
	- Activity组件在AMS的验证机制，代理Activity生命周期机制

#### 实现
插件化的成熟框架，有滴滴的`VirtualAPk`,360科技的`DroidPlugin`。`DynamicLoadApk`，作为`VirtualAPK`的前身，比较简练，核心代码都是没有大的改变。

我们抽取并修改了`DynamicLoadApk`的几个核心实现类，作为`lib-module`，生成公用通信接口，供插件应用实现。

##### 插件调用
```java
package com.shunwang.dysdk.plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.widget.Toast;
import com.shunwang.dysdk.config.SDKConfig;
import com.shunwang.dysdk.floatball.FloatBallController;
import com.shunwang.dysdklib.plugin.dynamicload.DLUtil.DLUtils;
import com.shunwang.dysdklib.plugin.dynamicload.internal.DLIntent;
import com.shunwang.dysdklib.plugin.dynamicload.internal.DLPluginManager;

/**
 * Fun: 调用并运行插件apk
 * Created by sxx.xu on 7/3/2018.
 */

public class PluginCaller {

  private static PluginCaller caller;
  PackageInfo packageInfo;
  private boolean isLoaded = false;

  private PluginCaller() {
  }

  public static PluginCaller getInstance() {
    if (caller == null) {
      synchronized (PluginCaller.class) {
        caller = new PluginCaller();
      }
    }
    return caller;
  }

  public void loadPlugin(Context context, String path) {
    path += "/sdkhelper.apk";
    packageInfo = DLUtils.getPackageInfo(context, path);
    DLPluginManager.getInstance(context).loadApk(path);
    isLoaded = true;
  }



  public void startPluginActivity(Context context) {
    DLPluginManager pluginManager = DLPluginManager.getInstance(context);
    if (packageInfo == null) {
      loadPlugin(context, SDKConfig.mPath);
    }
    if (packageInfo == null && isLoaded) {
      Toast.makeText(context, "插件丢失", Toast.LENGTH_SHORT).show();
      return;
    }

    String packageName = packageInfo.packageName;
    String launcherActivityName = packageInfo.activities[0].name;
    pluginManager.startPluginActivity(context, new DLIntent(packageName, launcherActivityName));
    FloatBallController.getInstance(context).hide();
  }
}

```
`PluginCaller`类，负责从磁盘读取apk文件，解析出插件包信息，然后调用插件管理器加载。
这里由于需要解析插件的包信息，因此插件包不要加固。这样带来风险，插件包可能会被第三方获取，并直接拿到源码。这里可以采用以下做法来保护应用：
1. 插件包放在app私有目录下，当然，root情况也可以被获取到
2. 插件所有用户相关信息，从宿主获取，而宿主是混淆和加固的
3. 网络请求，前后端通信，参数加密验证，杜绝伪造，由于我们的礼包功能是和用户ID绑定，并且在产品设计上，具有唯一性，这里通过后端的处理，已经可以防止冒领，重复领等问题

##### 插件管理器
```java
package com.shunwang.dysdk.plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Fun: 插件管理器
 * Created by sxx.xu on 7/3/2018.
 */
public class PluginManager {
  public static PluginManager instance;

  private DexClassLoader pluginClassLoader;
  private Resources pluginResource;
  private Context context;
  private PackageInfo pluginPackageAricheInfo;

  private PluginManager() {
  }

  public static PluginManager getInstance() {
    if (instance == null) {
      synchronized (PluginManager.class) {
        if (instance == null) {
          instance = new PluginManager();
        }
      }
    }
    return instance;
  }

  public void setContext(Context context) {
    this.context = context.getApplicationContext();
  }

  public void loadApk(String dexPath) {
    pluginClassLoader =
        new DexClassLoader(dexPath, context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(),
            null, context.getClassLoader());

    pluginPackageAricheInfo =
        context.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);

    AssetManager assets = null;
    try {
      assets = AssetManager.class.newInstance();
      Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
      addAssetPath.invoke(assets, dexPath);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    pluginResource = new Resources(assets, context.getResources().getDisplayMetrics(),
        context.getResources().getConfiguration());
  }

  public Resources getPluginResource() {
    return pluginResource;
  }

  public DexClassLoader getPluginClassLoader() {
    return pluginClassLoader;
  }

  public PackageInfo getPluginPackageAricheInfo() {
    return pluginPackageAricheInfo;
  }
}

```
这个就是核心类，提供了类加载器获取并加载dex文件，提供了资源管理器。
这里需要注意，不要在资源命名上宿主和插件重名。这里需要靠编码规则来限制了，比如`礼包插件模块`，那么这个模块里所有的资源开头可以以`gift`类似这样的开头来区分。

##### 代理类
```java
package com.shunwang.dysdk.plugin;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import com.shunwang.dysdk.R;
import com.shunwang.dysdklib.plugin.PluginInterface;
/**
 * Fun: 代理类页面
 * Created by sxx.xu on 7/3/2018.
 */
public class ProxyActivity extends FragmentActivity {

  private String className;
  private PluginInterface pluginInterface;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    className = getIntent().getStringExtra("className");

    try {
      Class<?> aClass = PluginManager.getInstance().getPluginClassLoader().loadClass(className);
      Object newInstance = aClass.newInstance();

      if (newInstance instanceof PluginInterface) {
        Log.d("tag", "=====" + newInstance);
        pluginInterface = (PluginInterface) newInstance;
        pluginInterface.attachContext(this);

        Bundle bundle = new Bundle();
        pluginInterface.onCreate(bundle);
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override public void startActivity(Intent intent) {
    Intent newIntent = new Intent(this, ProxyActivity.class);
    newIntent.putExtra("className", intent.getComponent().getClassName());
    super.startActivity(newIntent);
    overridePendingTransition(R.anim.left_in, 0);
  }

  @Override public Resources getResources() {
    return PluginManager.getInstance().getPluginResource();
  }

  @Override public void onStart() {
    Log.d("tag", "===pluginInterface==" + pluginInterface);
    pluginInterface.onStart();
    super.onStart();
  }

  @Override public void onResume() {
    pluginInterface.onResume();
    super.onResume();
  }

  @Override public void onRestart() {
    pluginInterface.onRestart();
    super.onRestart();
  }

  @Override public void onDestroy() {
    pluginInterface.onDestroy();
    super.onDestroy();
    overridePendingTransition(0, 0);
  }

  @Override public void onStop() {
    pluginInterface.onStop();
    super.onStop();
  }

  @Override public void onPause() {
    pluginInterface.onPause();
    super.onPause();
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(0, R.anim.left_out);
  }
}

```
宿主调用插件都是通过访问代理类，代理类通过传入的类名，调用实现公开接口的具体插件类。
由于我们是SDK项目，代理的声明，需要接入方在自己的清单文件中预先声明留一个位置。可以类比市面上通用的QQ登录，微信支付等等的实现方式。

在代理类中，如果插件类中重载了某个系统api，代理类也要有对应的调用，否则会出现方法调用不到等问题。因此，通常的处理做法是，比如一个Activity，大部分常见的系统API都做一个空声明，这样可以有效防止该问题。

##### 宿主开放接口
```java
/**
* 精简后的插件接口，由于宿主，插件都是一个人开发，所以这里少写了许多系统定义的api
*/

public interface PluginInterface {

  void onCreate(Bundle bundle);

  void attachContext(Activity context);

  void onResume();

  void onStart();

  void onPause();

  void onStop();

  void onRestart();

  void onDestroy();

}

```
下面类是`Dynamic-Load-Apk`中的实现，可以看到有很多别的api实现，可以说是框架级别的，已经可以输出到别的项目使用了。

```java

public interface DLPlugin {

  void onCreate(Bundle savedInstanceState);

  void onStart();

  void onRestart();

  void onActivityResult(int requestCode, int resultCode, Intent data);

  void onResume();

  void onPause();

  void onStop();

  void onDestroy();

  void attach(Activity proxyActivity, DLPluginPackage pluginPackage);

  void onSaveInstanceState(Bundle outState);

  void onNewIntent(Intent intent);

  void onRestoreInstanceState(Bundle savedInstanceState);

  boolean onTouchEvent(MotionEvent event);

  boolean onKeyUp(int keyCode, KeyEvent event);

  void onWindowAttributesChanged(LayoutParams params);

  void onWindowFocusChanged(boolean hasFocus);

  void onBackPressed();

  boolean onCreateOptionsMenu(Menu menu);

  boolean onOptionsItemSelected(MenuItem item);
}

```
#### 优势和不足
> 优势：
> 插件化的优点不再此处列及。针对项目上的优势，首先旧的代码没有大的改动，只是在SDK初始化时增加了插件的检测逻辑代码，相对独立。同时，不同子公司，项目组提出的需求，可以区别对待，作为小的项目进行开发；另外，宿主，插件，中间件库，还可以进一步解耦，把公共库都封装起来，让业务更加纯粹。

>不足：
>合作开发需要学习成本，由于只是抽取了主流框架的核心类，在本项目中运行良好，还不足以作为框架级别输出。还有就是，只在公司小范围的有限品类机器通过测试，还没有经过市场的考验。