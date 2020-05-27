package DecoratorPattern;

public class ConcreteDecoreator extends Decorator {

	public ConcreteDecoreator(Component c) {
		super(c);
		addBehavior();
	}

	private void addBehavior() {
		// do sth
	}

}
