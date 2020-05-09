## `java.lang.relect.Proxy`

### 概述

`java`动态代理机制的主类，提供了一组静态方法来为一组接口动态地生成代理类及其对象。同时也是所有的，这些方法创造出来的，动态代理类的父类。为接口`Foo`创建代理类的示例：

```java
InvocationHandler handler = new MyInvocationHandler(...);
Class<?> proxyClass = Proxy.getProxyClass(Foo.class.getCloassLoader(),Foo.class);
Foo f = (Foo)proxyClass.getConstructor(InvocationHandler.class).newInstance(handler); 
```

另一种更简单的方式：

```java
Foo f = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(), new Class<?>[] {Foo.class}, handler);
```



一个动态代理类，就是类已经创建了，在运行时又实现了一列方法的类。

代理接口：代理类实现的接口。

代理实例：代理类的实例。



每个代理实例都关联着一个调用处理器对象，这个调用处理器对象实现了`InvocationHandler`接口。

代理实例上的方法调用通过它的一个代理接口，会被分发到代理实例相关联的`InvocationHandler`实例的`invoke`上，传入参数：代理实例，反射方法对象，方法参数数组。调用处理器，恰当的处理经编码的方法调用，并且它返回的结果将会作为代理实例上的方法调用的返回结果。

#### 代理类的属性

1. 如果代理接口是`pulbic`，代理类就是`public`, `final`, 非`abstract`
2. 如果代理接口是`non-public`，代理类就是`non-public`, `final`, 非`abstract`
3. 代理类的非限定名称是未指定的。类名空间以`$Proxy`开始，应该这样的，但是，(这)是为代理类保留的(设计成这样的)
4. 代理类继承自`java.lang.reflect.Proxy`
5. 代理类在创建时，按照参数传入的接口的顺序实现了这些指定的接口
6. 假如一个代理类实现了`non-public`的接口，代理类就会被定义在和接口同一个包里面。否则代理类的包是未指定的。注意，包封闭不会阻止代理类在运行是被成功定义到指定的包中，也不会阻止已经被同一个类加载器和相同指定签名的包定义的类
7. 由于代理类在创建时实现了所有指定的接口，调用`getInterfaces`会返回相同顺序的接口列表，调用`getMethods`会返回包含接口方法在内的所有方法列表，调用`getMethod`会根据参数返回期望的方法
8. 在`isProxyClass`方法中，如果传入的类是`Proxy.getProxyClass`返回的，或者是`Proxy.newInstance`返回的，就返回`true`，否则返回`false`
9. 代理类的`java.security.ProtectionDomain`和引导类加载器加载的系统类是一样的，比如`java.lang.Object`，因为代理类的代码是被受信的系统代码生成的。保护域通常被授予`java.scurity.AllPermission`
10. 每个代理类都有一个拥有一个参数的公共构造方法，这个参数实现了`InvocationHandler`接口，参数用来为代理实例设置调用处理器。不同意使用反射`API`访问公共构造器，代理类实例可以通过调用`Proxy.newProxyInstance`创建，该方法联结到方法`Proxy.getProxyClass`，这里传递调用处理器调用了构造方法。

#### 代理类实例的属性

1. 代理类实例`proxy`实现了接口`Foo`, `proxy instanceof Foo` 返回`true`, `(Foo)proxy`不会返回类型转换异常
2. 每个代理类实例关联的调用处理器通过构造方法传入。静态方法`getInvocationHandler`方法会返回关联着的调用处理器，此时把代理类实例作为参数传入
3. 代理实例上的接口方法会被编码和调度到调用处理器的`invoke`方法上，如该方法的文档所述
4. `java.lang.Object`的`hashCode`, `equals`, `toString`方法在代理实例上的调用会和上面描述的一样，被编码和调度到调用处理器的`invoke`方法。传入`invoke`的第一个参数，声明方法的类，会是`Object`. 代理实例从`java.lang.Object`继承的别的公开方法不会被代理类重写，所以(代理类实例)调用那些方法和`Ojbect`实例调用是一样的结果。

