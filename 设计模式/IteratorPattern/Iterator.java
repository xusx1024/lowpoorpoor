package IteratorPattern;

public interface Iterator {

	void next();

	boolean isLast();

	void previous();

	boolean isFirst();

	Object getNextItem();

	Object getPreviousItem();
}
