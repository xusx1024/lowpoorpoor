---
layout: post
title:  非比较排序之计数排序
date:   2017-04-11
categories: Algorithm
tag: [算法,hotfix]
---
 


#### 简介 ####

[计数排序wiki](https://zh.wikipedia.org/wiki/%E8%AE%A1%E6%95%B0%E6%8E%92%E5%BA%8F) <br>

- 计数排序是用来排序0到100之间的数字的最好的算法
- 计数排序可以用在基数排序中的算法来排序数据范围很大的数组
- 计数排序是一种算法复杂度 O(n) 的排序方法，适合于小范围集合的排序
- 计数排序的优势是对已知数量范围的数组进行排序
- 基于比较的排序算法是不能突破O(NlogN)


 

#### 算法原理 ####
 
1. 找出待排序的数组中最大和最小的元素
1. 统计数组中每个值为i的元素出现的次数，存入数组 C 的第 i 项
1. 对所有的计数累加（从C中的第一个元素开始，每一项和前一项相加）
1. 反向填充目标数组：将每个元素i放在新数组的第C(i)项，每放一个元素就将C(i)减去1


#### 实例分析 ####

具体的分步动画演示详见：[Data Structure Visualizations](http://www.cs.usfca.edu/~galles/visualization/CountingSort.html)

<br>
对于数据2 5 3 0 2 3 0 3程序执行的过程如下图所示：
 
![示例图](/images/counting_sort_1.png)<br>
![示例图](/images/counting_sort_2.png)


#### 代码 ####
	package CountingSort;
	
	/**
	 * 计数排序
	 * 
	 * @author sxx.xu
	 *
	 */
	public class CountingSort {
	 
		private static void countingSort(int[] array) {
			int min = array[0], max = array[0];
			for (int i : array) {
				if (i < min)
					min = i;
				if (i > max)
					max = i;
			}
	
			System.out.println("max value:" + max + "\nmin value:" + min);
			int[] countArray = new int[max + 1];// 此处使用max+1太粗暴，见countingSort2
			int[] countResult = new int[array.length];
	
			for (int i : array) {
				countArray[i]++;
			}
	
			for (int i = 1; i < countArray.length; i++) {
				countArray[i] += countArray[i - 1];
			}
	
			for (int i = array.length - 1; i >= 0; i--) {
				int elem = array[i];
				int index = countArray[elem] - 1;
				countResult[index] = elem;
				System.out.println("当前元素为：" + elem + ";在排序数组中的位置是：" + index);
				countArray[elem]--;
			}
	
			for (int i : countResult) {
				System.out.println(i);
			}
	
			// 桶排序
			// int[][] resultArray = new int[countArray.length][countArray.length];
			// for (int i = 0; i < countArray.length; i++) {
			// for (int j = 0; j < countArray[i]; j++) {
			// resultArray[i][j] = i;
			// }
			// }
			//
			// for (int i = 0; i < resultArray.length; i++) {
			// for (int j = 0; j < resultArray[i].length; j++) {
			// if (resultArray[i][j] != 0)
			// System.out.println(resultArray[i][j]);// 这里直接输出，属于桶排序
			// }
			// }
		}
	
		/**
		 * 优化后的，减少中间数组大小
		 * @param array
		 */
		private static void countingSort2(int[] array) {
	
			int min = array[0], max = array[0];
			for (int i : array) {
				if (i < min)
					min = i;
				if (i > max)
					max = i;
			}
	
			System.out.println("max value:" + max + "\nmin value:" + min);
			int[] countArray = new int[max - min + 1];
			int[] countResult = new int[array.length];
	
			for (int i : array) {
				countArray[i - min]++;
			}
	
			for (int i = 1; i < countArray.length; i++) {
				countArray[i] += countArray[i - 1];
			}
	
			for (int i = array.length - 1; i >= 0; i--) {
				int elem = array[i] - min;
				int index = countArray[array[i] - min] - 1;
				countResult[index] = array[i];
				countArray[elem]--;
			}
	
			for (int i : countResult) {
				System.out.println(i);
			}
		}
	
		public static void main(String[] args) {
			int[] array = { 8, 2, 3, 4, 3, 6, 6, 3, 9 };
			int[] array2 = { 2, 8, 5, 1, 10, 5, 9, 9, 3, 5, 6, 6, 2, 8, 2 };
			countingSort(array);
			countingSort2(array);
		}
	}
