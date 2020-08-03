1. 使用模拟器，开启root
2. jeb打开apk，开始debug

遇到的问题：
1. jeb的debug找不到模拟器进程
    
    使用Android/platform-tools的adb替换模拟器自带的adb
    
2. jeb的debug提示要修改`android:debuggable="true"` 或者 `setprop ro.debuggable 1`

    使用[mprop工具](https://pan.baidu.com/s/1ZfYCq-zHdAq-KUa1BkJ6bg) 修改。[教程](https://www.jianshu.com/p/e540f34cec07)
    
    重启模拟器后使用该工具中bat文件，然后打开对应的app
    
3. jeb的debug提示adb进程被占用

    关闭Android Studio， IDEA， Eclipse等等相关软件。