[TOC]
# 包含min函数的栈
## 题目 155. Min Stack
改造栈，增加一个获取最小值的功能。
要求：常量时间获取最小值。

## 思路
改造库里面的Stack。
有的讨论认为不应该使用Stack。我觉得此题考查的关键点是“最小值的变化”，无论使用Array,LinkedList还是别的数据结构，思路都变化不大，都要借助集合里的那些add，remove，get操作。
难点在于：如何动态的更新最小值。
做法：可以考虑，每当添加元素，把当前元素和最小值元素都入栈。那么在出栈的时候，要出栈两次。
这里注意：入栈的顺序，因为还有获取栈顶元素的操作，所以要先入栈最小元素，再入栈真实元素。

## 代码
```java
class MinStack{
    Stack<Integer> stack;
    int min = Integer.MAX_VALUE;
    public MinStack(){
        stack = new Stack<>();
    }
    public void push(int x){
        if(x <= min){
            stack.push(min);
            min = x;
        }
        stack.push(x);
    }
    public void pop(){
        if(stack.pop() == min){
            min = stack.pop();
        }
    }
    
    public int peek(){
        return stack.peek();
    }
    
    public int getMain(){
        return min;
    }
}
```