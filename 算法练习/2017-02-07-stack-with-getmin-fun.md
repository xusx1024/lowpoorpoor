---
layout: post
title:  设计一个有getMin功能的栈
date:   2017-02-07
categories: Algorithm
tag: 算法
---
 

#### **题目** ####


##### 设计一个有getMin功能的栈 #####
>
实现一个特殊的栈，在实现栈的基本功能的基础上，再实现返回栈中最小元素的操作。

#### **要求** ####
>
1. pop,push,getMin操作的时间复杂度都是O(1)-【注】：*pop，push的时间复杂度本来就是O(1)，所以关注点在getMin的时间复杂度上。3/3/2017 11:16:04 AM *
2. 设计的栈类型可以使用现成的栈结构-【注】：*使用变量是无法达到getMin的O(1)的，eg：2，3，4，1。3/3/2017 11:16:21 AM * 


#### **难度** ####
>
士 ★☆☆☆


#### **解答** ####
>
在设计上我们使用两个栈，一个栈用来保存当前栈中的元素，其功能和一个正常的栈没有区别，这个栈记为stackData;另一个栈用于保存每一步的最小值，这个栈记为stackMin。具体的实现方式有两种。

##### **第一种设计方案** #####

	
 压入数据规则
 

假如当前数据为newNum,先将其压入stackData。然后判断stackMin是否为空；
	
- 如果为空，则newNum也压入stackMin
- 如果不为空，则比较nuwNum和stackMin的栈顶元素哪一个更小
- 如果newNum更小或两者相等，则newNum也压入stackMin
- 如果stackMin中栈顶元素小，则stackMin不压入任何内容



 弹出数据规则

先在stackData中弹出栈顶元素，记为value。然后比较当前stackMin的栈顶元素和value哪一个更小。

通过上文的压入规则可知，stackMin中存在的元素是从栈底到栈顶逐渐变小的，stackMin栈顶的元素及时stackMin栈的最小值，也是stackData栈的最小值。所以不会出现value小于stackMin栈顶元素的情况，value只可能大于或等于stackMin的栈顶元素。

当value等于stackMin的栈顶元素时，stackMin弹出栈顶元素【保证stackMin的栈顶始终为最小元素的值 3/3/2017 11:51:24 AM 】；当value大于stackMin的栈顶元素时，stackMin不弹出栈顶元素；返回value。



**查询当前栈中最小值的操作**
	
即stackMin的栈顶元素


		public class MyStack1 {
		private Stack<Integer> stackData;
		private Stack<Integer> stackMin;
	
		public MyStack1() {
			this.stackData = new Stack<>();
			this.stackMin = new Stack<>();
		}
	
		public void push(int newNum) {
			if (this.stackMin.isEmpty()) {
				stackMin.push(newNum);
			} else if (newNum <= this.getmin()) {
				stackMin.push(newNum);
			}
	
			this.stackData.push(newNum);
		}
	
		public int pop() {
			if (this.stackData.isEmpty()) {
				throw new RuntimeException("Your stack is Empty.");
			}
			int value = stackData.peek();
			if (value == this.getmin()) {
				stackMin.pop();
			}
			return value;
		}
	
		public int getmin() {
			if (this.stackMin.isEmpty()) {
				throw new RuntimeException("Your stack is empty.");
			}
			return stackMin.peek();
		}
	
	    }

 


##### **第二种设计方案** #####

 - 压入数据规则

>
假设当前数据为newNum，先将其压入stackData。然后判断stackMin是否为空。
如果为空，则newNum也压入stackMin；如果不为空，则判断newNum和stackMin的栈顶元素大小：
如果newNum比较小，则压入stackMin；否则再次将stackMin栈顶元素压入stackMin。


- 弹出数据规则


>
在stackData中弹出数据记为value；弹出stackMin的栈顶；返回value

- 查询当前栈中最小值的操作


