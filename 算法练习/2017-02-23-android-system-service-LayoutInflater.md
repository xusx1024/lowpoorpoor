---
layout: post
title:  Android系统服务：LayoutInflater
date:   2017-02-23
categories: Android System Framework
tag: android
---
 

#### 起源 ####
 
activity是android中最常用的UI容器。把写好的xml布局通过`setContentView(int ResId)`，就可以实现页面的绑定，然后我们根据控件的ID，根据业务代码展示UI就可以了。关于xml的解析和绘制，就是系统服务`LayoutInflater`的工作了。

##### 追踪android.app.Activity #####
  
Activity源码中有3个`setContentView`的重载方法。

    public void setContentView(@LayoutRes int layoutResID) {
        getWindow().setContentView(layoutResID);
        initWindowDecorActionBar();
    }

    public void setContentView(View view) {
        getWindow().setContentView(view);
        initWindowDecorActionBar();
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().setContentView(view, params);
        initWindowDecorActionBar();
    }

可以看到，调用当前Activity的window的`setContentView`来布局，这里我们上一张图看一下activity，window，view(xml布局)的关系：

![activity、window层级关系图](/images/activity-window-layer.jpg)

其中`com.android.internal.policy.PhoneWindow`是`android.view.Window`的子类。<br/>
DecorView是的PhoneWindow的顶级view，继承自FrameLayout，其内部包含一个竖直的LinearLayout。<br/>
TitleView即ActionBar所在位置，通常使用Theme来配置其状态，一般开发会屏蔽该ActionBar，使用自定义顶部栏。<br/>
ContentView即需要展示在UI上的xml布局文件。<br/>

继续看源码，getWindow()方法获取的是一个PhoneWindow的实例。所以setContentView可以去PhoneWindow中去查看其实现。<br/>
`initWindowDecorActionBar();`里面主要是设置ActionBar。

 
##### 追踪com.android.internal.policy.PhoneWindow #####

先简后繁，看一下现成的view如何添加到ContentView的：
	
		 @Override
		    public void setContentView(View view) {
		        setContentView(view, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
		    }
	
	 	@Override
	    public void setContentView(View view, ViewGroup.LayoutParams params) {
	        
			...
	
			mContentParent.addView(view, params);
	
			...
	
	     }

把已有的view设置进入`ContentView`，只需要调用`ViewGroup.addView(View view)`添加到DecorView即可。<br/>
这里提一句：view都是要添加到父view上的。 

	public static final int ID_ANDROID_CONTENT = com.android.internal.R.id.content;

这个ID对应的ViewGroup，即xml布局的父view。其源码注释：

>    This is the view in which the window contents are placed. It is either mDecor itself, or a child of mDecor where the contents go.
>    这是要放置window内容的view。是DecorView本身，或是DecorView的孩子。

总之，一句话，现成的view，亦或是将来的LayoutInflater生成的view，都要添加到ID_ANDROID_CONTENT之内。
     

下面，来看一下xml文件如何变成view的：


	@Override
	    public void setContentView(int layoutResID) {
	        
		...

	            mLayoutInflater.inflate(layoutResID, mContentParent);

		...
	        
	    }

一目了然，调用系统服务LayoutInflater的inflate方法。

#### 追踪LayoutInflater源码 ####

关键方法：

- inflate * n
- rInflate
- createViewFromTag
- createView



##### inflate #####

    public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot) {
        final Resources res = getContext().getResources();
        if (DEBUG) {
            Log.d(TAG, "INFLATING from resource: \"" + res.getResourceName(resource) + "\" ("
                    + Integer.toHexString(resource) + ")");
        }

        final XmlResourceParser parser = res.getLayout(resource);
        try {
            return inflate(parser, root, attachToRoot);
        } finally {
            parser.close();
        }
    }

获取XmlResourceParser(XmlPullParser的子类)的实例，用于待会遍历xml文件结点。

	
	    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
	        synchronized (mConstructorArgs) {
	
		...
		
		 final String name = parser.getName();
		 if (TAG_MERGE.equals(name)) {
		                    
		...
		
	    rInflate(parser, root, inflaterContext, attrs, false);
	
		...
	
	    } else {
	
		...
	
	 	rInflateChildren(parser, temp, attrs, true);
	
		...
	
				}
			}
		}

由于merge标签，只能作为xml根标签使用，所以分开处理。


