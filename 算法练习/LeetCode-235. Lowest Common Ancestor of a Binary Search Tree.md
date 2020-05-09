[TOC]
# LeetCode-235. Lowest Common Ancestor of a Binary Search Tree
## 题目

Given a binary search tree (BST), find the lowest common ancestor (LCA) of two given nodes in the BST.

According to the [definition of LCA on Wikipedia](https://en.wikipedia.org/wiki/Lowest_common_ancestor): “The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has both p and q as descendants (where we allow **a node to be a descendant of itself**).”

Given binary search tree:  root = [6,2,8,0,4,7,9,null,null,3,5]

     6
   /   \
  2     8
 / \   / \
0   4 7   9
   / \
  3   5
 

**Example 1:**

```
Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
Output: 6
Explanation: The LCA of nodes 2 and 8 is 6.
```

**Example 2:**

```
Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
Output: 2
Explanation: The LCA of nodes 2 and 4 is 2, since a node can be a descendant of itself according to the LCA definition.
```

 

**Note:**

- All of the nodes' values will be unique.
- p and q are different and both values will exist in the BST.



题目大意：

在二叉搜索树中找到任意两个节点的最近公共父节点。

节点的值不会重复，指定节点必定BST中存在。

## 思路

根据BST左小右大的特点来判断。

- 如果p，q是在root两边，那么root就是所求的最近公共父节点
- 如果p，q是在root的同侧，那么root赋值为自己的子节点，继续这两步判断，直到得到最终结果。

## 解法

java:

```java
class Solution{
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q){
        while(root != null){
            if(root.val > p.val && root.val > q.val){
                root = root.left;
            }else if(root.val < p.val && root.val <q.val){
                root = root.right;
            }else{
                break;
            }
        }
        return root;
    }
}
```



c:

```c
struct TreeNode* lowestCommonAncestor(struct TreeNode* root, struct TreeNode* p, struct TreeNode* q){
    while(root != NULL){
        if(root -> val > p -> val && root -> val > q -> val){
            root = root -> left;
        }else if(root -> val < p -> val && root -> val < q -> val){
            root = root -> right;
        }else{
            break;
        }
    }
    return root;
}
```