#### 多代理接口中的重复方法

当两个或多个代理类接口包含了相同名字和参数签名的方法，代理实现的接口的顺序就非常重要了。当一个这样重复的方法被代理实例调用，传入到调用处理器的方法对象，不一定是那些通过代理方法调用的，从接口的引用类型分配的，声明类的方法之一。这个限制存在的原因是生成的代理类中响应的方法实现不能决定通过哪个接口调用的。然而，在代理类实例上调用一个重复方法，在代理类接口列表最前面的方法对象会传入调用处理器的`invoke`方法，不考虑方法调用发生时的引用类型。【一句话：谁在前面就调用谁的，看传入的接口的顺序】

如果方法是和`java.lang.Object`中的`hashCode`, `equals`, `toString`重名了，传入调用处理器的`invoke`方法中的是`java.lang.Object`.

也要注意当一个重复方法被调度到调用处理器时，`invoke`只能抛出代理类型实现的接口声明中的那些。如果`invoke`抛出了别的异常，`UndeclaredThrowableExeption`会被抛出。这个限制意味着，不是所有`getExceptionType`返回的异常类型可以被`invoke`抛出。【一句话：接口中的方法有啥异常，代理类才能抛啥异常】

实际开发中，就是不要写重复方法。

### 关键`API`

#### `getInvocationHandler`

获取指定代理对象所关联的调用处理器

```java
@CallerSensitive
public static InvocationHandler getInvocationHandler(Object proxy) {
    /**
    * 验证传入的参数是不是一个`Proxy.class`实例
    */
    if (!isProxyClass(proxy.getClass())) {
        throw new IllegalArgumentException("not a proxy instance");
    }
    
    final Proxy p = (Proxy) proxy;
    final InvocationHandler ih = p.h;
    // Android移除的： 安全管理，访问检查
    /*
    if (System.getSecurityManager() != null) {
    	Class<?> ihClass = ih.getClass();
    	Class<?> caller = Reflection.getCallerClass();
        if (ReflectUtil.needsPackageAccessCheck(caller.getClassLoader(), ihClass.getClassLoader())) {
        ReflectUtil.checkPackageAccess(ihClass);
        }
    }    
    */
    
    return ih;
}
```

#### `getProxyClass`

 获取关联于指定类装载器和一组接口的动态代理类的对象

```java
/**
* 
* 根据指定类加载器和接口数组，返回代理类的类对象。
* 代理类将被给定的类加载器定义并且将会实现所有给定的接口。
* 假如任何给定的接口是非公开的，代理类就是非公开的。
* 假如相同接口排列的代理类已经被同一个类加载器定义，那将返回这个已存在的代理类，反之则动态生成。
*
* 有几个参数上的限制：
* 1. 接口数组中的元素必须都是接口类型，不能是类或基本类型；
* 2. 接口数组中的任何两个元素都不可引用相同的类对象；
* 3. 接口数组中的所有类型必须通过名字对类加载器是可见的，换言之，下面的表达式必须为true：
*	Class.forName(i.getName(), false, cl) == i;
* 4. 所有非公开的接口，必须在同一个包中，否则，代理类可能无法实现所有的接口，不管接口在哪里；
* 5. 任何指定接口的成员方法的集合都有相同的方法签名；
* 6. 假如任意一个方法返回类型是基本类型或void，则所有的方法必须有相同的返回类型；
* 7. 或者，一个方法的返回类型必须是可以分配到剩余其他方法。
* 8. 返回的代理类对象禁止超出任何虚拟机的限制。比如，虚拟机限制类实现接口的上限是65535，那传入的接口数组就禁止超过65535
* 违反任意上面的限制，本方法就会抛出非法参数异常。如果参数传入null，则抛出空指针异常。
* 
* 注意：接口数组的顺序很重要：两次调用本方法，参数一样，但是顺序不同，就会返回两个不同的代理类。
*
*/
@CallerSensitive
public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces)throws IllegalArgumentException {
    // Android 修改的地方：去除了安全管理，权限检查
    /*
    final Class<?>[] intfs = interfaes.clone();
    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
        checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
    }
    
    return getProxyClass0(loader,intfs);
    */
    return getProxyClass0(loader,interfaces);
}
```



