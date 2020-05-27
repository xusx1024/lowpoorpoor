package DecoratorPattern;

public class Decorator implements Component {

	private Component c;

	public Decorator(Component c) {
		super();
		this.c = c;
	}

	@Override
	public void sampleOperation() {
		c.sampleOperation();
	}

}
