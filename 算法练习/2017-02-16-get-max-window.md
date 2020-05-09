---
layout: post
title:  生成窗口最大值数组
date:   2017-02-16
categories: Algorithm
tag: 算法
---
 

#### 题目 ####

##### 生成窗口最大值数组 #####

有一个整型数组arr和一个大小为w的窗口从数组的最左边滑倒最右边，窗口每次向右边滑一个位置。
例如，数组[4,3,5,4,3,3,6,7]，窗口大小为3时：

>[4 3 5] 4 3 3 6 7 窗口中最大值为5 <br/>
>4 [3 5 4] 3 3 6 7 窗口中最大值为5 <br/>
>4 3 [5 4 3] 3 6 7 窗口中最大值为5 <br/>
>4 3 5 [4 3 3] 6 7 窗口中最大值为4 <br/>
>4 3 5 4 [3 3 6] 7 窗口中最大值为6 <br/>
>4 3 5 4 3 [3 6 7] 窗口中最大值为7 <br/>
 

>请实现一个函数：
>
- 输入：整型数组arr，窗口大小为w
- 输出：一个长度为n-w+1的数组res，res[i]表示每一种窗口状态下的最大值。

以上例，结果应为：{5,5,5,4,6,7}.

#### 难度 ####

	尉 ★★☆☆


#### 解答 ####
 
	 


##### 方法一:O(N * w)#####

	 public class Demo {
	
		public static void main(String[] args) {
			int[] arr = getMaxWindow(new int[] { 4, 3, 5, 4, 3, 3, 6, 7 }, 3);
			for (int i = 0; i < arr.length; i++) {
				System.out.println(arr[i]);
			}
		}
	
		public static int[] getMaxWindow(int[] arr, int w) {
			int[] result = new int[arr.length - w + 1];
			for (int i = 0; i < arr.length - w + 1; i++) {
				int x = arr[i];
				for (int j = 0; j < w; j++) {
					if (arr[i + j] >= x)
						x = arr[i + j];
				}
				result[i] = x;
			}
			return result;
		}
	}


##### 方法二:O(N)#####

>本题的关键在于利用双端队列来实现窗口最大值的更新。生成双端队列qmax存放数组arr中的下标。
>放入规则：
1. 如果qmax为空，直接把下标i放入；
2. 如果qmax不为空，比较arr[i]和qmax队尾下标对应的数组元素，如果arr[i]比较大，则持续弹出qmax中的元素，直至arr[i]小于qmax下标对应的元素或者qmax符合第一条；

>弹出规则：
如果qmax对头的下标等于i-w，说明当前qmax队头的下标已过期，弹出队头。

>上述过程，每个下标最多进入qmax一次，出qmax一次，所以遍历的过程中进出双端队列的操作时间复杂度是O(n).

>注意，我们的目的数组的大小为：n-w+1，在arr的循环过程中，录入数据进入目的数组时，注意过滤其边界，否则`java.lang.ArrayIndexOutOfBoundsException`.

	public static int[] getMaxWindow2(int[] arr, int w) {
			int[] result = new int[arr.length - w + 1];
			LinkedList<Integer> qmax = new LinkedList<>();
			int index = 0;
			for (int i = 0; i < arr.length; i++) {
	
				while (!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[i]) {
					qmax.pollLast();
				}
				qmax.addLast(i);
				if (qmax.peekFirst() == i - w) {
					qmax.pollFirst();
				}
				if (i >= w - 1)
					result[index++] = arr[qmax.peekFirst()];
	
			}
			return result;
		}

###### 双端队列的实现类 ######


	public class LinkedList<E> extends AbstractSequentialList<E>
	    implements List<E>, Deque<E>, Cloneable, java.io.Serializable

 

	public class ArrayDeque<E> extends AbstractCollection<E>
	                           implements Deque<E>, Cloneable, Serializable

  


[java集合类深入分析之Queue篇](http://shmilyaw-hotmail-com.iteye.com/blog/1700599)