##### rInflate #####

	 void rInflate(XmlPullParser parser, View parent, Context context,
	            AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {
	
	        final int depth = parser.getDepth();
	        int type;
	
	        while (((type = parser.next()) != XmlPullParser.END_TAG ||
	                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
	
	            if (type != XmlPullParser.START_TAG) {
	                continue;
	            }
	
	            final String name = parser.getName();
	            
	            if (TAG_REQUEST_FOCUS.equals(name)) {
	                parseRequestFocus(parser, parent);
	            } else if (TAG_TAG.equals(name)) {
	                parseViewTag(parser, parent, attrs);
	            } else if (TAG_INCLUDE.equals(name)) {
	                if (parser.getDepth() == 0) {
	                    throw new InflateException("<include /> cannot be the root element");
	                }
	                parseInclude(parser, context, parent, attrs);
	            } else if (TAG_MERGE.equals(name)) {
	                throw new InflateException("<merge /> must be the root element");
	            } else {
	                final View view = createViewFromTag(parent, name, context, attrs);
	                final ViewGroup viewGroup = (ViewGroup) parent;
	                final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
	                rInflateChildren(parser, view, attrs, true);
	                viewGroup.addView(view, params);
	            }
	        }
	
	        if (finishInflate) {
	            parent.onFinishInflate();
	        }
	    }

深度优先遍历xml树，判断标签类型，循环递归直至`XmlPullParser.END_DOCUMENT`。

##### createViewFromTag #####
		
		View createViewFromTag(View parent, String name, Context context, AttributeSet attrs,
		            boolean ignoreThemeAttr) {
		        if (name.equals("view")) {
		            name = attrs.getAttributeValue(null, "class");
		        }
		
		...
	
	  				if (name.equals(TAG_1995)) {
	            // Let's party like it's 1995!
	            return new BlinkLayout(context, attrs);
	        }
	
		...

	try {
            View view;
            if (mFactory2 != null) {
                view = mFactory2.onCreateView(parent, name, context, attrs);
            } else if (mFactory != null) {
                view = mFactory.onCreateView(name, context, attrs);
            } else {
                view = null;
            }

            if (view == null && mPrivateFactory != null) {
                view = mPrivateFactory.onCreateView(parent, name, context, attrs);
            }

            if (view == null) {
                final Object lastContext = mConstructorArgs[0];
                mConstructorArgs[0] = context;
                try {
                    if (-1 == name.indexOf('.')) {
                        view = onCreateView(parent, name, attrs);
                    } else {
                        view = createView(name, null, attrs);
                    }
                } finally {
                    mConstructorArgs[0] = lastContext;
                }
            }

            return view;
        } 

	...

	}

如果view为属性标签，那么name就是属性的值。<br/>
如果TAG_1995，做出来的布局会一闪一闪的。<br/>
如果该LayoutInflater被复制过，比如用于做换肤之类的功能，那么通过调用复制后的LayoutInflater的Factory定义的onCreateView——主要是看其定义的view的属性，最后还是调用的createView——生成view<br/>
如果是普通的view，标签名里如果含有“.”，那么说明是自定义view，不需要往createView中添加name的前缀，直接name就带有包名了；否则是系统的view，需要往createView中添加name的前缀，比如`android.widget`。<br/>

##### createView #####

    public final View createView(String name, String prefix, AttributeSet attrs)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor != null && !verifyClassLoader(constructor)) {
            constructor = null;
            sConstructorMap.remove(name);
        }
        Class<? extends View> clazz = null;

        try {
            Trace.traceBegin(Trace.TRACE_TAG_VIEW, name);

            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                clazz = mContext.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);
                
                if (mFilter != null && clazz != null) {
                    boolean allowed = mFilter.onLoadClass(clazz);
                    if (!allowed) {
                        failNotAllowed(name, prefix, attrs);
                    }
                }
                constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name, constructor);
            } else {
                // If we have a filter, apply it to cached constructor
                if (mFilter != null) {
                    // Have we seen this name before?
                    Boolean allowedState = mFilterMap.get(name);
                    if (allowedState == null) {
                        // New class -- remember whether it is allowed
                        clazz = mContext.getClassLoader().loadClass(
                                prefix != null ? (prefix + name) : name).asSubclass(View.class);
                        
                        boolean allowed = clazz != null && mFilter.onLoadClass(clazz);
                        mFilterMap.put(name, allowed);
                        if (!allowed) {
                            failNotAllowed(name, prefix, attrs);
                        }
                    } else if (allowedState.equals(Boolean.FALSE)) {
                        failNotAllowed(name, prefix, attrs);
                    }
                }
            }

            Object[] args = mConstructorArgs;
            args[1] = attrs;

            final View view = constructor.newInstance(args);
            if (view instanceof ViewStub) {
                // Use the same context when inflating ViewStub later.
                final ViewStub viewStub = (ViewStub) view;
                viewStub.setLayoutInflater(cloneInContext((Context) args[0]));
            }
            return view;

        } 
    }

- 根据view的名字，缓存的构造器map中是否存在；
- 如果不存在或者存在但没通过验证，就重新生成并缓存，生成时如果name有前缀就拼装上，通过反射获取其构造函数；
- 通过其构造生成view。








 

 
 