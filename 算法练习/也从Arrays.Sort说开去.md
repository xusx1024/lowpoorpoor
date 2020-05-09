[TOC]

# 也从Arrays.Sort说开去

经常使用JDK里`Arrays.sort()`这个API。跟源码发现，具体实现在`java.util.DaulPivotQuicksort`类中。类名的字面意思是双枢轴快排。由于在《算法导论》中，pivot也被称为主元，因此也可以翻译为双主元快排。

双主元快排拥有O(n long(n))的时间复杂度。尤其在数据量很大的情况下性能比单主元快排要优秀。

##  经典快排
- 从数组中挑一个元素，称为pivot(在《算法导论》中被称为主元)
- 根据这个pivot重排序数组剩余元素，小于等于pivot放在其左边，大于pivot放在其右边。这样，pivot的最终位置就确定了。
- 递归地排序pivot左右两边的两个数组

## 双主元快排
相比经典的快速排序算法，Dual-Pivot Quicksort使用了两个pivot元素。Vladimir Yaroslavskiy在其论文中证明比经典快排更快更高效，特别是在大数组上。这个算法经过了大量的数据验证，并且被推荐写入了JDK7中。

通用步骤：
1. 判断数组元素个数，根据元素的基本类型，如果小于某个值(JDK中，int类型是47)，就使用计数排序，插入排序，归并排序
2.  随机选择两个pivot元素，比如：p1 = a[left]; 	 p2 = a[right]
3.  `p1必须比p2小！`如果不是，则比较并且交换，然后划分为四部分
	- partI ,a[left+1 ... L-1],这个部分的元素全部小于p1
	- partII，a[L...K-1],这个部分的元素全部大于p1，小于p2
	- partIII，a[G+1,right-1],这个部分的元素全部大于p2
	- partIV，这个部分包含这个数组剩余的元素
4.   接下来，partIV的元素a[K]和p1，p2进行大小比较，比较之后，把partIV放到对应的部分，partI，partII，partIII中的某一个
5.  指针L，K，G在相应的方向上移动
6.  当 K <= G时，重复上面4，5步骤
7.  当partIV里所有元素都找到他们的归属，将p1和partI的最后一个元素交换，p2和partIII的第一个元素交换
8.  partI，partII，partIII三部分递归地重复步骤1-7.

