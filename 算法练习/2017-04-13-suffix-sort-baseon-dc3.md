---
layout: post
title:  基于DC3算法实现后缀数组
date:   2017-04-13
categories: Algorithm
tag: [算法,hotfix]
---
 


#### 简介 ####

 DC3算法(Difference Cover mod 3)是J. Kärkkäinen和P. Sanders在2003年发表的论文 "Simple Linear Work Suffix Array Construction"中描述的线性时间内构造后缀数组的算法。相对Prefix Doubling（前缀倍增）算法而言，虽然它的渐进时间复杂度比较小，但是常数项比较大。DC3算法的思想类似于找中位数的median of medians算法

#### 算法原理 ####
 
1. 先将后缀分成两部分，然后对第一部分的后缀排序;
2. 利用1的结果，对第二部分的后缀排序;
3. 将1和2的结果合并，即完成对所有后缀排序;

 
#### 代码 ####
	 
	package DC3;
	
	/**
	 * DC3算法构建后缀数组
	 * 
	 * @author sxx.xu
	 *
	 */
	public class DC3 {
	
		public static final char MAX_CHAR = '\u00FF';
	
		class Suffix {
			int[] sa;
			int[] rank;
			boolean done;
	
			public Suffix(int[] sa, int[] rank) {
				this.sa = sa;
				this.rank = rank;
			}
		}
	
		class Tuple {
			int iSuffix;
			int[] digits;
	
			public Tuple(int iSuffix, int[] digits) {
				this.iSuffix = iSuffix;
				this.digits = digits;
			}
	
			public String toString() {
				StringBuffer sb = new StringBuffer();
				sb.append(iSuffix);
				sb.append("(");
				for (int i = 0; i < digits.length; i++) {
					sb.append(digits[i]);
					if (i < digits.length - 1)
						sb.append("-");
				}
				sb.append(")");
				return sb.toString();
			}
		}
	
		private void countingSort(int d, Tuple[] tA, Tuple[] tB, int max) {
	
			int[] C = new int[max + 1];
			for (int i = 0; i <= max; i++) {
				C[i] = 0;
			}
	
			for (int i = 0; i < tA.length; i++) {
				C[tA[i].digits[d]]++;
			}
	
			for (int i = 1; i <= max; i++) {
				C[i] += C[i - 1];
			}
	
			for (int i = tA.length - 1; i >= 0; i--) {
				tB[--C[tA[i].digits[d]]] = tA[i];
			}
		}
	
		private void radixSort(Tuple[] tA, Tuple[] tB, int max, int digitsLen) {
			int len = tA.length;
			int digitsTotalLen = tA[0].digits.length;
	
			for (int i = digitsTotalLen - 1, j = 0; j < digitsLen; i--, j++) {
				this.countingSort(i, tA, tB, max);
				if (j < digitsLen - 1) {
					for (int k = 0; k < len; k++) {
						tA[k] = tB[k];
					}
				}
			}
		}
	
		private Suffix rank(Tuple[] tA, Tuple[] tB, int max, int digitsLen) {
			int len = tA.length;
			radixSort(tA, tB, max, digitsLen);
	
			int digitsTotalLen = tA[0].digits.length;
	
			int[] sa = new int[len];
			sa[0] = tB[0].iSuffix;
	
			int[] rank = new int[len + 2];
			rank[len] = 1;
			rank[len + 1] = 1;
	
			int r = 1;
	
			rank[tB[0].iSuffix] = r;
	
			for (int i = 1; i < len; i++) {
				sa[i] = tB[i].iSuffix;
	
				boolean equalLast = true;
	
				for (int j = digitsTotalLen - digitsLen; j < digitsTotalLen; j++) {
					if (tB[i].digits[j] != tB[i - 1].digits[j]) {
						equalLast = false;
						break;
					}
				}
	
				if (!equalLast)
					r++;
	
				rank[tB[i].iSuffix] = r;
	
			}
	
			Suffix suffix = new Suffix(sa, rank);
			if (r == len) {
				suffix.done = true;
			} else {
				suffix.done = false;
			}
	
			return suffix;
		}
	
		private int[] orderSuffix(Tuple[] tA, Tuple[] tB, int max, int digitsLen) {
	
			int len = tA.length;
			radixSort(tA, tB, max, digitsLen);
			int[] sa = new int[len];
			for (int i = 0; i < sa.length; i++)
				sa[i] = tB[i].iSuffix;
	
			return sa;
	
		}
	
		public Suffix reduce(int[] rank, int max) {
	
			int len = rank.length - 2;
			int n1 = (len + 1) / 3;
			int n2 = len / 3;
	
			Tuple[] tA = new Tuple[n1 + n2];
			Tuple[] tB = new Tuple[n1 + n2];
	
			for (int i = 0, j = 1; i < n1; i++, j += 3) {
	
				int r1 = rank[j];
				int r2 = rank[j + 1];
				int r3 = rank[j + 2];
	
				tA[i] = new Tuple(i, new int[] { r1, r2, r3 });
			}
	
			for (int i = n1, j = 2; i < n1 + n2; i++, j += 3) {
	
				int r1 = rank[j];
				int r2 = rank[j + 1];
				int r3 = rank[j + 2];
	
				tA[i] = new Tuple(i, new int[] { r1, r2, r3 });
			}
	
			return rank(tA, tB, max, 3);
		}
	
		public int[] skew(int[] rank, int max) {
			int len = rank.length - 2;
	
			Suffix suffixT12 = reduce(rank, max);
	
			int[] sa12 = null;
	
			// 1.caculate sa12
			if (!suffixT12.done) {
				int[] rankT12 = suffixT12.rank;
				int maxT12 = rankT12[suffixT12.sa[suffixT12.sa.length - 1]];
				sa12 = skew(rankT12, maxT12);
			} else {
				sa12 = suffixT12.sa;
			}
	
			// index conversion for sa12
			int n1 = (len + 1) / 3;
			for (int i = 0; i < sa12.length; i++) {
				if (sa12[i] < n1) {
					sa12[i] = 1 + 3 * sa12[i];
				} else {
					sa12[i] = 2 + 3 * (sa12[i]-n1);
				}
			}
	
			// recaculate rank for sa12
			int[] rank12 = new int[len + 2];
	
			rank12[len] = 1;
			rank12[len + 1] = 1;
	
			for (int i = 0; i < sa12.length; i++) {
				rank12[sa12[i]] = i + 1;
			}
	
			// 2.caculate sa0
	
			int n0 = (len + 2) / 3;
			Tuple[] tA = new Tuple[n0];
			Tuple[] tB = new Tuple[n0];
	
			for (int i = 0, j = 0; i < n0; i++, j += 3) {
				int r1 = rank[j];
				int r2 = rank12[j + 1];
				tA[i] = new Tuple(i, new int[] { r1, r2 });
			}
	
			int max12 = rank12[sa12[sa12.length - 1]];
			int[] sa0 = orderSuffix(tA, tB, max < max12 ? max12 : max, 2);
	
			for (int i = 0; i < n0; i++) {
				sa0[i] = 3 * sa0[i];
			}
	
			// 3.merge sa12 and sa0
	
			int[] sa = new int[len];
			int i = 0, j = 0, k = 0;
	
			while (i < sa12.length && j < sa0.length) {
				int p = sa12[i];
				int q = sa0[j];
	
				if (p % 3 == 1) {
	
					if (rank[p] < rank[q]) {
						sa[k++] = p;
						i++;
					} else if (rank[p] > rank[q]) {
						sa[k++] = q;
						j++;
					} else {
						if (rank12[p + 1] < rank12[q + 1]) {
							sa[k++] = p;
							i++;
						} else {
							sa[k++] = q;
							j++;
						}
					}
				} else {
					if (rank[p] < rank[q]) {
						sa[k++] = p;
						i++;
					} else if (rank[p] > rank[q]) {
						sa[k++] = q;
						j++;
					} else {
						if (rank[p + 1] < rank[q + 1]) {
							sa[k++] = p;
							i++;
						} else if (rank[p + 1] > rank[q + 1]) {
							sa[k++] = q;
							j++;
						} else {
							if (rank12[p + 2] < rank12[q + 2]) {
								sa[k++] = p;
								i++;
							} else {
								sa[k++] = q;
								j++;
							}
						}
					}
				}
			}
			for (int m = i; m < sa12.length; m++) {
				sa[k++] = sa12[m];
			}
			for (int m = j; m < sa0.length; m++) {
				sa[k++] = sa0[m];
			}
			return sa;
	
		}
	
		 public Suffix solve(String text){  
		        if(text == null)return null;  
		        int len = text.length();  
		        if(len == 0) return null;  
		          
		        char base = text.charAt(len-1); //the smallest char  
		        Tuple[] tA = new Tuple[len];  
		        Tuple[] tB = new Tuple[len]; //placeholder  
		        for(int i=0;i<len;i++){  
		            tA[i] = new Tuple(i,new int[]{0,text.charAt(i)-base});  
		        }  
		        Suffix suffix = rank(tA,tB,MAX_CHAR-base,1);  
		           
		        int max = suffix.rank[suffix.sa[len-1]];  
		        int[] sa  = skew(suffix.rank,max);  
		          
		        //caculate rank for result suffix array  
		        int[] r = new int[len];       
		        for(int k=0;k<sa.length;k++){  
		            r[sa[k]] = k+1;  
		        }  
		        return new Suffix(sa,r);  
		          
		    }  
		    public void report(Suffix suffix){  
		        int[] sa = suffix.sa;  
		        int[] rank = suffix.rank;  
		        int len = sa.length;  
		          
		        System.out.println("suffix array:");  
		        for(int i=0;i<len;i++){  
		            System.out.format(" %s", sa[i]);              
		        }  
		        System.out.println();  
		        System.out.println("rank array:");  
		        for(int i=0;i<len;i++){  
		            System.out.format(" %s", rank[i]);            
		        }         
		        System.out.println();  
		    }  
		    public static void main(String[] args) {  
		        String text = "GACCCACCACC#";  
		        DC3 dc3 = new DC3();  
		        Suffix suffix = dc3.solve(text);  
		        System.out.format("Text: %s%n",text);  
		        dc3.report(suffix);  
		          
		        text = "mississippi#";  
		        dc3 = new DC3();  
		        suffix = dc3.solve(text);  
		        System.out.format("Text: %s%n",text);  
		        dc3.report(suffix);  
		          
		        text = "abcdefghijklmmnopqrstuvwxyz#";  
		        dc3 = new DC3();  
		        suffix = dc3.solve(text);  
		        System.out.format("Text: %s%n",text);  
		        dc3.report(suffix);  
		          
		        text = "yabbadabbado#";  
		        dc3 = new DC3();  
		        suffix = dc3.solve(text);  
		        System.out.format("Text: %s%n",text);  
		        dc3.report(suffix);  
		          
		        text = "DFDLKJLJldfasdlfjasdfkldjasfldafjdajfdsfjalkdsfaewefsdafdsfa#";  
		        dc3 = new DC3();  
		        suffix = dc3.solve(text);  
		        System.out.format("Text: %s%n",text);  
		        dc3.report(suffix);  
		    }  
	}
