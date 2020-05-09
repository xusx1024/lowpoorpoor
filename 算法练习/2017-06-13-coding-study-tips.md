---
title: 字符编码学习笔记
date: 2017-06-13
tags: javaEE
categories: JAVA & kotlin
---

#### 常见字符编码 ####
- ISO-8859-1 不支持中文
- gbk、gb2312 国标 
- utf-8 万国码

#### 响应编码 ####
- 当使用response.getWriter()来向客户端发送字符数据时，如果之前没有设置编码，默认使用ISO，不支持中文
- 一般浏览器会把数据当做gbk
- 服务器设置Content-Type响应头，告诉浏览器编码格式 `text/html;charset=utf-8`，同时也设置了响应编码，一句顶两句
- 使用writer之前，先setContentType

#### 请求编码 ####

- 上次请求返回的Content-Type类型决定表单的编码类型
- servlet默认使用ISO
	- POST：在获取参数之前调用request的setCharacterEncoding
    - GET： `apache-tomcat-9.0.0.M21/conf/server.xml` 的Connector标签下，设置URIEncoding="UTF-8" 但是`禁止使用`，这是Tomcat的全局设置
    - GET：先获取GET参数，得到ISO的编码，用ISO反编回来，然后用UTF-8编码
- Tomcat8 之后默认UTF-8了

#### URL编码 ####

- 不是字符编码，是用来在客户端与服务端之间传递参数用的一种方式
- 服务器可自动识别URL编码
- URLEncoder.encode



