[TOC]

# 02 LeetCode 455 Assign Cookies
## 题意
```
Assume you are an awesome parent and want to give your children some cookies. But, you should give each child at most one cookie. Each child i has a greed factor gi, which is the minimum size of a cookie that the child will be content with; and each cookie j has a size sj. If sj >= gi, we can assign the cookie j to the child i, and the child i will be content. Your goal is to maximize the number of your content children and output the maximum number.

Note:
You may assume the greed factor is always positive. 
You cannot assign more than one cookie to one child.

假设你是个不错的家长，要给孩子们分饼干了。
孩子们最多需要一块饼干。
条件：孩子们有满足因子，饼干有满足因子。只有饼干的满足因子大于等于孩子的满足因子，这块饼干分给这个孩子，才能满足要求。
你的目标就是满足尽量多的小孩，输出满足的小孩的数量。

```
**Example 1:**

> Input: [1,2,3], [1,1]

> Output: 1

> Explanation: You have 3 children and 2 cookies. The greed factors of 3 children are 1, 2, 3. 
> And even though you have 2 cookies, since their size is both 1, you could only make the child whose greed factor is 1 content.
> You need to output 1.

> 如题：三个孩子，满足因子分别是1，2，3
>            二块饼干，满足因子分别是1，1.
> 	   因此呢，饼干只能满足一个孩子，就是第一个孩子，返回1。

**Example 2:**

> Input: [1,2], [1,2,3]

> Output: 2

> Explanation: You have 2 children and 3 cookies. The greed factors of 2 children are 1, 2. 
> You have 3 cookies and their sizes are big enough to gratify all of the children, 
> You need to output 2.

> 二个孩子，满足因子分别是1，2
> 三块饼干，满足因子分别是1，2，3
> 因此，分饼干的选择有：(1,2),(1,3)(2,3)(3,2)这么几种情况，我们可以不必考虑这些情况，只要符合满足条件，就可以分配给孩子，结果增加1.这种思想，满足了就可以分配一下，不考虑是不是最佳，这就是greedy，贪婪思想。



##  思路
据题意，为了`尽量的满足更多的孩子`，`拥有大满足因子的饼干，尽量分配给大满足因子的孩子`。这样，可以先对孩子和饼干数组进行`排序`。
指针i,j同时遍历孩子和饼干数组，如果饼干满足孩子，就继续，i++,j++
如果饼干不能满足孩子，因为是有序递增数组(排序后)，那么饼干也不能满足以后的孩子，此时，继续判断下一块饼干(j++)，孩子原地不动。

## 代码


```java
class Solution{
    public int findContentChildren(int[] g, int[] s){
    	int gLen = g.length;
    	int sLen = s.length;
    	if(gLen == 0 || sLen == 0) return 0;
    	
    	int i = 0; 
    	int j = 0; 
    	int result = 0;
    	while(i < gLen && j < sLen){
            if(g[i] <= s[j]){
                result++;
                i++;
                j++;
            }else{
                j++;
            }
    	}
    	return result;
    }
}
```