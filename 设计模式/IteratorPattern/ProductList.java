package IteratorPattern;

import java.util.List;

public class ProductList extends ObjectList {

	public ProductList(List<Object> objects) {
		super(objects);
	}

	@Override
	public Iterator createIterator() {
		return new ProductIterator(this);
	}

}