## 优点
- 当对基本数据类型进行排序时，将未排序数组划分为3个部分，比经典快排划分为2个部分要高效的多。要排序的数组越大，就比经典快排和JDK6的快排要更高效。论文地址：[Dual-Pivot Quicksort algorithm](https://codeblab.com/wp-content/uploads/2009/09/DualPivotQuicksort.pdf)  
- Dual-Pivot Quciksort算法在有序数组或者充满重复元素的数组也比经典的快速排序要快。
- 算法对于pivot元素P1，P2的选择有额外的改进。不采取首尾两个元素，而是选择两个中间元素。在随机源数据的情况下，所描述的修改并没有使Dual-Pivot Quicksort算法的性能变差。

```java
int third = arrayLen / 3;
int p1 = a[left + third];
int p2 = a[right - third];
```

## 源码

|                             阈值                             |                          注释                           |   算法   |
| :----------------------------------------------------------: | :-----------------------------------------------------: | :------: |
| COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 3200(large short or char array) | 如果`short`或`char`数组大于此阈值，计数排序要优于快排。 | 计数排序 |
|            QUICKSORT_THRESHOLD = 286(small array)            |      如果数组长度小于此阈值，快排要优于归并排序。       | 快速排序 |
|          INSERTION_SORT_THRESHOLD = 47(tiny array)           |    如果数组长度小于此阈值，插入排序要优于快速排序。     | 插入排序 |
|   COUNTING_SORT_THRESHOLD_FOR_BYTE = 29(large byte array)    | 如果`字节数组`长度大于此阈值，计数排序要优于插入排序。  | 计数排序 |

[双基准快排的核心源码](https://gist.github.com/Pzixel/07a59fc7f147c8d46d99) 

```java
class DualPivotQuicksort{
    public void sort(int[] input){
        sort(input,0,input.length - 1);
    }
    
    private void sort(int[] input, int lowIndex, int highIndex){
        if(highIndex <= lowIndex){
            return;
        }
        
        int pivot1 = input[lowIndex];
        int pivot2 = input[highIndex];
               
        if(less(pivot2,pivot1)){
            exchange(intpu,lowIndex,highIndex);
            pivot1 = input[lowIndex];
            pivot2 = input[highIndex];
        }
        
        int i = lowIndex + 1;
        int lt = lowIndex + 1;
        int gt = highIndex + 1;
        
        while(i <= gt){
            if(less(input[i], pivot1)){
                exchange(input,i++,lt++);
            }else if(less(pivot2,input[i])){
                exchange(input,i,gt--)
            }else{
                i++;
            }
        }
        exchange(input,lowIndex,--lt);
        exchange(input,highIndex,++gt);
        
        sort(input,lowIndex,lt - 1);
        sort(input,lt + 1, gt - 1);
        sort(input, gt + 1,highIndex);
    }
}

```

[另一种核心源码：](https://blog.csdn.net/qingxili/article/details/45584613) 

```java
class Solution{
    public static void sort(int[] a){
        sort(a,0,a.length);
    }
    
    public static void sort(int[] a,int fromIndex, int toIndex){
        rangeCheck(a.length,fromIndex,toIndex);
        dualPivotQuicksort(a,fromIndex,toIndex - 1, 3);
    }
    
    private static void rangeCheck(int length, int fromIndex, int toIndex){
        if(fromIndex > toIndex){
            throw new IllegalArgumentException("fromIndex > toIndex");
        }
        if(fromIndex < 0){
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if(toIndex > length){
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }
    
    private static void swap(int[] a, int i, int j){
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
    
    private static void dualPivotQuicksort(int[] a, int left, int right,int div){
        int len = right - left;
        if(len < 27){ // insertion sort for tiny array
            for(int i = left + 1; i <= right; i++){
                for(int j = i; j > left && a[j] < a[j - 1];j--){
                    swap(a, j, j - 1);
                }
            }
            return;
        }
        
        int third = len / div;
          // "medians"
        int m1 = left + third;
        int m2 = right - third;
        if(m1 <= left){
            m1 = left + 1;
        }
        
        if(m2 >= right){
            m2 = right -1;
        }
        
        if(a[m1] < a[m2]){
            swap(a, m1, left);
            swap(a, m2, right);
        }else{
            swap(a, m1, right);
            swap(a, m2, left);
        }
        // pivots
        int pivot1 = a[left];
        int pivot2 = a[right];
        // pointers
        int less = left + 1;
        int great = right - 1;
        // sorting
        for(int k = less; k <= great; k++){
            if(a[k] < pivot1){
                swap(a, k, less++);
            }else if(a[k] > pivot2){
                while(k < great && a[great] > pivot2){
                    great--;
                }
                swap(a, k, great--);
                if(a[k] < pivot1){
                    swap(a, k, less++);
                }
            }
        }
        // swaps
        int dist = great - less;
        if(dist < 13){
            div++;
        }
        
        swap(a, less - 1, left);
        swap(a, great + 1, right);
         // subarrays
        dualPivotQuicksort(a, left, less - 2, div);
        dualPivotQuicksort(a, great + 2, right, div);
        // equal elements
        if(dist > len - 13 && pivot1 != pivot2){
            for(int k = less; k <= great; k++){
                if(a[k] == pivot1){
                    swap(a, k, less++);
                }else if(a[k] == pivot2){
                    swap(a, k, great--);
                    if(a[k] == pivot){
                        swap(a, k, less++);
                    }
                }
            }
        }
         // subarray
        if(pivot1 < pivot2){
            dualPivotQuicksort(a, less, great, div);
        }
    }
    
}
```




