[TOC]

# LeetCode-502 IPO

## 题目
Suppose LeetCode will start its IPO soon. In order to sell a good price of its shares to Venture Capital, LeetCode would like to work on some projects to increase its capital before the IPO. Since it has limited resources, it can only finish at most k distinct projects before the IPO. Help LeetCode design the best way to maximize its total capital after finishing at most k distinct projects.

You are given several projects. For each project i, it has a pure profit Pi and a minimum capital of Ci is needed to start the corresponding project. Initially, you have W capital. When you finish a project, you will obtain its pure profit and the profit will be added to your total capital.

To sum up, pick a list of at most k distinct projects from given projects to maximize your final capital, and output your final maximized capital.

---
假设LeetCode马上要IPO了。为了向风险投资机构卖个好的股票价格，LeetCode将在IPO之前运作一些项目来增值。鉴于资源有限，IPO之前只能完成最多`K`个项目。帮帮忙，在K个项目结束之后，获取最大收益。(这里简直在直接说：需要用贪心来求)

你现在手握几个项目。每个项目`i`,具有纯收益`Pi`，最小启动资本`Ci`。最初，只有`W`资本。完成一个项目，项目收益会转变为资本。

从给定的项目中，获取最大资本，并输出这个最终的最大的资本值。

### Example 1:

Input: k=2, W=0, Profits=[1,2,3], Capital=[0,1,1].

Output: 4

Explanation: Since your initial capital is 0, you can only start the project indexed 0.
             After finishing it you will obtain profit 1 and your capital becomes 1.
             With capital 1, you can either start the project indexed 1 or the project indexed 2.
             Since you can choose at most 2 projects, you need to finish the project indexed 2 to get the maximum capital.
             Therefore, output the final maximized capital, which is 0 + 1 + 3 = 4.
             
由于初始资本为0，只能从第0个项目开始。结束后，收益为1，资本也是1.
手握1个资本，现在既可以启动项目1，也可以启动项目2
由于最多可以选K=2个项目，只好选项目2获取最大资本
因此，输入结果： 0 + 1 + 3 = 4

### Note:

- You may assume all numbers in the input are non-negative integers.
- The length of Profits array and Capital array will not exceed 50,000.
- The answer is guaranteed to fit in a 32-bit signed integer.


- 可以假定所有的数字都是非负数
- 收益和资本数组不会超过50000
- 结果是可以int表示的

## 思路
The idea is each time we find a project with max profit and within current capital capability.
Algorithm:

Create (capital, profit) pairs and put them into PriorityQueue pqCap. This PriorityQueue sort by capital increasingly.
Keep polling pairs from pqCap until the project out of current capital capability. Put them into
PriorityQueue pqPro which sort by profit decreasingly.
Poll one from pqPro, it's guaranteed to be the project with max profit and within current capital capability. Add the profit to capital W.
Repeat step 2 and 3 till finish k steps or no suitable project (pqPro.isEmpty()).
Time Complexity: For worst case, each project will be inserted and polled from both PriorityQueues once, so the overall runtime complexity should be O(NlgN), N is number of projects.

每次使用当前资产能力获取最大收益。

算法：
1. 创建(capital,profit)对，放入`PriorityQueue`pqCap中，以资产递增排序。
2. 持续从pqCap中取出数据对，直到项目耗费完了资产。把这些数据对放入`PriorityQueue`pqPro中，以收益递减排序
3. 从pqPro中取出一个数据，他保证了最大的收益，最小的资产耗费。把收益累加到资产W中。
4. 重复步骤2，3，直到K步或者没有合适的项目可用

复杂度：
最坏情况，每个项目都会从两个队列里插入和取出一次，所以时间复杂度是：O(NlgN),N是项目的数量。

## 解法
```java
class Solution{
    public int findMaximizedCapital(int k, int W, int[] Profits, int[] Capital){
    	int size = Profits.length;
        PriorityQueue<int[]> pqCap = new PriorityQueue<>(size,(a,b) -> (a[0] - b[0]));
        PriorityQueue<int[]> pqPro = new PriorityQueue<>(size,(a,b) ->(b[1] - a[1]));
        
        for(int i = 0; i < size; i++){
            pqCap.add(new int[]{Capital[i],Profits[i]});
        }
        
        for(int i = 0; i < k; i++){
            while(!pqCap.isEmpty() && pqCap.peek()[0] <= W){
                pqPro.add(pqCap.poll());
            }
            if(!pqPro.isEmpty()) break;
            W += pqPro.poll()[1];
        }
        return W;
    }
}
```
