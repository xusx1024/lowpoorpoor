---
layout: post
title:  构造数组的MaxTree
date:   2017-02-17
categories: Algorithm
tag: 算法
---
 

#### 题目 ####

##### 构造数组的MaxTree #####

定义二叉树结点如下：

	public class Node {
		public int value;
		public Node left;
		public Node right;
	
		public Node(int data) {
			this.value = data;
		}
	}

一个数组的MaxTree定义如下。

- 数组必须没有重复元素。
- MaxTree是一颗二叉树，数组的每一个值对应一个二叉树节点。
- 包括MaxTree树在内且在其中的每一颗树上，值最大的节点都是树的头。

给定一个没有重复元素的数组arr，写出生成这个数组的MaxTree的函数，要求如果数组长度为N，则时间复杂度为O(n)、额外空间复杂度为O(n).

#### 难度 ####

	校 ★★★☆

#### 解答 ####

下面举例说明如何在满足空间和时间复杂度的要求下生成MaxTree。

> arr = {3,4,5,1,2}<br/>
> 3的左边第一个比3大的数：无  3的右边第一个比3大的数：4<br/>
> 4的左边第一个比4大的数：无  4的右边第一个比4大的数：5<br/>
> 5的左边第一个比5大的数：无  5的右边第一个比5大的数：无<br/>
> 1的左边第一个比1大的数：5   1的右边第一个比1大的数：2<br/>
> 2的左边第一个比2大的数：5   2的右边第一个比2大的数：无<br/>

以下列原则来建立这颗树：

 - 每一个数的父节点是他左边第一个比他大的数和他右边第一个比他大的数中，较小的那个。
 - 如果一个数左边没有比他大的数，右边也没有。也就是说，这个数是整个数组的最大值，那么这个数是MaxTree的头节点。

示例代码如下：
		
		import java.util.HashMap;
		import java.util.Stack;
		
		public class MaxTree {
		
			public Node getMaxTree(int[] arr) {
		
				Node[] nArr = new Node[arr.length];
				for (int i = 0; i != arr.length; i++) {
					nArr[i] = new Node(arr[i]);
				}
		
				Stack<Node> stack = new Stack<>();
				HashMap<Node, Node> lBigMap = new HashMap<>();
				HashMap<Node, Node> rBigMap = new HashMap<>();
		
				for (int i = 0; i != nArr.length; i++) {
					Node curNode = nArr[i];
					while ((!stack.isEmpty()) && stack.peek().value < curNode.value) {
						popStackSetMap(stack, lBigMap);
					}
					stack.push(curNode);
				}
		
				while (!stack.isEmpty()) {
					popStackSetMap(stack, lBigMap);
				}
		
				for (int i = nArr.length; i != -1; i--) {
					Node curNode = nArr[i];
					while ((!stack.isEmpty()) && stack.peek().value < curNode.value) {
						popStackSetMap(stack, rBigMap);
					}
					stack.push(curNode);
				}
		
				while (!stack.isEmpty()) {
					popStackSetMap(stack, rBigMap);
				}
		
				Node head = null;
				for (int i = 0; i != nArr.length; i++) {
					Node curNode = nArr[i];
					Node left = lBigMap.get(curNode);
					Node right = rBigMap.get(curNode);
		
					if (left == null && right == null) {
						head = curNode;
					} else if (left == null) {
						if (right.left == null) {
							right.left = curNode;
						} else {
							right.right = curNode;
						}
					} else if (right == null) {
						if (left.left == null) {
							left.left = curNode;
						} else {
							left.right = curNode;
						}
					} else {
						Node parent = left.value < right.value ? left : right;
						if (parent.left == null) {
							parent.left = curNode;
						} else {
							parent.right = curNode;
						}
					}
		
				}
		
				return head;
			}
		
			private void popStackSetMap(Stack<Node> stack, HashMap<Node, Node> map) {
				Node popNode = stack.pop();
				if (stack.isEmpty()) {
					map.put(popNode, null);
				} else {
					map.put(popNode, stack.peek());
				}
			}
		}
		