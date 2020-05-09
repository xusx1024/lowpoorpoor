---
layout: post
title:  用一个栈实现另一个栈的排序
date:   2017-02-14
categories: Algorithm
tag: 算法
---
 


#### 题目 ####
    
	一个栈中的元素的类型为整型，现在想将该栈从顶到底按从大到小的顺序排序，只许申请一个栈。除此之外，可以申请新的变量，但不能申请额外的数据结构。如何完成排序？

#### 难度 ####

	士 ★☆☆☆


#### 解答 ####
 
	将要排序的栈记为stack，申请的辅助栈记为help。在stack上执行pop操作，弹出的元素记为cur。

- 如果cur小于或等于help的栈顶元素，将cur压入help；
- 如果cur大于help的栈顶元素，则将help元素逐个弹出，并压入stack，直至cur小于或等于栈顶元素，将cur压入help；

示例代码如下：

	
	import java.util.Stack;
	
	public class SortStackByStack {
	
		public static void main(String[] args) {
			Stack<Integer> ss = new Stack<>();
			ss.push(7);
			ss.push(2);
			ss.push(8);
			ss.push(3);
			ss.push(9);
			sortStackByStack(ss);
	
		}
	
		public static void sortStackByStack(Stack<Integer> stack) {
			Stack<Integer> help = new Stack<>();
			while (!stack.isEmpty()) {
				int cur = stack.pop();
				while (!help.isEmpty() && help.peek() < cur) {
					stack.push(help.pop());
				}
				help.push(cur);
			}
			while (!help.isEmpty()) {
				stack.push(help.pop());
			}
		}
	}
