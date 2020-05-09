---
title: HttpRequest学习笔记
date: 2017-06-12 20:13:51 
tags: javaEE
categories: NetWork
---

#### cvc-complex-type.2.4.a: Invalid content was found starting with element 'display-name' ####

`web.xml`在自动生成servlet的时候出现的提示。

通常是由于标签位置，标签DTD的校验规则所影响的。

删除 `http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd` 即不采用该校验规则 
 

#### request 重要方法 ####
- 获取IP
- 获取请求方法
- 获取请求参数
- 获取浏览器类型
- 获取协议
- 获取服务器名
- 获取服务器端口
- 获取项目名
- 获取servlet路径
- 获取GET参数
- 获取URI
- 获取URL
 
#### 使用referer完成防盗链 ####
- 在浏览器地址栏直接输入ip，referer为空
- 从百度输入ip，referer为百度

#### 获取request参数 ####
- 获取get参数
- 获取post参数
- 获取单值
- 获取多值
- 获取map

#### 请求转发和请求包含 ####

- 转发：下一个servlet设置响应体，当前servlet只能设置响应头
- 包含：共同完成响应体
- 无论转发或者包含，都是使用同一个request/response
- 使用set/getAttribute来，在两个servlet之间传递参数【request域、session域、application域中都有这个属性的操作，但是其生命周期不同】

##### 和重定向的区别 ####
- 重定向是两个请求，地址栏地址会改变
- 转发/包含是一个请求
- 转发/包含只能转发到本项目的servl
- 重定向既可以到本项目也可以到别的项目
- 重定向需要给出requestURI，即全路径
- 转发/包含是服务端行为
- 转发/包含效率较高


