package IteratorPattern;

import java.util.List;

/**
 * 在迭代器模式结构图中，我们可以看到具体迭代器和具体聚合类之间存在着双重关系 其中一个为关联关系，在具体的迭代器中维护一个对具体聚合对象的引用，该关联关系
 * 的目的是<B>访问存储在聚合对象中的数据<B>，以便迭代器能够对这些数据进行遍历。{@link ProductIterator} <br/>
 * 为了让迭代器可以访问到聚合对象中的数据，我们可以考虑把迭代器做成聚合对象的内部类。参考java.util.List类的具体实现
 * 
 * @author sxx.xu
 *
 */
public class ProductList2 extends ObjectList {

	public ProductList2(List<Object> objects) {
		super(objects);
	}

	@Override
	public Iterator createIterator() {
		return new ProductIterator();
	}

	class ProductIterator implements Iterator {

		private int cursor1;
		private int cursor2;

		public ProductIterator() {
			cursor1 = 0;
			cursor2 = objects.size() - 1;
		}

		@Override
		public void next() {
			if (cursor1 < objects.size()) {
				cursor1++;
			}

		}

		@Override
		public boolean isLast() {

			return cursor1 == objects.size();
		}

		@Override
		public void previous() {
			if (cursor2 > -1) {
				cursor2--;
			}
		}

		@Override
		public boolean isFirst() {

			return cursor2 == -1;
		}

		@Override
		public Object getNextItem() {

			return objects.get(cursor1);
		}

		@Override
		public Object getPreviousItem() {

			return objects.get(cursor2);
		}

	}
}
