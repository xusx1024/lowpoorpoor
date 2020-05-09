[TOC]
# 数组中的第K个最大元素
## 题目 215. Kth Largest Element in an Array
从未排序的数组中找到第K个最大元素。注意，是已排好序的数组中的第K个最大元素，不是第K个元素（考虑到有重复的元素影响排名）。

Example 1:

Input: [3,2,1,5,6,4] and k = 2
Output: 5
Example 2:

Input: [3,2,3,1,2,4,5,5,6] and k = 4
Output: 4

注意：
您可以认定K值总是可用，即1 <= k <= array's length

## 思路
先给数组排序，然后取出第K个位置的值。
主要是排序的方式：
- 冒泡
- 选择
- 快速
- 堆
- 插入
- 归并

也可以选择有序的数据结构：PriorityQueue,Tree
## 代码
``` java
class Solution{
    public int findKthLargest(int[] nums,int k){
        Arrays.sort(nums);// 这是从小到大的序
        return nums[nums.length - k];
    }
}
```


















