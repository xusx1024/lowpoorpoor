package MediatorPattern;

public abstract class Person {

	protected String name;
	protected Mediator mediator;

	public Person(String name, Mediator mediator) {
		super();
		this.name = name;
		this.mediator = mediator;
	}

}
