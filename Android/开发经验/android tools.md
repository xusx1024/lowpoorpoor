在xml文件中，会有`xmlns:tools="http://schemas.android.com/tools"` 命名空间。
作用如下：
1. 针对lint检查
2. 针对布局预览
3. 资源压缩

### Lint检查
> tools:ignore="HardcodedText"
>
> 告诉Lint，不要再检查硬编码，就不会有对应的警告了。

 
>  tools:targetApi="14"
>
> 表明所在标签只在 >= 14的api上生效

> tools:locale="es"
>
> 用于<resource/>标签，指定检查拼写是英语-es

### xml预览

> 所有android:xxx几乎都可以用tools:xxx代替
> tools:text="Hello World!"
>
> 只在预览时有效


> tools:context="com.demo.MainActivity"
>
> 声明该布局默认关联的页面

> tools:itemCount = "12"
>
>用于RecyclerView的预览条目数，默认10

### 资源压缩

> tools:shrinkMode="safe"
>
> 这个比较专用，在raw/keep.xml中使用

```$xslt
<?xml version = "1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
           tools:shrinkMode="safe"
/>

    <!--<editor-fold desc="keep.xml的作用">-->
    <!-- shrink Mode 默认为safe，如果能够确定要排除的资源，使用strict 模式-->

    <!-- 一般的，不使用的资源直接删除即可，没必要在这里列出来。
         特殊的，考虑到多个flavor版本，某些资源代码检查时会被使用，而实际上不会用于特定版本时，
    把所有资源放入通用目录，为不同的版本变种创建不同的keep.xml文件。
    -->
    <!--</editor-fold>-->
```

同时，build.gradle也要开启资源压缩：
```$xslt
android {
    buildTypes {
        release {
            shrinkResources true    //开启资源压缩。minifyEnabled 也必须为true,否则编译不通过
            minifyEnabled true     //开启代码混淆/压缩
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
```

[官方文档：自定义要保留的资源](https://developer.android.google.cn/studio/build/shrink-code?hl=zh_cn#keep-resources)







