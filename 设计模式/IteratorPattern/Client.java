package IteratorPattern;

import java.util.ArrayList;
import java.util.List;

public class Client {

	public static void main(String[] args) {

		List<Object> products = new ArrayList<>();
		products.add("倚天剑");
		products.add("屠龙刀");
		products.add("断肠草");
		products.add("葵花宝典");
		products.add("四十二章经");

		ObjectList list;
		Iterator iterator;

//		list = new ProductList(products);
		list = new ProductList2(products);
		iterator = list.createIterator();

		System.out.println("正向遍历：");
		while (!iterator.isLast()) {
			System.out.print(iterator.getNextItem() + "，");
			iterator.next();
		}
		System.out.println();
		System.out.println("-----------------------------");
		System.out.println("逆向遍历：");
		while (!iterator.isFirst()) {
			System.out.print(iterator.getPreviousItem() + "，");
			iterator.previous();
		}
		
		
	 
				
	}

}
