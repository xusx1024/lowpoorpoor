---
layout: post
title:  由两个栈组成的队列
date:   2017-02-11
categories: Algorithm
tag: 算法
---
 

#### 题目 ####

##### 由两个栈组成的队列 #####


	编写一个类，使用两个栈实现队列，支持队列的基本操作(add,poll,peek)

#### 难度 ####

	尉 ★★☆☆


#### 解答 ####
 
	栈的特点是先进后出，而队列的特点是先进先出。我们用两个栈，互相反序，实现类似队列的操作。
	具体实现上是一个栈作为压入栈，在压入数据时只往这个栈中压入，记为stackPush，另一个栈只作为弹出栈，记为stackPop。
	根据栈的特点，循环把stackPush中的数据压入stackPop中，stackPop的栈顶元素即为队列的dequeue元素。

##### 注意事项 #####
		
	1. 如果stackPush要往stackPop中压入数据，必须一次性压入完毕；
	2. 如果stackPop要接收数据，必须保证stackPop为空。

##### stackPush压入stackPop的操作时机 #####
	
	调用add、poll、peek三种方法中任何一种时发生都是可以，只要满足注意事项提到的两点，就不会出错。下面的例子是在调用poll和peek方法时进行压入数据的。

#### 代码 ####
		
	import java.util.Stack;
	
	public class TwoStackQueue {

		public Stack<Integer> stackPush;
		public Stack<Integer> stackPop;
	
		public TwoStackQueue() {
			stackPop = new Stack<>();
			stackPush = new Stack<>();
		}
	
		public void add(int pushInt) {
			stackPush.push(pushInt);
		}
	
		public int poll() {
			if (stackPop.empty() && stackPush.empty()) {
				throw new RuntimeException("Queue is empty!");
			} else if (stackPop.empty()) {
				while (!stackPush.empty()) {
					stackPop.push(stackPush.pop());
				}
			}
			return stackPop.pop();
		}
		
		public int peek(){
			if (stackPop.empty() && stackPush.empty()) {
				throw new RuntimeException("Queue is empty!");
			} else if (stackPop.empty()) {
				while (!stackPush.empty()) {
					stackPop.push(stackPush.pop());
				}
			}
			return stackPop.peek();
		}
	}



























