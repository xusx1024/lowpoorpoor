---
layout: post
title:  Android电量优化:Battery Historian 使用准备
date:   2018-01-04
categories: Android
tag: android
---

[battery-historian](https://github.com/google/battery-historian) 是google官方提供的,一款优秀的电量分析工具.

在优化电量相关很是有用.

鉴于国内网络条件,和多数开发人员的终端都是win平台,记录自己的研究过程.

好了,废话到此为止.


#### docker安装 ####

- 有人说需要翻墙,其实不需要,千万千万不要打开代理,尤其是全局代理
- 有人说只支持win10和mac,亲测win7可以使用
- 使用[Docker Toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/)

安装如下图:

![install](/images/setup-docker-toolbox.png)
- 比如git,VirtualBox,如果有就不要勾选了
- Docker Compose for windows,kitematic forwindows 最好选用,挺好用的

一路下一步,基本安装成功.

#### 镜像环境 ####

使用battery-historian需要go,Python,Java,git环境,这些在docker里可以获取一个已经配置好的镜像,非常方便.

打开docker quickstart terminal,等待docker初始化成功,并获得一个IP.

注意:

- 不要使用github上的这个命令 `docker -- run -p <port>:9999 gcr.io/android-battery-historian/stable:3.0 --port 9999`
	- 命令陈旧,--run,--port都已经没有了
	- --run 被run替代
	- --port 可以使用 --publish代替
- 请使用:`docker pull bhaavan/battery-historian`,这是下载该img
	- `docker run -d -p 9999:9999 bhaavan/battery-historian --publish 9999` 这是映射端口号

如果一切顺利,到此为止,在浏览器里输入`ip:端口号`应该是可以使用了,如图:
![upload](/images/battery-historian-upload.png)

#### bugreport ####

在导出bugreport文件时候:

- 高于7.0Android系统 `$ adb bugreport bugreport.zip`
- 低于6.0Android系统 `$ adb bugreport > bugreport.txt`

#### Kitematic (Alpha) ####

- 可视化统一管理多个镜像
- 注册登录千万不要用代理
- 感觉比terminal慢了一点,但是好用

#### 友情提示 ####

有的blog上,有使用[historian.py](https://github.com/google/battery-historian/tree/master/scripts)来可视化bugreport文件的,如下:

	python historian.py -a bugreport.txt > battery.html

如果你有Python环境,会优先选择他,看似简单,实际上有些老旧,不建议使用.

理由:
- Python 2.7写的,和现在的3.6语法大有不同,该文件在3.6环境下,print语句都需要添加`()`,有两个库没法导入(注:我就是在此折戟)
- Android7.0及其以上没法用,该文件分析的`.txt`,Android7.0导出的是`zip`
- 安装docker,不止可以使用google的这个分析库,还可以调试源码,模拟别的服务器环境等等,详见:[Docker Hub](https://hub.docker.com/)