---
layout: post
title:  如何仅用递归函数和栈操作逆序一个栈
date:   2017-02-11
categories: Algorithm
tag: 算法
---
 

#### 题目 ####

	一个栈中一次压入1，2，3，那么从栈顶到栈底依次为3，2，1.将这个栈逆序，从栈顶到栈底依次为1，2，3.但是只能用递归函数来实现，不能用其他数据结构。

#### 难度 ####

	尉 ★★☆☆


#### 解答 ####
 
	需要设计两个递归函数。
	1，将stack的栈底元素返回并移除；
	2，将stack逆序；

 

#### 代码 ####
		
	import java.util.Stack;

	public class ReverseStack {

	private static int getAndRemoveLastElement(Stack<Integer> stack) {
		int result = stack.pop();
		if (stack.empty()) {
			return result;
		} else {
			int last = getAndRemoveLastElement(stack);
			stack.push(result);//此处将除栈底外的元素重新压入
			return last;
		}
	}

	public static void reverse(Stack<Integer> stack) {
		if (stack.empty()) {
			return;
		} else {
			int i = getAndRemoveLastElement(stack);
			reverse(stack);
			stack.push(i);
		}
	}
}





















