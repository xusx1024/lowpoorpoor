---
layout: post
title:  用栈来求解汉诺塔的问题
date:   2017-02-16
categories: Algorithm
tag: 算法
---
 

#### 题目 ####

##### 用栈来求解汉诺塔问题 #####


	汉诺塔问题比较经典，这里修改一下游戏规则：现在限制不能从最左侧的塔直接移动到最右侧，也不能从最右侧直接移动到最左侧，而是必须经过中间。求当塔有N层的时候，打印最优移动过程和最优移动总步数。

	例如，当塔数为两层时，最上层的塔记为1，最下层的塔记为2，则打印：

	Move 1 from left to mid
	Move 1 from mid to right
	Move 2 from left to mid
	Move 1 from right to mid
	Move 1 from mid to left
	Move 2 from mid to right
	Move 1 from left to mid
	Move 1 from mid to right
 

用以下两种方法解决

- 方法一：递归的方法
- 方法二：非递归的方法，用栈来模拟汉诺塔的三个塔


#### 难度 ####

	校 ★★★☆


#### 解答 ####
 
	 


##### 方法一 #####

	/**
	 * 递归的方法： 首先，如果只剩最上层(递归的终止条件)的塔需要移动，则有如下处理： 
	 * 1.如果希望从“左”移到“中”，打印“Move 1 from left to mid” 
	 * 2.如果希望从“中”移到“右”，打印“Move 1 from mid to right” 
	 * 3.如果希望从“右”移到“中”，打印“Move 1 from right to mid” 
	 * 4.如果希望从“中”移到“左”，打印“Move 1 from mind to left”
	 * 5.如果希望从“右”移到“左”，打印“Move 1 from right to mid” 和 “Move 1 from mind to left”
	 * 6.如果希望从“左”移到“右”，打印“Move 1 from left to mid” 和 “Move 1 from mid to right”
	 * 
	 */
	public class HanoiProblem {
	
		public static void main(String[] args) {
			System.out.println(process(7, "left", "mid", "right", "left", "right"));
		}
	
		public static int hanoiProblem1(int num, String left, String mid, String right) {
			if (num < 1)
				return 0;
	
			return process(num, left, mid, right, left, right);
	
		}
	
		public static int process(int num, String left, String mid, String right, String from, String to) {
			if (num == 1) {
				if (from.equals(mid) || to.equals(mid)) {
					System.out.println("Move 1 from " + from + " to " + to);
					return 1;
				} else {
					System.out.println("Move 1 from " + from + " to " + mid);
					System.out.println("Move 1 from " + mid + " to " + to);
					return 1;
				}
			}
			if (from.equals(mid) || to.equals(mid)) {
				String another = (from.equals(left) || to.equals(left)) ? right : left;
				int part1 = process(num, left, mid, right, from, another);
				int part2 = 1;// what's this?
				System.out.println("Move " + num + " from " + from + " to " + to);
				int part3 = process(num - 1, left, mid, right, another, to);
				return part1 + part2 + part3;
			} else {
				int part1 = process(num - 1, left, mid, right, from, to);
				int part2 = 1;
				System.out.println("Move " + num + " from " + from + " to " + mid);
				int part3 = process(num - 1, left, mid, right, to, from);
				int part4 = 1;
				System.out.println("Move " + num + " from " + mid + " to " + to);
				int part5 = process(num - 1, left, mid, right, from, to);
				return part1 + part2 + part3 + part4 + part5;
			}
		}
	}



##### 方法二 #####
	
	import java.util.Stack;
	
	/**
	 * 非递归方法 —— 用栈来模拟整个过程 修改后的汉诺塔问题不能让任何塔从“左”直接移动到“右”，也不能从“右”直接移动到“左”，而是要经过
	 * 中间。也就是说，实际动作只有4个：“左”到“中”、“中”到“左”、“中”到“右”、“右”到“中”。
	 * 
	 * 现在我们把左、中、右三个地点抽象成栈，依次记为LS,MS,RS。最初所有的塔都在LS上。
	 * 那么如上4个动作就可以看作是：某一个栈(from)把栈顶元素弹出，然后压入另一个栈(to)，做为该栈的栈顶。
	 * 
	 * 第一原则：小压大的原则；每个栈的栈顶-栈底元素，从小到大。
	 * 第二原则：相邻不可逆原则；比如L->M发生了，那么下一步一定不可为：M->L这样的操作，没意义。
	 * 
	 * 非递归方法结论： <br/>
	 * 1.第一个动作一定是L->M；<br/>
	 * 2.在走出最少步数过程中的任何时刻，四个动作中只有一个动作不违反小压大和相邻不可逆原则，另外三个动作一定都会违反。
	 * 
	 * @author sxx.xu
	 *
	 */
	public class HanoiProblem2 {
	
		public static void main(String[] args) {
			System.out.println(hanoi(4, "L", "M", "R"));
		}
		public static int hanoi(int num, String left, String mid, String right) {
			Stack<Integer> lS = new Stack<>();
			Stack<Integer> mS = new Stack<>();
			Stack<Integer> rS = new Stack<>();
			lS.push(Integer.MAX_VALUE);
			mS.push(Integer.MAX_VALUE);
			rS.push(Integer.MAX_VALUE);
			for (int i = num; i > 0; i--) {
				lS.push(i);
			}
	
			Action[] record = { Action.No };
			int step = 0;
			while (rS.size() != num + 1) {
				step += fStackToStack(record, Action.MToL, Action.LToM, lS, mS, left, mid);
				step += fStackToStack(record, Action.LToM, Action.MToL, mS, lS, mid, left);
				step += fStackToStack(record, Action.RToM, Action.MToR, mS, rS, mid, right);
				step += fStackToStack(record, Action.MToR, Action.RToM, rS, mS, right, mid);
			}
			return step;
		}
	
		public static int fStackToStack(Action[] record, Action preNoAct, Action nowAct, Stack<Integer> fStack,
				Stack<Integer> tStack, String from, String to) {
			if (record[0] != preNoAct && fStack.peek() < tStack.peek()) {
				tStack.push(fStack.pop());
				System.out.println("Move " + tStack.peek() + " from " + from + " to " + to);
				record[0] = nowAct;
				return 1;
			}
			return 0;
		}
	
	}
	
	enum Action {
		No, LToM, MToL, MToR, RToM
	
	} 







