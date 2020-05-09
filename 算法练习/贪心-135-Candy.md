[TOC]

# 135.Candy
## 题意
There are N children standing in a line. Each child is assigned a rating value.

You are giving candies to these children subjected to the following requirements:

- Each child must have at least one candy.
- Children with a higher rating get more candies than their neighbors.

What is the minimum candies you must give?

	有N个小朋友站成一排。每个都被赋予了价值。
	发糖果的时候要满足下面要求：
	* 每一个小朋友都至少得到一粒糖
	* 价值高的小朋友，要分到比旁边小朋友更多的糖果

Example 1:

	Input: [1,0,2]
	Output: 5
	Explanation: You can allocate to the first, second and third child with 2, 1, 2 candies respectively.

Example 2:

	Input: [1,2,2]
	Output: 4
	Explanation: You can allocate to the first, second and third child with 1, 2, 1 candies respectively. The third child gets 1 candy because it satisfies the above two conditions.
## 思路
使用数组存储每个孩子可以获取的糖果，最后的结果是数组的和。
需要两次遍历ratings数组：一次正序，一次逆序。

正序：
默认第一个孩子的糖果是1，如果当前孩子值大于上一个，那么当前孩子的糖果数是上一个孩子糖果数+1；
```java
if(ratings[i] > ratings[i - 1]){
    ratings[i] = ratings[i - 1] + 1;
}else{
    ratings[i] = 1;
}
```

逆序：
如果当前孩子值大于前一个，那么当前孩子的糖果数是当前值和前一个孩子值+1二者中较大的。
```java
if(ratings[i] > ratings[i + 1]){
    ratings[i] = Math.max(ratings[i],ratings[i + 1] + 1);
}
```
## 解法

### java

```java
class Solution{
    public int candy(int[] ratings){
        int len = ratings.length;
        if(len <= 1){
            return len;
        }
        int[] ans = new int[len];
        ans[0] = 1;
        for(int i = 1; i < len; i++){
            if(ratings[i] > ratings[i - 1]){
                ans[i] = ans[i - 1] + 1;
            }else{
                ans[i] = 1;
            }
        }
        int sum = ans[len - 1];
        for(int i = len - 2; i >= 0; i--){
            if(ratings[i] > ratings[i + 1]){
                ans[i] = Math.max(ans[i], ans[i + 1] + 1);
            }
            sum += ans[i];
        }
        return sum;
    }
}
```
### C

```c
#include<stdio.h>
int candy(int *rating, int ratingsSize);

int main(void){
    int a[] = {1, 0, 2};
    int res = candy(a, 3);
    printf("The total candy is: %d \n", res);
    return 0;
}
int candy(int *rating, int ratingsSize){
    int len = ratingsSize;
    if(len <= 1) return len;
    int ans[len];
    ans[0] = 1;
    for(int i = 1; i < len; i++){
        if(*(rating + i) > *(rating + i - 1)){
            ans[i] = ans[i - 1] + 1;
        }else{
            ans[i] = 1;
        }
    }
    int sum = ans[len - 1];
    for(int i = len - 2; i >= 0; i--){
        if(*(rating + i) > *(rating + i + 1)){
            ans[i] = ans[i] > ans[i + 1] + 1 ? ans[i] : ans[i + 1] + 1;
        }
        sum += ans[i];
    }
}
```

### C++

```c++
#include <vector>
using namespace std;
class Solution{
    public:
    int candy(vector<int> *ratings){
        int size = ratings.size();
        if(size <= 1) return size;
        int ans[size];
        ans[0] = 1;
        for(int i = 1; i < size; i++){
            if(ratings[i] > ratings[i - 1]){
                ans[i] = ans[i - 1] + 1;
            }else{
                ans[i] = 1;
            }
        }
        int sum = ans[size - 1];
        for(int i = size - 2; i >= 0; i--){
            if(ratings[i] > ratings[i + 1]){
                ans[i] = ans[i] > ans[i + 1] + 1 ? ans[i] : ans[i + 1] + 1;
            }
            sum += ans[i];
        }
        return sum;
    }
}
```

