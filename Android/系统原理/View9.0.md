[TOC]

在[Activity9.0](<https://xushengxing.info/activity-9-0/>) 中细述了一个活动从入口到启动，以及相关的进程启动，生命周期调用的全过程。当`Activity`出世后，下一步就是`View`相关的绘制了。即本文的主旨。

`View`有人称之为『控件』，也有称之为『视图』。我们则随机应变，根据实际情况而定称呼，但心中只对应一个`View`即可。

##  1. 入口

在`setContentView`中设置自定义的`UI`，看以下代码：

```java
@Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
}
```

 ### 1.1 入口1 `AppCompatActivity`

 如果自定义`Activity`继承了`AppCompatActivity`，那么进入`setContentView`，发现调用跳转如下：`AppCompatActivity$setContentView`  --> `AppCompatDelegateImpl$setContentView`发现代码如下：

```java
@Override public void setContentView(int resId) {
    this.ensureSubDector();
    // 打开AppCompatDelegateImpl.class可以看到：android.R.id.content = 16908290
    ViewGroup contentParent = (ViewGroup)this.mSubDector.findViewById(android.R.id.content);
    contentParent.removeAllViews();
    LayoutInflater.from(mContext).inflate(resId,contentParent);
    mOriginalWindowCallback.onContentChanged();
}
```

同时还可以看到`setContentView(View v)`的实现，代码略。

####  1.1.1 创建subDecor

上面有两个入口，但是都会进入`3.PhoneWindow`，这里先看一下`AppcompatActivity`里面的实现逻辑。

我们看到上面有`ensureSubDector`，里面跳转到`createSubDecor`。这个方法的作用是创建一个名字叫做`subDector`的`ViewGroup`实例对象，并且通过`setContentView`设置到当前`Activity`相关联的`Window`上去。代码如下：

```java
private ViewGroup createSubDecor() {
    // ...
    // 【A】根据主题风格，设置窗口对应的特征，下面代码省略了if...else...判断条件
    requestWindowFeature(Window.FEATURE_NO_TITLE);// 标记“无标题”功能，关闭屏幕顶部的标题
    // int FEATURE_SUPPORT_ACTION_BAR = 100 + WindowCompat.FEATURE_ACTION_BAR;
    requestWindowFeature(FEATURE_SUPPORT_ACTION_BAR);// 启用操作栏
    requestWindowFeature(FEATURE_SUPPORT_ACTION_BAR_OVERLAY);// 启用可以覆盖在内容上的操作栏；通常清空下，操作栏在内容上方的空间，启用了这个，就覆盖了。如果内容可以滚动到透明的操作栏下这样的需求，就这样用。
    requestWindowFeature(FEATURE_ACTION_MODE_OVERLAY);// 操作栏没有展示的时候指定操作模式的行为。如果启用了覆盖 ，操作模式相关的UI会被允许覆盖已存在的内容。
    
    // 确保在检索decor的时候，window已经安装了自己的decor 
    mWindow.getDecorView();
    
    // 【B】接下来根据有没有标题分为两大类，再根据是否悬浮，是否有操作栏这样填充不同的布局文件，省略if...else...
    
    // 对话框标题装饰，漂浮的窗口永远不能有操作栏
    subDecor = (ViewGroup) inflater.inflate(R.layout.abc_dialog_title_material, null);
    // 根据上下文主题，填充有标题的布局
    subDecor = (ViewGroup) LayoutInflater.from(themedContext)
                        .inflate(R.layout.abc_screen_toolbar, null);
    // 填充操作栏可以覆盖内容的布局
    subDecor = (ViewGroup) inflater.inflate(
                        R.layout.abc_screen_simple_overlay_action_mode, null);
    // 填充一个简单的布局
    subDecor = (ViewGroup) inflater.inflate(R.layout.abc_screen_simple, null);
    
    // 【C】findViewById
    mTitleView = (TextView) subDecor.findViewById(R.id.title);
    contentView = (ContentFrameLayout) subDecor.findViewById(R.id.action_bar_activity_content);
    windowContentView = (ViewGroup) mWindow.findViewById(android.R.id.content);
    
    // 【D】覆盖内容视图，设置id，关联到对应的窗口
    if (windowContentView != null) {
        // There might be Views already added to the Window's content view so we need to
        // migrate them to our content view
        while (windowContentView.getChildCount() > 0) {
            final View child = windowContentView.getChildAt(0);
            windowContentView.removeViewAt(0);
            contentView.addView(child);
        }

        // Change our content FrameLayout to use the android.R.id.content id.
        // Useful for fragments.
        windowContentView.setId(View.NO_ID);// NO_ID = -1
        contentView.setId(android.R.id.content);

        // The decorContent may have a foreground drawable set (windowContentOverlay).
        // Remove this as we handle it ourselves
        if (windowContentView instanceof FrameLayout) {
            ((FrameLayout) windowContentView).setForeground(null);
        }
    }

    // Now set the Window's content view with the decor
    mWindow.setContentView(subDecor);
    // ...
    return subDecor;
}
```

### 1.2 入口2 `Activity`

 如果自定义`Activity`继承了`Activity`,那么点击直接进入`Activity$setContentView`，代码如下：

```java
 public void setContentView(@LayoutRes int layoutResID) {
     getWindow().setContentView(layoutResID);
     initWindowDecorActionBar();
 }
```


## 2. PhoneWindow

在[Activity9.0](<https://xushengxing.info/activity-9-0/>) 的5.2-e小节，我们遇到过`Activity$attach`方法，在这里关联了一个`PhoneWindow`，这是`Window`在`Android`里的唯一子类，因此每次`activity.getWindow()`都会得到一个`PhoneWindow`。由上一步最后：`mWindow.setContentView(subDecor);`或者入口2：`getWindow().setContentView(layoutResID);`

差别在于一个设置了布局的资源ID，一个直接设置了`View`。方式不同，但都为变量`mContentParent`添加了新的`View`，最后都将调用`ViewGroup.addView(v)`。

这里先留着这个`addView`，这里面有个`AttachInfo`，用来获取`ViewRootImpl`，在`ActivityThread$attach`中，我们曾经见到此君，当时添加了一个配置改变回调，此回调触发`Activity`的`Configuration`改变的函数，相关联的`Theme`都更新。

进入正题，看下`setContentView`的源码：

```java
@Override
public void setContentView(View view, ViewGroup.LayoutParams params) {
    // Note: FEATURE_CONTENT_TRANSITIONS may be set in the process of installing the window
    // decor, when theme attributes and the like are crystalized. Do not check the feature
    // before this happens.
    if (mContentParent == null) {
        installDecor();
    } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        mContentParent.removeAllViews();
    }
    if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        view.setLayoutParams(params);
        final Scene newScene = new Scene(mContentParent, view);
        transitionTo(newScene);
    } else {
        mContentParent.addView(view, params);
    }
    mContentParent.requestApplyInsets();
    final Callback cb = getCallback();
    if (cb != null && !isDestroyed()) {
        cb.onContentChanged();
    }
    mContentParentExplicitlySet = true;
}
```

上面代码有三个点：

1. `mContentParent`为空则创建一个: `installDecor`
2. 启用`FEATURE_CONTENT_TRANSITIONS`功能时，转场动画的处理，这里和`FEATURE_ACTIVITY_TRANSITIONS`做一个区别
3. 未启用`FEATURE_CONTENT_TRANSITIONS`时，直接填充布局或添加视图



### 2.1 mContentParent 为空的情况。

初始化链路如下：

`generateDecor`生成`DecorView`, 根据`Decor`，`generateLayout(mDecor)`生成`mContentParent`.

```java
protected DecorView generateDecor(int featureId) {
    // 系统进程不拥有应用程序的上下文，直接使用我们有的上下文。否则就使用应用程序上下文，并不依赖于Activity
    // 在构造方法中有说明，只有主Activity窗口使用 decor 上下文，其他的窗口有什么用什么
    Context context;
    if(mUseDecorContext) {// 默认是false
        Context applicationContext = getContext().getApplicationContext();
        if (applicationContext == null) {
            context = getContext();
        } else {
            context = new DecorContext(applicationContext, getContext());
            if (mTheme != -1) {
                context.setTheme(mTheme);
            }
        }
    } else {
        context = getContext();
    }
    return new DecorView(context, featureId, this, getAttributes());
}
```

`generateLayout`根据风格生成不同的标记，同时配置不同的布局文件。相关源码如下：

```java
// 应用当前主题的数据
protected ViewGroup generateLayout(DecorView decor) {
    TypedArray a = getWindowStyle(); // 默认值：com.android.internal.R.styleable.Window
    // 是否悬浮
    mIsFloating = a.getBoolean(R.styleable.Window_windowIsFloating, false);
    // 省略一些if...else...
    // 和第二步中的subDecor几乎一样的
    requestFeature(FEATURE_NO_TITLE);
    requestFeature(FEATURE_ACTION_BAR);
    requestFeature(FEATURE_ACTION_BAR_OVERLAY);
    requestFeature(FEATURE_ACTION_MODE_OVERLAY);
    requestFeature(FEATURE_SWIPE_TO_DISMISS);
    setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN & (~getForcedWindowFlags()));
    setFlags(FLAG_TRANSLUCENT_STATUS, FLAG_TRANSLUCENT_STATUS & (~getForcedWindowFlags()));
    setFlags(FLAG_TRANSLUCENT_NAVIGATION, FLAG_TRANSLUCENT_NAVIGATION
                    & (~getForcedWindowFlags()));
    setFlags(FLAG_LAYOUT_IN_OVERSCAN, FLAG_LAYOUT_IN_OVERSCAN&(~getForcedWindowFlags()));
    setFlags(FLAG_SHOW_WALLPAPER, FLAG_SHOW_WALLPAPER&(~getForcedWindowFlags()));
    setFlags(FLAG_SPLIT_TOUCH, FLAG_SPLIT_TOUCH&(~getForcedWindowFlags()));
    // 状态栏颜色
    mStatusBarColor = a.getColor(R.styleable.Window_statusBarColor, 0xFF000000);
    // 导航栏颜色
    mNavigationBarColor = a.getColor(R.styleable.Window_navigationBarColor, 0xFF000000);
    mNavigationBarDividerColor = a.getColor(R.styleable.Window_navigationBarDividerColor,
                    0x00000000);
    // 根据主题，配置decor相关的属性，略。
    
    int layoutResource;// 将要填充的布局id，省略if...else...
    // 从左向右滑动，退出Activity
    layoutResource = R.layout.screen_swipe_dismiss;
	setCloseOnSwipeEnabled(true);
    // 操作栏带左/右图标&浮动窗口
    TypedValue res = new TypedValue();
    getContext().getTheme().resolveAttribute(R.attr.dialogTitleIconsDecorLayout, res, true);
    layoutResource = res.resourceId;
    // 操作栏带左/右图标的布局
    layoutResource = R.layout.screen_title_icons;
    // 操作栏带进度条
    layoutResource = R.layout.screen_progress;
    // 普通标题浮动
    TypedValue res = new TypedValue();
    getContext().getTheme().resolveAttribute(R.attr.dialogCustomTitleDecorLayout, res, true);
    layoutResource = res.resourceId;
    // 普通标题
    layoutResource = R.layout.screen_custom_title;
    // 无标题
    layoutResource = R.layout.screen_title;
    // 操作栏浮动在内容上
    layoutResource = R.layout.screen_simple_overlay_action_mode;
    // 最普通的
    layoutResource = R.layout.screen_simple;
    // 交给DecorView处理
    mDecor.startChanging();
    mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);
    ...
    mDecor.finishChanging();
    return contentParent;
}
```



### 2.2 启用 `FEATURE_CONTENT_TRANSITIONS` 

这里的目的是要执行一次转场动画。调用链路：`PhoneWindow$transitionTo`  --> `TransitionManager$transitionTo`  --> `TransitionManager$changeScene`  --> `Scene.enter`

```
    public void enter() {
        // Apply layout change, if any
        if (mLayoutId > 0 || mLayout != null) {
            // empty out parent container before adding to it
            getSceneRoot().removeAllViews();

            if (mLayoutId > 0) {
                LayoutInflater.from(mContext).inflate(mLayoutId, mSceneRoot);
            } else {
                mSceneRoot.addView(mLayout);
            }
        }

        // Notify next scene that it is entering. Subclasses may override to configure scene.
        if (mEnterAction != null) {
            mEnterAction.run();
        }

        setCurrentScene(mSceneRoot, this);
    }
```

### 2.3  在`DecorView`，`mContentParent`都存在，没有启用`FEATURE_CONTENT_TRANSITIONS` 的情况

直接或间接都会走到`ViewGroup.addView(v)`

## 3. DecorView

### 3.1 构造生成`Decor`

```java
DecorView(Context context, int featureId, PhoneWindow window,WindowManager.LayoutParams params) {
    // ...
    // 设置进入动画
    // 设置退出动画
    // 状态栏颜色
    // 更新可用宽度
    // 绑定当前窗口
    // 初始化横向，竖向两支画笔
    // ...
}
```



### 3.2  加载布局`onResourcesLoaded`

```java
    void onResourcesLoaded(LayoutInflater inflater, int layoutResource) {
        // 大小改变时候绘制的背景，比如destory时候
        if (mBackdropFrameRenderer != null) {
            loadBackgroundDrawablesIfNeeded();
            mBackdropFrameRenderer.onResourcesLoaded(
                    this, mResizingBackgroundDrawable, mCaptionBackgroundDrawable,
                    mUserCaptionBackgroundDrawable, getCurrentColor(mStatusColorViewState),
                    getCurrentColor(mNavigationColorViewState));
        }
		// 自由浮动窗口时用到的。了解下画中画，分屏，自由窗口 
        mDecorCaptionView = createDecorCaptionView(inflater);
        final View root = inflater.inflate(layoutResource, null);
        // 如果浮动窗口，先加浮动窗口，再加传入的布局
        if (mDecorCaptionView != null) {
            if (mDecorCaptionView.getParent() == null) {
                addView(mDecorCaptionView,
                        new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            }
            mDecorCaptionView.addView(root,
                    new ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT));
        } else {

            // Put it below the color views.
            addView(root, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        }
        mContentRoot = (ViewGroup) root;
        // 设置Z轴的高度，即阴影
        initializeElevation();
    }
```

[支持多窗口模式](https://source.android.google.cn/devices/tech/display/multi-window#freeform)

## 4. LayoutInflater

源码中使用`LayoutInflater$inflate`来扩充布局，生成`View`。

### 4.1 获取一个Inflater实例

```
LayoutInflater inflater = LayoutInflater.from(context);
```

```java
public static LayoutInflater from(Context context) {
    LayoutInflater LayoutInflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    if (LayoutInflater == null) {
        throw new AssertionError("LayoutInflater not found.");
    }
    return LayoutInflater;
}
```

这里看到常用的代码：`(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);`

那么是怎么来的呢？我们这是`Context`的一个抽象方法，在`ContextWrapper`我们发现是个代理方法，具体的实现在`ContextImpl`中。调用链路如下：

`ContextImpl$getSystemService`  --> `SystemServiceRegistry$getSystemService`

在`SystemServiceRegistry`中看到一个很巨的`static`方法，根据关键字`LAYOUT_INFLATER_SERVICE`，找到该服务初始化的地方：

```java
registerService(Context.LAYOUT_INFLATER_SERVICE, LayoutInflater.class,
        new CachedServiceFetcher<LayoutInflater>() {
    @Override
    public LayoutInflater createService(ContextImpl ctx) {
        return new PhoneLayoutInflater(ctx.getOuterContext());
    }});
```

`registerService` 静态方法，使用两个`HashMap`，一个存放服务`Fetcher`和对应的常量名字；一个存放类和常量名字。懒加载的方式，获取时再实例化。



看到一个`LayoutInflater`的子类`PhoneLayoutInflater`，这里定义了几个类前缀：

```java
    private static final String[] sClassPrefixList = {
        "android.widget.",
        "android.webkit.",
        "android.app."
    };
```

在`PhoneLayoutInflater$onCreateView`触发时候，调用父类方法`LayoutInflater$createView`循环创建对应前缀的`view`.



### 4.2 inflate

一般使用`View root = inflater.inflate(layoutResource, null);`来扩充已有的视图层级。

```java
/**
*
* @param parser 包含了视图层级描述的XML dom 节点
* @param root 视图可以用作生成视图层级的父级视图
* @param attachToRoot 是否将扩张后的层级添加到根视图参数。如果不，
*/
public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
    // 在参数里，我们的xml被转成了XmlPullParser
    synchronized (mConstructorArgs) { // 同步锁构造方法里的参数
            final Context inflaterContext = mContext;
    		// 根据parser得到属性集
			final AttributeSet attrs = Xml.asAttributeSet(parser);
    		// 取出上一次的上下文，然后把这次构造传入的上下文放入
			Context lastContext = (Context) mConstructorArgs[0];
			mConstructorArgs[0] = inflaterContext;
            // 如果root ！= null，扩充视图树后，返回其父视图
			View result = root;
        
        	// 处理 merge 标签
        	if (TAG_MERGE.equals(name)) {
    			if (root == null || !attachToRoot) {
        			throw new InflateException("<merge /> can be used only with a valid "
                			+ "ViewGroup root and attachToRoot=true");
    			}
    			rInflate(parser, root, inflaterContext, attrs, false);
            } else {
                final View temp = createViewFromTag(root, name, inflaterContext, attrs);
                rInflateChildren(parser, temp, attrs, true);
                if (root != null && attachToRoot) {
    				root.addView(temp, params);
				}
                if (root == null || !attachToRoot) {
                        result = temp;
                }
            }
		return result;
    }
}
```

#### 4.2.1 获取XMLPullParser

调用链路：`LayoutInflater$inflate` --> `Resources$getLayout`  --> `Resources$loadXmlResourceParser`  --> `ResourcesImpl$loadXmlResourceParser` -->  `AssetManager$openXmlBlockAsset` -- > `XmlBlock$newParser`

其实最后得到的是`XmlBlock$Parser`的实例，实现了`XmlPullParser`和`AttributeSet`接口，用来封装和`xml`对应的文档标记。

#### 4.2.2 AttributeSet

#### 4.2.3 rInflate

递归方法，下降遍历`xml`层级并且实例化视图及其子视图，最后调用`onFinishInflate`

```
void rInflate(XmlPullParser parser, View parent, Context context,
            AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException
	// 深度。根节点外，深度是0.每遇到一个开始标记深度就+1；每遇到一个结束标记深度就-1；
	final int depth = parser.getDepth();
	// 没有到达结束标记或者还有未完成的标记并且没有达到文档结尾
	while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
                    // 接下来处理特殊标记
                    TAG_REQUEST_FOCUS -- "requestFocus"
                    TAG_TAG  --  "tag"
                    TAG_INCLUDE  --  "include"
                    TAG_MERGE -- "merge"
                    
                     // 如果不是上述的标记，递归父节点
                	final View view = createViewFromTag(parent, name, context, attrs);
					final ViewGroup viewGroup = (ViewGroup) parent;
					final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
					rInflateChildren(parser, view, attrs, true);// 这里还是 rInflate()
					viewGroup.addView(view, params);
                }

}
```



#### 4.2.4  createViewFromTag

在`4.2.2`，看到这个方法，生成一个`View`，然后作为跟视图传入了递归方法里。具体生成视图的代码在`createView`中：

```java
    public final View createView(String name, String prefix, AttributeSet attrs)
            throws ClassNotFoundException, InflateException {
            // 看到这里应该联想到：常用控件，应该都有缓存
             Constructor<? extends View> constructor = sConstrutorMap.get(name);
             if (constructor != null && !verifyClassLoader(constructor)) { // ---------- 1
                 constructor == null;
                 sConstructorMap.remove(name);
             }
        	 Class< ? extends View> clazz = null;
        try {
            if (constructor == null) { // 缓存中为空，使用当前上下文的类加载器加载得到其实例，并加入缓存Map<String, Constructor>
                // 看呐，loadClass，双亲委派实现之地
                clazz = mContext.getClassLoader().loadClass( // ---------- 2
                prefix != null ? (prefix + name) : name).asSubclass(View.class);
                // 根据传入的规则过滤，比如在RemoteViews$onLoadClass中，就定义了: clazz.isAnnotationPresent(RemoteView.class); 类必须有这个注解才可以inflate
                if (mFilter != null && clazz != null) {
                    boolean allowed = mFilter.onLoadClass(clazz);
                    if (!allowed) {
                        failNotAllowed(name, prefix, attrs);
                    }
                }
                
                // 获取类构造器加入缓存
                constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name, constructor);
                
            } else { 
                // 复用缓存中的类构造，然后查看过滤规则的缓存，看是否是允许状态，即可以inflate，如果是第一次判断，则判断后如果成功则加入
                if (mFilter != null) {
                    // Have we seen this name before? 
                    // filter的缓存中有吗？
                    Boolean allowedState = mFilterMap.get(name);
                    if (allowedState == null) { // 没，根据其规则判断后加入filter缓存
                        // New class -- remember whether it is allowed
                        clazz = mContext.getClassLoader().loadClass(
                                prefix != null ? (prefix + name) : name).asSubclass(View.class);

                        boolean allowed = clazz != null && mFilter.onLoadClass(clazz);
                        mFilterMap.put(name, allowed);
                        if (!allowed) {
                            failNotAllowed(name, prefix, attrs);
                        }
                    } else if (allowedState.equals(Boolean.FALSE)) { // 有，但是false
                        failNotAllowed(name, prefix, attrs);
                    }
                }

            }
            
            // ...  省略准备args代码
            final View view = constructor.newInstance(args);
            if (view instanceof ViewStub) {
                // Use the same context when inflating ViewStub later.
                final ViewStub viewStub = (ViewStub) view;
                viewStub.setLayoutInflater(cloneInContext((Context) args[0]));
            }
            mConstructorArgs[0] = lastContext;
            return view;
            
        } catch (...) { // 省略待捕获异常
            // NoSuchMethodException
            // ClassCastException
            // ClassNotFoundException
            // Exception
        } finally { ... }
        
	}
```

 在`1`处看到，`verifyClassLoader`，来看看其实现：

```java

private static final ClassLoader BOOT_CLASS_LOADER = LayoutInflater.class.getClassLoader();
// 双亲委派，如果父类的加载器在，直接用  
private final boolean verifyClassLoader(Constructor<? extends View> constructor) {
        final ClassLoader constructorLoader = constructor.getDeclaringClass().getClassLoader();
        if (constructorLoader == BOOT_CLASS_LOADER) {
            // fast path for boot class loader (most common case?) - always ok
            return true;
        }
        // in all normal cases (no dynamic code loading), we will exit the following loop on the
        // first iteration (i.e. when the declaring classloader is the contexts class loader).
        ClassLoader cl = mContext.getClassLoader();
        do {
            if (constructorLoader == cl) {
                return true;
            }
            cl = cl.getParent();
        } while (cl != null);
        return false;
    }
```

在`2`处看到`LoadClass`，摘记源码如下：

```java
protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // 类是否已加载，本地方法快速
    Class<?> c = findLoadedClass(name);
    if (c == null) {
        try {
            // 使用父类加载器加载类
            if (parent != null) {
                c = parent.laodClass(name,false);
            } else { // 父类加载器为空，使用内建在虚拟机中的类加载器替代
                c = findBootstrapClassOrNull(name);
            }
        }catch(ClassNotFoundException e) {
            
        }
        
        if (c == null) {
            // 使用findClass方法，这个类必须被重写类加载器的实现类，直接调用会抛异常
            // 比如PathClassLoader，插件化时候必用到的，就实现了自己的findClass
            c = findClass(name);
        }
    }
    return c;
}
```

到此，根据传入的布局，已经生成`View`并且加入了`DecorView`的`content`中了。即`UI`上显示的『控件』或者『视图』，那`View`上的绘制，事件响应是怎样的？

## 5 绘制

上面四步都基于`Activity$onCreate`中的`setContentView`分析的。

在[Activity 9.0](<https://zhuanlan.zhihu.com/p/63367009>) 中我们提到：在`onCreate`之后，有`getLifeCyclePath`返回下一个生命周期`onStart`，在`onStart`里，主要是恢复状态的操作。再下一步是`onResume`，这样就执行到`ActivityThread$handleResumeActivity`。

这个方法的最后一句是我们熟悉的：

```java
Looper.myQueue().addIdleHandler(new Idler());
```

在[Activity 9.0](<https://zhuanlan.zhihu.com/p/63367009>) 中已经解析过了，这里的空闲是用来处理上个活动的`stop`和`destory`的。 

### 5.1 handleResumeActivity

```java
@Override public void handleResumeActivity(IBinder token, boolean finalStateRequest, boolean isForward, String reason) {
    // 转到后台后准备好GC了，但是我们又回来了(resume)，所以跳过GC。
    unscheduleGcIdler();
    mSomeActivitiesChanged = true;// 有活动状态改变吗？有，在获取焦点
    // 获取恢复活动的ActivityClientRecord
    final ActivityClientRecord r = performResumeActivity(token, finalStateRequest, reason);
    if (r == null) {
        // We didn't actually resume the activity, so skipping any follow-up actions.
     	return;
    }
    final Activity a = r.activity;
    
    boolean willBeVisible = !a.mStartedActivity;
    if (!willBeVisible) {
        willBeVisible = ActivityManager.getService().willActivityBeVisible(a.getActivityToken());
    }
    // 活动相关的窗口还没添加上；活动不是要关掉自己并开启别的活动，那么去添加窗口。
    if (r.window == null && !a.mFinished && willBeVisible) {
        // ...
        // 设置下window，设置下decor，设置下LayoutParams，略。
        
        // 根视图更新。这里的实现在View$getViewRootImpl
        ViewRootImpl impl = decor.getViewRootImpl();
        if (impl != null) {
            // 从窗口的视角，通知子视图重建了。此时，DecorView不变，但是控制DecorView的活动变了，我们需要更新回调
            impl.notifyChildRebuilt();
        }
    } else if (!willBeVisible) {
        // 如果窗口已经被加到活动里了，但是在恢复期间又打开别的活动，那就隐藏窗口。
        r.hideForNow = true;
    }
    if (!r.activity.mFinished && willBeVisible && r.activity.mDecor != null && !r.hideForNow) {
        // 根据窗口Configuration通知回调修改
        // 组一下LayoutParams，软键盘
        ...
       ViewManager wm = a.getWindowManager();
       View decor = r.window.getDecorView();
       wm.updateViewLayout(decor, l); // 【*】
    }
    Looper.myQueue().addIdleHandler(new Idler());
}
```

### 5.2 updateViewLayout

在上一步中来到了`WindowManager$updateViewLayout` 方法中。剧透下，这里就能看到熟悉的`measure`, `layout`, `draw` 相关方法了。

`WindowManager`是行为类，他的实现类`WindowManagerImpl`，这个类不干活的，他和`Context`打交道，管理干活的`WindowManagerGlobal`, `WidowManagerGlobal` 根据`AttachInfo`处理`ViewRootImpl`。

```java
public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
    // ...
    synchronized (mLock) {
        int index = findViewLocked(view, true);
        ViewRootImpl root = mRoots.get(index);
        mParams.remove(index);
        mParams.add(index, wparams);
        root.setLayoutParams(wparams, false);
    }
}
```

`ViewRootImpl$setLayoutParams`， 又一次设置布局参数，我为啥要说又。。。各种标记满天飞，我们看重的代码就一行：

```java
void setLayoutParams(WindowManager.LayoutParams attrs, boolean newView) {
    synchronized (this) {
        // ...
        scheduleTraversals();
        // ...
    }
}
```

`ViewRootImpl$scheduleTraversals`里有个变量：`final TraversalRunnable mTraversalRunnable = new TraversalRunnable();`，这是个子线程，在`run`方法中，有`doTraversal`，然后调用了`PerformTraversal`，在这里调用了`performMeasure`, `performLayout`, `performDraw`，分别对应了`measure`, `layout`, `draw`三个方法，如果我们自定义`View`，实现三大法宝：`onMeasure`, `onLayout`, `onDraw`这样也都对应上了。

### 5.3 performTraversals

接近1000行的方法，多略略略。

```java
private void performTraversals() {
    // 准备了一个WindowManager.LayoutParams
    // ...
    if (mFirst) {// true：视图第一次添加
    	// 需要完全重画
        mFullRedrawNeeded = true; 
        // 布局
        mLayoutRequested = true;
        // 从Configuration$setToDefaults获取一个默认的配置
        final Configuration config = mContext.getResources().getConfiguration();
        // 设计窗口的宽高
        // 接下来：使用32位绘制缓存，是否有焦点，是否可见，重新计算全局属性，布局朝向 
        
    } else {
        // 设计窗口的宽高
    }
    
    // 视图的可见性没变
    if (viewVisibilityChanged) {
        // 通知view
        host.dispatchWindowVisibilityChanged(viewVisibility);
        // 判断VISIBLE，GONE
    }
    // 根据attachInfo，设置触摸，宽高，软键盘之类
    // 在Surface可用的判断和处理
    
    // 该变量很有用，用于执行帧同步相关操作，涉及Native代码
    final ThreadedRenderer threadedRenderer = mAttachInfo.mThreadedRenderer;
    
    // -------------------------------------------------------------------
    // 其实就是为了找到下面三个方法：
    
    // 上面一直在提的宽高有了用武之地
    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
    performLayout(lp, mWidth, mHeight);
    performDraw();
}
```



### 5.4 View 三大将

测量，布局，画上。这样一个`View`就完工了。

#### 5.4.1 测量

(一) measure

`measure(int widthMeasureSpec, int heightMeasureSpec)` 这个调用用来找到`View`应该多大。父视图在宽高参数中提供了约束信息。

`view`具体的测量工作在`onMeasure`中，是由`measure`调用的。然而，只有`onMeasure`可以，也必须被子类重写。

```java
/**
*
* @param widthMeasureSpec 父控件施加的水平空间要求
* @param heightMeasureSpec 父控件施加的垂直空间要求
*/
public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
    // ...
    onMeasure(widthMeasureSpec, heightMeasureSpec);
    // ...
}
```

(二)onMeasure

`onMeasure(int widthMeasureSpec, int heightMeasureSpec)` 测量视图和内容以确定测量宽度和测量高度。

**约定** ： 重写`onMeasure`**必须**调用`setMeasureDimension(int, int)`以存储此视图的测量宽高。失败的话，会触发非法状态异常。

基类测量的实现默认是背景的尺寸大小，除非`MeasureSpec`允许了更大的尺寸。子类应该重写`onMeasure` 提供更好的子类内容的测量。

如果重写了`onMeasure`，确保测量宽高最少是视图的最小宽高的任务就是子类的了。

`onMeasure(int, int)` --> `setMeasureDimension` --> `setMeasuredDimensionRaw`

#### 5.4.1.1 扩展：测量模式



| MeasureSpec |                  释义                  |          应用           |
| :---------: | :------------------------------------: | :---------------------: |
|   AT_MOST   |     在父视图尺寸内，子视图没有限制     |      WRAP_CONTENT       |
|   EXACTLY   |           子视图有明确的尺寸           | MATCH_PARENT & 具体宽高 |
| UNSPECIFIED | 父视图没有任何限制，子视图可以随意设置 |       SCROLLVIEW        |

(一)在顶级`View`中，测量规范由布局参数确定：`ViewRootImpl$getRootMeasureSpec`

```java
/**
* 基于布局参数指出根视图的测量规范。
*/
private static int getRootMeasureSpec(int windowSize, int rootDimension) {
    int measureSpec;
    switch (rootDimension) {
        case ViewGroup.LayoutParams.MATCH_PARENT:
            // Window can't resize. Force root view to be windowSize.
            // 窗口大小不可变，强制根视图为窗口大小。
            measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.EXACTLY);
            break;
        case ViewGroup.LayoutParams.WRAP_CONTENT:
            // Window can resize. Set max size for root view.
            // 窗口大小不可改变，设置根视图为最大尺寸。
            measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.AT_MOST);
            break;
        default:
            // Window wants to be an exact size. Force root view to be that size.
            // 窗口想要确切的大小，强制根视图也是那么大。
            measureSpec = MeasureSpec.makeMeasureSpec(rootDimension, MeasureSpec.EXACTLY);
            break;
        }
        return measureSpec;
    }
}
```

(二) 子视图的测量规范：由父容器和自身的布局参数决定

| 横：parent<br />竖：child |       EXACTLY        |       AT_MOST        |    UNSPECIFIED     |
| :-----------------------: | :------------------: | :------------------: | :----------------: |
|            DP             |  EACTLY(child size)  |  EACTLY(child size)  | EACTLY(child size) |
|       MATCH_PARENT        | EACTLY(parent size)  | AT_MOST(parent size) |   UNSPECIFIED(0)   |
|       WRAP_CONTENT        | AT_MOST(parent size) | AT_MOST(parent size) |   UNSPECIFIED(0)   |



#### 5.4.1.2 扩展：座标系

(一) 手指触摸屏幕：

![](http://cdn.xushengxing.info/motion_event.png)

| MotionEvent |              释义               |
| :---------: | :-----------------------------: |
|   getX()    | 到当前view的左上角圆点的x轴距离 |
|   getY()    | 到当前view的左上角圆点的y轴距离 |
|  getRawX()  | 到当前屏幕的左上角圆点的x轴距离 |
|  getRawY()  | 到当前屏幕的左上角圆点的x轴距离 |

(二) View相对于父View

![](http://cdn.xushengxing.info/view_coordinate.png)

视图座标系：

|       View        |               释义               |
| :---------------: | :------------------------------: |
|     getLeft()     |    自身最左边到父布局的最左边    |
|     getTop()      |    自身最上边到父布局的最上边    |
|    getRight()     |   自身最右边到父布局的最左边边   |
|    getBottom()    |    自身最下边到父布局的最上边    |
| getTranslationX() |   相对于getLeft()的横向偏移量    |
| getTranslationY() |    相对于getTop()的纵向偏移量    |
| getTranslationZ() | 相对于getElevation()的深度偏移量 |

#### 5.4.1.3  扩展：scrollTo, scrollBy

关键变量：

```java
    /**
     * The offset, in pixels, by which the content of this view is scrolled
     * horizontally. 偏移量。该视图内容的横向滚动像素值。
     * {@hide}
     */
    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollX;
    /**
     * The offset, in pixels, by which the content of this view is scrolled
     * vertically.偏移量。该视图内容的横向滚动像素值。
     * {@hide}
     */
    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollY;

```

scrollTo, scrollBy的关联：根据名字也能区分那个是滚到`(x,y)`，滚了`(x,y)`。

```java

    /**
     * Move the scrolled position of your view. This will cause a call to
     * {@link #onScrollChanged(int, int, int, int)} and the view will be
     * invalidated. 
     * @param x the amount of pixels to scroll by horizontally
     * @param y the amount of pixels to scroll by vertically
     */
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX + x, mScrollY + y);
    }

    public void scrollTo(int x, int y) {
        if (mScrollX != x || mScrollY != y) {
            int oldX = mScrollX;
            int oldY = mScrollY;
            mScrollX = x;
            mScrollY = y;
            invalidateParentCaches();
            onScrollChanged(mScrollX, mScrollY, oldX, oldY);
            if (!awakenScrollBars()) {
                postInvalidateOnAnimation();
            }
        }
    }
```

代码里直接说到：滚动到指定位置，会调用`onScrollChanged`，然后引起视图(无效)重绘。

然后进入了常规套路，`measure`, `layout`, `draw`,在`draw`的时候，有相关`drawScrollBar`的方法，在这里传入了`View`的`Rect`，调用链路：

`View$postInvalidateOnAnimation` --> `ViewRootImpl$dispatchInvalidateOnAnimation`  --> `InvalidateOnAnimationRunnable$run`  -->  `View$invalidate()` --> `View$invalidateInternal` -->  `ViewRootImpl$invalidateChild` --> `ViewRootImpl$invalidateChildInParent` --> `ViewRootImpl$invalidate` --> `scheduleTraversals` --> `ViewRootImpl$doTraversals` --> `ViewRootImpl$performTraversals` --> `ViewRootImpl$performDraw`--> `ViewRootImpl$draw`--> `ViewRootImpl$drawSoftware`  --> `View.draw` --> `View.onDrawFroeground` --> `View.onDrawScrollBars` -->  `View.invalidate(Rect)` --> `View.invalidateInternal(int,int,int,int)`

```java
/**
 * 标记一块需要重绘的脏区域。
 *
 */
@Deprecated
    public void invalidate(Rect dirty) {
        final int scrollX = mScrollX;
        final int scrollY = mScrollY;
        invalidateInternal(dirty.left - scrollX, dirty.top - scrollY,
                dirty.right - scrollX, dirty.bottom - scrollY, true, false);
    }
```

到这里，使用`scrollTo`, `scrollBy`传入正值，视图却反向移动的原因找到了。

#### 5.4.1.4 `onMeasure()` 中常用方法

```java
getChildCount()：获取子View的数量；
getChildAt(i)：获取第i个子控件；
subView.getLayoutParams().width/height：设置或获取子控件的宽或高；
measureChild(child, widthMeasureSpec, heightMeasureSpec)：测量子View的宽高；
child.getMeasuredHeight/width()：执行完measureChild()方法后就可以通过这种方式获取子View的宽高值；
getPaddingLeft/Right/Top/Bottom()：获取控件的四周内边距；
setMeasuredDimension(width, height)：重新设置控件的宽高。如果写了这句代码，就需要删除“super.onMeasure(widthMeasureSpec, heightMeasureSpec);”这行代码。
```



### 5.4.2 布局

在`5.3$performTraversals`，也调用了`performLayout。` 然后调用了`View.layout`

`ViewRootImpl$performTraversals`

```java
    private void performLayout(WindowManager.LayoutParams lp, int desiredWindowWidth,
            int desiredWindowHeight) {
        // ...
        final View host = mView;
        host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());
        // ...
    }
```

`View$layout`

```java
public void layout(int l, int t, int r, int b) {
    // ...
    onLayout(changed, l, t, r, b);
    // ...
}
```

####  5.4.2.1 LinearLayout$onLayout

以`LinearLayout`为例，我们看下`onLayout`实现的典范：

```java
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mOrientation == VERTICAL) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }
    }
```

垂直布局的实现：

```java
void layoutVertical(int left, int top, int right, int bottom) {
    	// 像素值，表示当前视图的左边和视图内容的左边这么一段距离。
        final int paddingLeft = mPaddingLeft;

        int childTop;
        int childLeft;

        // Where right end of child should go
        final int width = right - left;
    	// 视图内容的最右边
        int childRight = width - mPaddingRight;

        // Space available for child，子视图可用空间
        int childSpace = width - paddingLeft - mPaddingRight;
		// 返回子视图的虚拟数量，如果子视图里有TableLayout,TableRow这样的，数量是不定的
        final int count = getVirtualChildCount();
		// Gravity，用来计算childTop，顶部距离父视图顶部的大小
        final int majorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        switch (majorGravity) {
           case Gravity.BOTTOM:
               // mTotalLength contains the padding already
               childTop = mPaddingTop + bottom - top - mTotalLength;
               break;

               // mTotalLength contains the padding already
           case Gravity.CENTER_VERTICAL:
               childTop = mPaddingTop + (bottom - top - mTotalLength) / 2;
               break;

           case Gravity.TOP:
           default:
               childTop = mPaddingTop;
               break;
        }

        for (int i = 0; i < count; i++) {
            final View child = getVirtualChildAt(i);
            if (child == null) {
                childTop += measureNullChild(i);
            } else if (child.getVisibility() != GONE) { // 子视图可见
                // measure之后才可以调用的方法
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final LinearLayout.LayoutParams lp =
                        (LinearLayout.LayoutParams) child.getLayoutParams();

                int gravity = lp.gravity;
                if (gravity < 0) {
                    gravity = minorGravity;
                }
                // 返回该视图的布局方向
                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                // 子视图左边距离父视图左边的距离，上面我们判断了上边，又拥有totalLength，这样位置定了
                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                                + lp.leftMargin - lp.rightMargin;
                        break;

                    case Gravity.RIGHT:
                        childLeft = childRight - childWidth - lp.rightMargin;
                        break;

                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + lp.leftMargin;
                        break;
                }

                if (hasDividerBeforeChildAt(i)) {
                    childTop += mDividerHeight;
                }

                childTop += lp.topMargin;
                // 实际上调用了View$layout方法
                setChildFrame(child, childLeft, childTop + getLocationOffset(child),
                        childWidth, childHeight);
                childTop += childHeight + lp.bottomMargin + getNextLocationOffset(child);

                i += getChildrenSkipCount(child, i);
            }
        }
    }
```

#### 5.4.2.2 RecyclerView$onLayout

进来一看，调用链路如下：`onLayout` --> `dispatchLayout` 

```java
void dispatchLayout {
    mState.mIsMeasuring = false;
    if (mState.mLayoutStep == State.STEP_START) {
        dispatchLayoutStep1();
        mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
    } else if (mAdapterHelper.hasUpdates() || mLayout.getWidth() != getWidth()
            || mLayout.getHeight() != getHeight()) {
        // First 2 steps are done in onMeasure but looks like we have to run again due to
        // changed size.
        mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
    } else {
        // always make sure we sync them (to ensure mode is exact)
        mLayout.setExactMeasureSpecsFrom(this);
    }
    dispatchLayoutStep3();
}
```

`dispatchLayoutStep1`:

```java
private void dispatchLayoutStep1() {
    // 处理适配器更新
    // 决定运行哪个动画
    // 保存当前视图的信息
    // 必要情况下，运行预测的布局并保存信息
}
```

`dispatchLayoutStep2`: 执行视图实际布局的最终状态，如果有必要，可能会执行好几次比如测量时。

`dispatchLayoutStep3`: 最后一步，为动画保存视图信息，做一些必要的清理工作。

### 5.4.3 绘制

看到`View$draw`方法，不禁赞叹一句，作者太贴心了，注释已经写好了绘制的顺序，现翻译如下：

1. 绘制背景
2. 如有必要，保存画布的图层，为淡入淡出做好准备
3. 绘制视图内容
4. 绘制子视图
5. 如有必要，绘制淡入淡出的边缘，并且还原图层
6. 绘制装饰空间，比如滚动条

## 6 别的话

到这里，`UI`已经展示，似乎已经结束了。但是，还很不够，原因如下：

1. 只是`Android`框架层实现的追踪，至于真正的干货还在`skia`,`openGLES`库的底层实现
2. 只是从用户角度，直观的纵向以`View`的展示为主线追踪，说到横向的整个`GUI`系统，还需要有更大的全局观

##  总结

1. `LayoutInflater `使用`pull`递归地解析`xml`
2. `AttributeSet`定义了`xml`节点属性解析的`api`
3. 反射生成`View`
4. `View `进行测量，布局，绘制

 

















































