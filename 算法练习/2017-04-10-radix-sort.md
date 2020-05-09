---
layout: post
title:  非比较排序之基数排序
date:   2017-04-10
categories: Algorithm
tag: [算法,hotfix]
---


#### 简介 ####

探究Tinker的BSDiff算法的过程中，发现用到了`后缀排序`，就去研究后缀排序，然后发现用到`倍增算法`的思想。而倍增是排序的一种策略，这种策略要求必须对`基数排序`有一定的理解 T.T。<br>
好，言归正传。基数排序是[桶排序](http://xusx1024.com/2017/04/10/bucket-sort/)的一种特例。 

#### 比较和非比较的区别 ####

常见的快速排序、归并排序、堆排序、冒泡排序等属于比较排序。在排序的最终结果里，元素之间的次序依赖于它们之间的比较。每个数都必须和其他数进行比较，才能确定自己的位置。

在 冒泡排序 之类的排序中，问题规模为n，又因为需要比较n次，所以平均时间复杂度为O(n²)。在 归并排序、快速排序 之类的排序中，问题规模通过分治法消减为logN次，所以时间复杂度平均 O(nlogn) 。

比较排序的优势是，适用于各种规模的数据，也不在乎数据的分布，都能进行排序。可以说，比较排序适用于一切需要排序的情况。

计数排序、基数排序、桶排序则属于非比较排序。非比较排序是通过确定每个元素之前，应该有多少个元素来排序。针对数组arr，计算arr[i]之前有多少个元素，则唯一确定了arr[i]在排序后数组中的位置。

非比较排序只要确定每个元素之前的已有的元素个数即可，所有一次遍历即可解决。算法时间复杂度 O(n) 。

非比较排序时间复杂度底，但由于非比较排序需要占用空间来确定唯一位置。所以对数据规模和数据分布有一定的要求。

#### 算法原理 ####
>基数排序是一种非比较型、整数排序算法，其原理是将整数按位数切割成不同的数字，然后按照每个位数分别比较。
<br>

排序过程：<br>
将所有待比较数值(正整数)统一为同样的数位长度，数位较短的数前面补零。然后从最地位开始，依次进行一次排序。<br>

基数排序法会使用到桶，即把要比较的个、十、百、千。。。位的对应的元素分配到0~9个桶中，借以达到排序的作用，在某些时候，基数排序法的效率高于其他的比较性算法。具体的分步动画演示详见：[Data Structure Visualizations](http://www.cs.usfca.edu/~galles/visualization/RadixSort.html)。

<br>
基数排序的方式可以采用LSD（Least significant digital）或MSD（Most significant digital），LSD的排序方式由键值的最右边开始，而MSD则相反，由键值的最左边开始。

#### 效率 ####

基数排序的时间复杂度是 **O(k·n)**，其中**n**是排序元素个数， **k**是数字位数。注意这不是说这个时间复杂度一定优于 **O(n·log(n))**， **k**的大小取决于数字位的选择（比如比特位数），和待排序数据所属数据类型的全集的大小； **k**决定了进行多少轮处理，而 **n**是每轮处理的操作数目。


#### 实例分析 ####

对数组{53, 542, 63, 3, 63, 14, 214, 154, 748, 616}，它的示意图如下：

![示例图](../res/img/radix_sort.png)
Java代码如下：
	
	package RadixSort;
	
	/**
	 * 基数排序
	 * 
	 * @author sxx.xu
	 *
	 */
	public class RadixSort {
	
		private static int getMax(int[] array) {
			int max = array[0];
			for (int i : array) {
				max = i > max ? i : max;
			}
			return max;
		}
	
		public static void radixSort(int[] array) {
			int exp = 1;// 个、十、百、千。。。位
			int max = getMax(array);
	
			for (exp = 1; max / exp > 0; exp *= 10)
				countSort(array, exp);
		}
	
		private static void countSort(int[] array, int exp) {
	
			int[] outputs = new int[array.length];
			int[] buckets = new int[10];// 0-9
	
			//统计array中对应位数的元素的个数
			for (int i : array) {
				buckets[(i / exp) % 10]++;
			}
			//统计array中，小于该位的元素的个数，相当于排序
			for (int i = 1; i < buckets.length; i++) {
				buckets[i] += buckets[i - 1];
			}
			//赋值
			for (int i = array.length - 1; i >= 0; i--) {
				outputs[buckets[(array[i] / exp) % 10] - 1] = array[i];
				buckets[(array[i] / exp) % 10]--;
			}
	
			for (int i = 0; i < outputs.length; i++) {
				array[i] = outputs[i];
			}
			outputs = null;
			buckets = null;
		}
	
		private static void radixSort(int[] array, int d) {
			int n = 1;// 个、十、百、千。。。位
			int k = 0;// 保存每一位排序后的结果用于下一位的排序输入
			int length = array.length;
			int[][] bucket = new int[10][length];// 排序桶用于保存每次排序后的结果，这一位上排序结果相同的数字放在同一个桶里
			int[] order = new int[length];// 用于保存每个桶里有多少个数字
			while (n < d) {
				for (int num : array) // 将数组array里的每个数字放在相应的桶里
				{
					int digit = (num / n) % 10;
					bucket[digit][order[digit]] = num;
					order[digit]++;
				}
				for (int i = 0; i < length; i++)// 将前一个循环生成的桶里的数据覆盖到原数组中用于保存这一位的排序结果
				{
					if (order[i] != 0)// 这个桶里有数据，从上到下遍历这个桶并将数据保存到原数组中
					{
						for (int j = 0; j < order[i]; j++) {
							array[k] = bucket[i][j];
							k++;
						}
					}
					order[i] = 0;// 将桶里计数器置0，用于下一次位排序
				}
				n *= 10;
				k = 0;// 将k置0，用于下一轮保存位排序结果
			}
	
		}
	
		public static void main(String[] args) {
			int array[] = { 53, 3, 542, 748, 14, 214, 154, 63, 616, 70 };
			for (int i : array) {
				System.out.print(i + "  ");
			}
			System.out.println("");
			System.out.println("before===================================after");
			radixSort(array);
			for (int i : array) {
				System.out.print(i + "  ");
			}
			int[] A = new int[] { 53, 3, 542, 748, 14, 214, 154, 63, 616, 70 };
			radixSort(A, 100);
			System.out.println("");
			for (int i : A) {
				System.out.print(i + "  ");
			}
		}
	}
