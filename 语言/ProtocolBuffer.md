## ProtocolBuffer



AndroidStudio版本：AndroidStudio 3.4

AndroidStudio安装插件版本： Protobuf Support  v0.13.0 

### 1. 配置project -> build.gradle

```
 dependencies {
        classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.8'
    }
```

注意：也许会遇到`'debugAndroidTestCompile' not found.`问题，把版本更新到最新即可。截止本文是0.8.8

地址：[com.google.protobuf]([com.google.protobuf](https://plugins.gradle.org/plugin/com.google.protobuf))

### 2. 配置app -> build.gradle

```
apply plugin: 'com.google.protobuf'
```



```
android {
      protobuf {
    //配置protoc编译器
    protoc {
      artifact = 'com.google.protobuf:protoc:3.5.1'
    }
    //这里配置生成目录，编译后会在build的目录下生成对应的java文件
    generateProtoTasks {
      all().each { task ->
        task.builtins {
          remove java
        }
        task.builtins {
          java {}
        }
      }
    }
  }
  sourceSets {
    main {
      java {
        srcDir 'src/main/java'
      }
      proto {
        srcDir 'src/main/proto'
      }
    }
  }
    
}
```

1. 新建`src/main/proto`文件夹，放入自定义的`.pro`文件
2. rebuild
3. 在`\app\build\generated\source\proto`路径下找到生成的文件

#### 编码性能对比 (S)

| Person个数 | Protobuf | JSON   |
| :--------- | :------- | :----- |
| 10         | 4.687    | 6.558  |
| 50         | 23.728   | 41.315 |
| 100        | 45.604   | 81.667 |

#### 解码性能对比 (S)

| Person个数 | Protobuf | JSON   |
| :--------- | :------- | :----- |
| 10         | 0.226    | 8.839  |
| 50         | 0.291    | 43.869 |
| 100        | 0.220    | 85.444 |