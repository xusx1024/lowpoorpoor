[TOC]
# LeetCode 321 Create Maximum Number

## 题目
Given two arrays of length m and n with digits 0-9 representing two numbers. Create the maximum number of length k <= m + n from digits of the two. The relative order of the digits from the same array must be preserved. Return an array of the k digits.

Note: You should try to optimize your time and space complexity.

给定2个数组来描述两个数，长度分别是m和n，元素是0-9。从这两个数组中重新造一个最大数，要求长度看k <= m + n。要求数字在原理数组中的序不能改变。要求返回一个有k个元素的数组。

注意：要优化时间，空间的复杂度。

### Example 1:

Input:
nums1 = [3, 4, 6, 5]
nums2 = [9, 1, 2, 5, 8, 3]
k = 5
Output:
[9, 8, 6, 5, 3]

### Example 2:

Input:
nums1 = [6, 7]
nums2 = [6, 0, 4]
k = 5
Output:
[6, 7, 6, 0, 4]


### Example 3:

Input:
nums1 = [3, 9]
nums2 = [8, 9]
k = 3
Output:
[9, 8, 9]

## 思路

*  需要从两个数组取K个，有序的，最大值
*  使用贪心算法，循环从nums1里取出i个，则从nums2里取出k-i个
*  比较取出来的两个数组并合并为结果数组


## 代码
```java
class Solution {
   public int[] maxNumber(int[] nums1, int[] nums2, int k) {
    int n = nums1.length;
    int m = nums2.length;
    int[] ans = new int[k];
    for (int i = Math.max(0, k - m); i <= k && i <= n; ++i) {
      int[] candidate = merge(maxArray(nums1, i), maxArray(nums2, k - i), k);
      if (greater(candidate, 0, ans, 0)) {
        ans = candidate;
      }
    }
   
    return ans;
  }

  private int[] merge(int[] nums1, int[] nums2, int k) {
    int[] ans = new int[k];
    for (int i = 0, j = 0, r = 0; r < k; ++r) {
      ans[r] = greater(nums1, i, nums2, j) ? nums1[i++] : nums2[j++];
    }
       return ans;
  }

  private boolean greater(int[] nums1, int i, int[] nums2, int j) {
    while (i < nums1.length && j < nums2.length && nums1[i] == nums2[j]) {
      i++;
      j++;
    }
    return j == nums2.length || (i < nums1.length && nums1[i] > nums2[j]);
  }

  // 求一个数组的最大子数组
  private int[] maxArray(int[] nums, int k) {
    int n = nums.length;
    int[] ans = new int[k];
    for (int i = 0, j = 0; i < n; ++i) {
      while (n - i + j > k && j > 0 && ans[j - 1] < nums[i]) {
        j--;
      }
      if (j < k) {
        ans[j++] = nums[i];
      }
    }
   
    return ans;
  }
}
```