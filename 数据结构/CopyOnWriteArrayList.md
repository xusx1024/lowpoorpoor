# CopyOnWriteArrayList

> 1. 要意识到此为数据容器, 需考察其`增`,`删`,`查`.
> 2. 要留意其时空效率.
> 3. 要意识到是开放类, 向`Doug Lea`学习`库`类的设计. 
> 4. 根据特点思考使用场景



###  概述

- `java.util.ArrayList`一个线程安全的变种,  消除`ConcurrentModificationException`.
  -  安全: 所有的变化操作都通过底层复制一个新的数组实现. 
    - 思考: 相比ArrayList的空间,时间占用? 单线程情况和并发情况下.
    - 思考: `System.arraycopy为什么快?`
- 一般来说代价较高,但是:
  - 遍历操作远超过数据改动操作的情况下更加有效
  - 遍历时不想或不可以使用`synchronize`消除并发干扰情况下更加有用
- 迭代器不支持`add`,`set`,`remove`
- 允许null值
- 内存一致性:
  - 和其他并发容器一样, 增加数据操作所在的线程先于访问或移除元素操作所在的其他线程

### 实现


`增`:

```java
/**
* 同步锁. 忽略序列化.
*/
final transient Object lock = new Object();

/**
* 添加指定元素到列表尾端
*/
public boolean add(E e) {
    // 增的时候,需要加锁.
    synchronized (lock) {
        Object[] elements = getArray();
        int len = elements.length();
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        newElements[len] = e;
        setArray(newElements);
        return true;
    }
}
```

读的时候不需要加锁,如果读取时,别的线程在添加数据,读到还是旧容器中的数据.

`删`:

```
    public E remove(int index) {
        synchronized (lock) {
            Object[] elements = getArray();
            int len = elements.length;
            E oldValue = get(elements, index);
            int numMoved = len - index - 1;
            if (numMoved == 0)
                setArray(Arrays.copyOf(elements, len - 1));
            else {
                Object[] newElements = new Object[len - 1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index + 1, newElements, index,
                                 numMoved);
                setArray(newElements);
            }
            return oldValue;
        }
    }
```

`改`:

```
    public E set(int index, E element) {
        synchronized (lock) {
            Object[] elements = getArray();
            E oldValue = get(elements, index);

            if (oldValue != element) {
                int len = elements.length;
                Object[] newElements = Arrays.copyOf(elements, len);
                newElements[index] = element;
                setArray(newElements);
            } else {
                // Not quite a no-op; ensures volatile write semantics
                setArray(elements);
            }
            return oldValue;
        }
    }
```

`查`:

```
public synchronized E get(int index) {
    if (index >= elementCount) {
        throw new ArrayIndexOutOfBoundsException(index);
    }
    return elementData(index);
}
```


### COW

写入时复制（CopyOnWrite，简称COW）思想是计算机程序设计领域中的一种优化策略。其核心思想是，如果有多个调用者（Callers）同时要求相同的资源（如内存或者是磁盘上的数据存储），他们会共同获取相同的指针指向相同的资源，直到某个调用者视图修改资源内容时，系统才会真正复制一份专用副本（private copy）给该调用者，而其他调用者所见到的最初的资源仍然保持不变。这过程对其他的调用者都是透明的（transparently）。此做法主要的优点是如果调用者没有修改资源，就不会有副本（private copy）被创建，因此多个调用者只是读取操作时可以共享同一份资源。

### 注意事项

1. 减少扩容的开销. 可以根据实际需要初始化大小,避免写时扩容的开销.
2. 使用批量添加,减少添加次数.
3. 内存占用,因为COW的写时复制机制, 在写的时候,内存里会同时驻扎两个内存对象. 如果这些对象占用内存较大,有可能造成频繁的GC.
4. 数据一致性问题: 只能保证数据的最终一致性,不能保证实时一致性.
5. 和Vector相比: 并发安全且性能好. 因为Vector各个方法都是有同步锁的,性能会大大下降.

### 规范

1. Effective Java 13: 使类和成员的可访问性最小化

```java
private transient volatile Object[] elements;
final Object[] getArray() {
    return elements;
}

final void setArray(Object[] a) {
    elements = a;
}
```

`get/set`方法都是默认访问权限,平时我们都是直接访问`elements`, `Doug Lea` 通过`get/set` 访问.



2. Effective Java 14: 在公有类中使用访问方法而非公有域

```java
public int size() {
        return getArray().length;
    }
    
public boolean isEmpty() {
        return size() == 0;
    } 
```

3. null值在index时的处理, 同理lastIndexOf的处理

```java
    private static int indexOf(Object o, Object[] elements,int index, int fence) {
        if (o == null) {
            for (int i = index; i < fence; i++)
                if (elements[i] == null)
                    return i;
        } else {
            for (int i = index; i < fence; i++)
                if (o.equals(elements[i]))
                    return i;
        }
        return -1;
    }
```

4. 如何抛出角标越界异常

```java
static String outOfBounds(int index, int size) {
    return "Index: " + index + ", Size: " + size;
}
// 调用
throw new IndexOutOfBoundsException(outOfBounds(index, len));
```
