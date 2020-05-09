---
layout: post
title:  Git使用笔记
date:   2017-04-07
categories: Others
tag: 杂项
---
 

#### git管理github ####

1，需要有Githb账号，然后新建仓库，此时保留SSH，长这样：`git@github.com:xusx1024/codewars.git`

2，pc端安装git工具，`git --info-path`打印git所在路径；创建本地仓库；

3，在本地文件中，右键点击 Git Bash Here，进入git命令行

4，全局配置 <br>
	`git config --global username "此处定义你的名字" `<br>
	`git config --global useremail xxxxxxx@gmail.com`

5，初始化本地仓库：
	`git init `

6，创建readme，并上传：<br>
	`git add readme.txt`<br>
	`git commint -m "upload readme"`<br>

7，生成SSH key，用来和Github连接：
	`ssh-keygen -C '这里填你刚才配置的email地址' -t rsa`然后一路点击enter，最后，得到usr/.ssh中的SSH key

8，把SSH key配置到 GitHub中
	在用户名上，`setting -  SSH and GPG keys`

9，用到 第一步的那个串了<br>
	`git remote add origin git@github.com:xusx1024/codewars.git`<br>
	`git push origin master`<br>
 
	
10，以后如果添加的话：<br>
	`git add sth`<br>
	`git commint -m "文件说明"`<br>
	`git push origin master`<br>

11.`git add -A .`
添加所有文件，注意后面有个点。

12.
`git pull`  抓取远程仓库所有分支更新并合并到本地

13.`git config -l` 查看git的配置信息



#### git 提交空目录 ####

git与svn不同，git只跟踪文件的变化，不跟踪目录，如果想要提交一个空目录，我们可以在该目录添加一个文件，命名为.gitignore，这是一个变通的方法。

#### git 克隆分支 ####

多个分支的仓库，使用git clone的方法，只能把HEAD复制下来，如果有多个分支，可以考虑使用：

`git branch -r`来查看各个分支

`git checkout xxx`来clone指定的分支

#### filename too long ####

文件夹的层级过多，导致在clone的时候，出现该提示，可以考虑使用：

`git config --system core.longpaths true`