```java
/**
* a cache of proxy classes
*/
private static final WeakCache<ClassLoader, Class<?>[], Class<?>>
	proxyClassCache = new WeakCache<>(new KeyFactory(), new ProxyClassFactory());
```



```java
/**
* 如果缓存中存在，返回缓存中的副本，否则通过`ProxyClassFactory`创建一个代理类。
*/
private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        return proxyClassCache.get(loader, interfaces);
    }
```

#### `isProxyClass`

判断给定的类对象是否是一个动态代理类

```java
/**
* 假如指定的类是使用`getProxyClass`或者`newProxyInstance` 动态生成的代理类，那么返回true。
*/
public static boolean isProxyClass(Class<?> cl) {
    return Proxy.class.isAssignableFrom(cl) && proxyClassCache.containsValue(cl);
}
```

#### `newProxyInstance`

指定类加载器，一组接口，调用处理器，生成动态代理类实例。

```java
/** parameter types of a proxy class constructor */
private static final Class<?>[] constructorParams = { InvocationHandler.class };

@CallerSensitive
public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) throws IllegalArgumentException {
    // 检查指定的对象引用是非空的。这个方法被设计用来验证方法或构造中验证参数的有效性。
    Objects.requireNonNull(h);
    
    final Class<?>[] intfs = interfaces.clone();
    Class<?> cl = getProxyClass0(loader, intfs);
    try {
        final Constructor<?> cons = cl.getConstructor(constructorParams);
        final InvocationHandler ih = h;
        if (!Modifier.isPublic(cl.getModifiers())) {
            cons.setAccessible(true);
        }
        return cons.newInstance(new Object[]{h});
    } catch (IllegalAccessException|InstantiationException e) {
        throw new InternalError(e.toString(), e);
	} catch (InvocationTargetException e) {
        Throwable t = e.getCause();
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new InternalError(t.toString(), t);
        }
	} catch (NoSuchMethodException e) {
        throw new InternalError(e.toString(), e);
    }
}
```

#### `ProxyClassFactory`

`java.lang.reflect.Proxy`的内部类，根据给出的类加载器和接口数组，生成定义和返回代理类实例。