>
即stackMin的栈顶元素


		
		public class MyStack2 {
		
			private Stack<Integer> stackData;
			private Stack<Integer> stackMin;
		
			public MyStack2() {
				this.stackData = new Stack<>();
				this.stackMin = new Stack<>();
			}
		
			public void push(int newNum) {
				if (this.stackMin.isEmpty()) {
					stackMin.push(newNum);
				} else if (newNum < this.getmin()) {
					stackMin.push(newNum);
				} else {
					int newMin = stackMin.peek();
					stackMin.push(newMin);
				}
		
				this.stackData.push(newNum);
			}
		
			public int pop() {
				if (this.stackData.isEmpty()) {
					throw new RuntimeException("Your stack is Empty.");
				}
		
				int value = stackData.peek();
				stackMin.pop();
				stackData.pop();
				return value;
			}
		
			public int getmin() {
				if (this.stackMin.isEmpty()) {
					throw new RuntimeException("Your stack is empty.");
				}
				return stackMin.peek();
			}
		}



#### **点评** ####

	方案一和方案二其实都是用stackMin栈保存着stackData每一步的最小值。共同点是所有操作的时间复杂度都为O(1)、空间复杂度都为O(n).
	区别是：方案一中stackMin压入时稍省空间，但是弹出操作稍费时间；方案二中stackMin压入时稍费时间，但是弹出操作稍省时间。


#### **java.util.stack** ####

 

	package java.util;
	 
	public class Stack<E> extends Vector<E> {
	    /**
	     * Creates an empty Stack.
	     */
	    public Stack() {
	    }
	
	    /**
	     * Pushes an item onto the top of this stack. This has exactly
	     * the same effect as:
	     * <blockquote><pre>
	     * addElement(item)</pre></blockquote>
	     *
	     * @param   item   the item to be pushed onto this stack.
	     * @return  the <code>item</code> argument.
	     * @see     java.util.Vector#addElement
	     */
	    public E push(E item) {
	        addElement(item);
	
	        return item;
	    }
	
	    /**
	     * Removes the object at the top of this stack and returns that
	     * object as the value of this function.
	     *
	     * @return  The object at the top of this stack (the last item
	     *          of the <tt>Vector</tt> object).
	     * @throws  EmptyStackException  if this stack is empty.
	     */
	    public synchronized E pop() {
	        E       obj;
	        int     len = size();
	
	        obj = peek();
	        removeElementAt(len - 1);
	
	        return obj;
	    }
	
	    /**
	     * Looks at the object at the top of this stack without removing it
	     * from the stack.
	     *
	     * @return  the object at the top of this stack (the last item
	     *          of the <tt>Vector</tt> object).
	     * @throws  EmptyStackException  if this stack is empty.
	     */
	    public synchronized E peek() {
	        int     len = size();
	
	        if (len == 0)
	            throw new EmptyStackException();
	        return elementAt(len - 1);
	    }
	
	    /**
	     * Tests if this stack is empty.
	     *
	     * @return  <code>true</code> if and only if this stack contains
	     *          no items; <code>false</code> otherwise.
	     */
	    public boolean empty() {
	        return size() == 0;
	    }
	
	    /**
	     * Returns the 1-based position where an object is on this stack.
	     * If the object <tt>o</tt> occurs as an item in this stack, this
	     * method returns the distance from the top of the stack of the
	     * occurrence nearest the top of the stack; the topmost item on the
	     * stack is considered to be at distance <tt>1</tt>. The <tt>equals</tt>
	     * method is used to compare <tt>o</tt> to the
	     * items in this stack.
	     *
	     * @param   o   the desired object.
	     * @return  the 1-based position from the top of the stack where
	     *          the object is located; the return value <code>-1</code>
	     *          indicates that the object is not on the stack.
	     */
	    public synchronized int search(Object o) {
	        int i = lastIndexOf(o);
	
	        if (i >= 0) {
	            return size() - i;
	        }
	        return -1;
	    }
	
	    /** use serialVersionUID from JDK 1.0.2 for interoperability */
	    private static final long serialVersionUID = 1224463164541339165L;
	}

#### **栈的简单概念** ####

>
栈是限制插入和删除只能在一个位置上进行的表，该位置是表的末端，叫做栈顶。对栈的基本操作有push(进栈)和pop(出栈)，前者相当于插入，后者则是删除最后插入的元素。栈又叫做后进先出表。
 

【注】*栈在java中有[数据结构和数据存储结构](http://xusx1024.com/2017/02/11/different-between-ADT/)两种功能。即stack是数据结构概念，但是java语言中实现了stack功能的容器，也命名为stack。比较特殊和容易让人迷惑。*3/3/2017 11:13:33 AM 

 



























