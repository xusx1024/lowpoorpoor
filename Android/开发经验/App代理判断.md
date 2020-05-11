## App代理判断

```
private boolean isWifiProxy(Context context) {
    String host;
    String port;
    if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
      host = System.getProperty("http.proxyHost");
      port = System.getProperty("http.proxyPort");
    } else {
      host = Proxy.getHost(context);
      port = String.valueOf(Proxy.getPort(context));
    }
    Log.e("-->", host + ":" + port);
    return !TextUtils.isEmpty(host) && !TextUtils.isEmpty(port);
  }
```

OKHttp设置不使用代理:

``` 
OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
okHttpClient.proxy(Proxy.NO_PROXY);
```

看下`java.net.Proxy`的源码:


代理类型:

``` 
    /**
     * Represents the proxy type.
     *
     * @since 1.5
     */
    public enum Type {
        /**
         * Represents a direct connection, or the absence of a proxy.
         */
        DIRECT,
        /**
         * Represents proxy for high level protocols such as HTTP or FTP.
         */
        HTTP,
        /**
         * Represents a SOCKS (V4 or V5) proxy.
         */
        SOCKS
    };
```

不使用代理(直连):

``` 
 /**
     * A proxy setting that represents a {@code DIRECT} connection,
     * basically telling the protocol handler not to use any proxying.
     * Used, for instance, to create sockets bypassing any other global
     * proxy settings (like SOCKS):
     * <P>
     * {@code Socket s = new Socket(Proxy.NO_PROXY);}
     *
     */
    public final static Proxy NO_PROXY = new Proxy();
```

> 代表着直连的代理设置，  基本上告诉协议处理程序不要使用任何代理。  例如，用于创建绕过任何其他全局的套接字 代理设置（如SOCKS）:`Socket s = new Socket(Proxy.NO_PROXY);`