```java
private static final class ProxyClassFactory
        implements BiFunction<ClassLoader, Class<?>[], Class<?>>
    {
        // prefix for all proxy class names
        // 所有代理名字的前缀
        private static final String proxyClassNamePrefix = "$Proxy";

        // next number to use for generation of unique proxy class names
        // 用于生成独特的代理类名字的数字，结合上面的，新的代理类名字：$ProxyN
        private static final AtomicLong nextUniqueNumber = new AtomicLong();

        @Override
        public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {

            Map<Class<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);
            for (Class<?> intf : interfaces) {
                /*
                 * Verify that the class loader resolves the name of this
                 * interface to the same Class object.
                 * 验证类加载解析的接口名字是不是同一个类加载器。
                 */
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = Class.forName(intf.getName(), false, loader);
                } catch (ClassNotFoundException e) {
                }
                if (interfaceClass != intf) {
                    throw new IllegalArgumentException(
                        intf + " is not visible from class loader");
                }
                /*
                 * Verify that the Class object actually represents an
                 * interface.是不是接口类型
                 */
                if (!interfaceClass.isInterface()) {
                    throw new IllegalArgumentException(
                        interfaceClass.getName() + " is not an interface");
                }
                /*
                 * Verify that this interface is not a duplicate.接口有没有重复。
                 */
                if (interfaceSet.put(interfaceClass, Boolean.TRUE) != null) {
                    throw new IllegalArgumentException(
                        "repeated interface: " + interfaceClass.getName());
                }
            }

			// 代理类的包名
            String proxyPkg = null;     // package to define proxy class in
            int accessFlags = Modifier.PUBLIC | Modifier.FINAL;

            /*
             * Record the package of a non-public proxy interface so that the
             * proxy class will be defined in the same package.  Verify that
             * all non-public proxy interfaces are in the same package.
             * 记录那些non-public的代理接口，可以把代理类定义到同一个包下。
             */
            for (Class<?> intf : interfaces) {
                int flags = intf.getModifiers();
                if (!Modifier.isPublic(flags)) {
                    accessFlags = Modifier.FINAL;
                    String name = intf.getName();
                    int n = name.lastIndexOf('.');
                    String pkg = ((n == -1) ? "" : name.substring(0, n + 1));
                    if (proxyPkg == null) {
                        proxyPkg = pkg;
                    } else if (!pkg.equals(proxyPkg)) {
                        throw new IllegalArgumentException(
                            "non-public interfaces from different packages");
                    }
                }
            }

            if (proxyPkg == null) {
                // if no non-public proxy interfaces, use the default package.
                proxyPkg = "";
            }

            {
                // Android-changed: Generate the proxy directly instead of calling
                // through to ProxyGenerator. Android修改了JDK中生成代理类的部分逻辑。
                List<Method> methods = getMethods(interfaces);
                Collections.sort(methods, ORDER_BY_SIGNATURE_AND_SUBTYPE);
                validateReturnTypes(methods);
                List<Class<?>[]> exceptions = deduplicateAndGetExceptions(methods);

                Method[] methodsArray = methods.toArray(new Method[methods.size()]);
                Class<?>[][] exceptionsArray = exceptions.toArray(new Class<?>[exceptions.size()][]);

                /*
                 * Choose a name for the proxy class to generate.
                 */
                long num = nextUniqueNumber.getAndIncrement();
                String proxyName = proxyPkg + proxyClassNamePrefix + num;

                return generateProxy(proxyName, interfaces, loader, methodsArray,
                                     exceptionsArray);
            }
        }
    }
```



## `InvocationHandler`

调用处理器接口，有一个`invoke`方法，用于集中处理在动态代理类对象上的方法调用，通常在该方法中实现对委托类的代理访问。

### `invoke`

```java
public Object invoke(Object proxy, Method method, Object[] args)
   throws Throwable;
```

## 使用

```java
/**
* 假设要代理的类为：Person
* 要hook的方法为：doSomething
* 自定义调用处理器为：InvocationHandlerImpl
*/

Person p = new Person();
p.doSomething();

// 1. InvocationHandler,参数传要被代理的类
InvocationHandler h = new InvocationHandlerImpl(p);
  
// 2. ClassLoader
ClassLoader cl = p.class.getClassLoader();

// 3. Interfaces
Class<?>[] intfs = p.getClass().getInterfaces();

// 4. ProxyClass

Class<?> proxyClass = Proxy.getProxyClass(cl, intfs);

// 5. Constructor
Constructor c = proxyClass.getConstructor(InvocationHandler.class);

// 6. 调用
p = (Person)c.newInstance(h);

// 和定义出对比看代理是否成功
p.doSomething();
```



上面是严格按照`api`来的，需要什么参数，就组织什么参数。然后设计者还提供了一种简单形式：

```java
Person p = (Person) Proxy.newProxyInstance(Person.class.getClassLoader, p.getClass.getInterfaces(), new InvocationHandler(p));
p.doSomething();
```

实际开发中就用这个。

### `.class` 和 `.getClass()`的区别

使用的时候区别：`Person.class`和`new person().getClass()`.

同一个类的时候，这二者是相同的。

考虑：`AInterface `和`AInterfaceImpl`在获取`class`的时候是不同的。

不同的原因是：`.class`是编译时确定的，`.getClass()`是运行时确定的。