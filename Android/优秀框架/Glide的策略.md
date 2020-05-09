#  Glide的策略

[TOC]
![](https://cn.bing.com/th?id=OHR.AlbertaThanksgiving_ZH-CN5899007960_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp)
用于学习策略模式. 

重要的是要会应用, 分解所处的场景, 选取合适的设计模式.

### 缓存策略

`Glide` 的磁盘缓存策略,:



```java
/**
 * 用于图片的缓存策略集
 */
public abstract class DiskCacheStrategy {
    /**
     * true: 缓存原始数据. 
     * @param dataSource  
     */ 
    public abstract boolean isDataCacheable(DataSource dataSource);
    /**
     * true: 缓存解码后的资源
     */
    public abstract boolean isResourceCacheable(boolean isFromAlternateCacheKey,
      DataSource dataSource, EncodeStrategy encodeStrategy);
    
  /**
   * true: 需要解码缓存中的资源数据
   */
  public abstract boolean decodeCachedResource();
  /**
   * true: 需要解码缓存中的原数据
   */
  public abstract boolean decodeCachedData();
}
```

`DataSource`

```
/**
 * data 未解码前的数据
 */ 
public enum DataSource {
/**
 * 表明可能从设备本地取得数据, 就算是通过`ContentProvider`获取的.
 * 比如从相册获取, 此时肯定叫做`LOCAL`
 * 比如从三方的`ContentProvider`获取, 而三方从网络上下载的, 此时也定义为`LOCAL`
 */ 
    LOCAL,
    /**
     * 表明获取远程资源而不是设备本地
     */
    REMOTE,
    /**
     * 表明获取本地缓存的原始图片
     */
    DATA_DISK_CACHE,
    /**
     * 表明获取修改过的(适合控件大小的)本地缓存的原始图片
     */
    RESOURCE_DISK,CACHE,
    /**
     * 从内存缓存中获取
     */
    MEMORY_CACHE,
}
```

- `ALL`: 缓存原始数据合解码转换后的资源

```java
  public static final DiskCacheStrategy ALL = new DiskCacheStrategy() {
    @Override
    public boolean isDataCacheable(DataSource dataSource) {
      return dataSource == DataSource.REMOTE;
    }

    @Override
    public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource,
        EncodeStrategy encodeStrategy) {
      return dataSource != DataSource.RESOURCE_DISK_CACHE && dataSource != DataSource.MEMORY_CACHE;
    }

    @Override
    public boolean decodeCachedResource() {
      return true;
    }

    @Override
    public boolean decodeCachedData() {
      return true;
    }
  };
```

- `NONE`: 不缓存

```java
  public static final DiskCacheStrategy NONE = new DiskCacheStrategy() {
    @Override
    public boolean isDataCacheable(DataSource dataSource) {
      return false;
    }

    @Override
    public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource,
        EncodeStrategy encodeStrategy) {
      return false;
    }

    @Override
    public boolean decodeCachedResource() {
      return false;
    }

    @Override
    public boolean decodeCachedData() {
      return false;
    }
  };
```

- `DATA`: 解码前直接写入磁盘缓存

```java
  public static final DiskCacheStrategy DATA = new DiskCacheStrategy() {
    @Override
    public boolean isDataCacheable(DataSource dataSource) {
      return dataSource != DataSource.DATA_DISK_CACHE && dataSource != DataSource.MEMORY_CACHE;
    }

    @Override
    public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource,
        EncodeStrategy encodeStrategy) {
      return false;
    }

    @Override
    public boolean decodeCachedResource() {
      return false;
    }

    @Override
    public boolean decodeCachedData() {
      return true;
    }
  };
```



- `RESOURCE`: 把解码后的资源写入磁盘缓存

```java
  public static final DiskCacheStrategy RESOURCE = new DiskCacheStrategy() {
    @Override
    public boolean isDataCacheable(DataSource dataSource) {
      return false;
    }

    @Override
    public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource,
        EncodeStrategy encodeStrategy) {
      return dataSource != DataSource.RESOURCE_DISK_CACHE && dataSource != DataSource.MEMORY_CACHE;
    }

    @Override
    public boolean decodeCachedResource() {
      return true;
    }

    @Override
    public boolean decodeCachedData() {
      return false;
    }
  };
```

- `AUTOMATIC`: 基于`DataFetcher`,`EncodeStrategy`,`ResourceEncoder`智能选择缓存策略

```java
  public static final DiskCacheStrategy AUTOMATIC = new DiskCacheStrategy() {
    @Override
    public boolean isDataCacheable(DataSource dataSource) {
      return dataSource == DataSource.REMOTE;
    }

    @Override
    public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource,
        EncodeStrategy encodeStrategy) {
      return ((isFromAlternateCacheKey && dataSource == DataSource.DATA_DISK_CACHE)
          || dataSource == DataSource.LOCAL)
          && encodeStrategy == EncodeStrategy.TRANSFORMED;
    }

    @Override
    public boolean decodeCachedResource() {
      return true;
    }

    @Override
    public boolean decodeCachedData() {
      return true;
    }
  };
```

###  编码策略

决定如何编码资源写入缓存.

```java
public enum EncodeStrategy {
  /**
   * Writes the original unmodified data for the resource to disk, not include downsampling or
   * transformations.
   */
  SOURCE,

  /**
   * Writes the decoded, downsampled and transformed data for the resource to disk.
   */
  TRANSFORMED,

  /**
   * Will write no data.
   */
  NONE,
}
```

###  重用策略

```
interface LruPoolStrategy {
  void put(Bitmap bitmap);

  @Nullable
  Bitmap get(int width, int height, Bitmap.Config config);

  @Nullable
  Bitmap removeLast();

  String logBitmap(Bitmap bitmap);

  String logBitmap(int width, int height, Bitmap.Config config);

  int getSize(Bitmap bitmap);
}
```

#### 根据`Android`版本不同,默认重用策略不同.

```
  private static LruPoolStrategy getDefaultStrategy() {
    final LruPoolStrategy strategy;
    // October 2013: Android 4.4, kitkat, 19. 
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      strategy = new SizeConfigStrategy();
    } else {
      strategy = new AttributeStrategy();
    }
    return strategy;
  }
```

####  SizeConfigStrategy

使用`Bitmap.Config`和字节数安全地重用更多的图片变种.提高了缓存池的命中率和应用的性能.

字节数使用`TreeMap`有序.

根据请求的大小, 返回最接近的重用图片.

#### AttributeStrategy

根据请求的大小返回确定匹配的重用图片.

### 图像采样策略

`DownsampleStrategy`

表明图片采样的算法. 和`Android`定义的那些有些不同, 比如都有`FIT_CENTER` `AT_LAST`等. 这个策略只控制输出的缩放值, 不控制这个图片在`xml`里如何使用.

和``

#### `FIT_CENTER`

缩放保持原始纵横比, 以便图像的一个尺寸恰好是请求的尺寸, 而另一个尺寸小于等于请求的尺寸.

如果请求的宽高大于源文件宽高, 该方法会升级. 使用`AT_MOST`,`AT_LEAST`,`CENTER_INSIDE`来避免.

#### `CENTER_OUTSIDE` 

默认值.

缩放保持原始纵横比, 以便图像的一个尺寸恰好是请求的尺寸, 而另一个尺寸大于等于请求的尺寸.

如果请求的宽高大于源文件宽高,该方法会升级. 使用`AT_LEAST`,`AT_MOST`,`CENTER_INSIDE`来避免.

#### `AT_LEAST`

采样, 最小尺寸在请求的尺寸和2倍请求尺寸之间, 对最大尺寸没有限制.

请求尺寸大于原始尺寸也不会升级.

#### `AT_MOST`

采样, 最大尺寸在请求的尺寸的一半和请求尺寸之间, 对最小尺寸没有限制.

请求尺寸大于原始尺寸也不会升级.

#### `CENTER_INSIDE`

如果小于目标控件就返回原始图片, 否则会降级.

保持原始纵横比, 以便图像的一个尺寸恰好是请求的尺寸, 另一个尺寸小于等于请求的尺寸.

#### `NONE`

输出原图



#### 代码值得学习

```
public abstract class DownsampleStrategy { 
// 抽象方法
  public abstract float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth,
      int requestedHeight);
  
  public abstract SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight,
    int requestedWidth, int requestedHeight);
// 策略类实例
  public static final DownsampleStrategy FIT_CENTER = new FitCenter();
  public static final DownsampleStrategy CENTER_OUTSIDE = new CenterOutside();
  public static final DownsampleStrategy AT_LEAST = new AtLeast();
  public static final DownsampleStrategy AT_MOST = new AtMost();
  public static final DownsampleStrategy CENTER_INSIDE = new CenterInside();
  public static final DownsampleStrategy NONE = new None();
  
  // 策略实现类
  private static class FitCenter extends DownsampleStrategy { }
  private static class CenterOutside extends DownsampleStrategy { }
  private static class AtLeast extends DownsampleStrategy { }
  private static class AtMost extends DownsampleStrategy { }
  private static class None extends DownsampleStrategy { }
  private static class CenterInside extends DownsampleStrategy { }
}
```

线程间消息机制 ok

进程间通信

图片

网络

插件化

组件化

jetpack



listener 观察者, 可以有多个 

callback, 只有一个 

Error:svn: E170013: Unable to connect to a repository at URL 'https://svn.shunwang.com/svn/homeLine/mobile/UGames/Android/trunk/shunyoushouka'
svn: E175013: Access to '/svn/homeLine/mobile/UGames/Android/trunk/shunyoushouka' forbidden