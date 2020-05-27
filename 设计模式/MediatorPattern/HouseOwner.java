package MediatorPattern;

public class HouseOwner extends Person {

	public HouseOwner(String name, Mediator mediator) {
		super(name, mediator);
	}

	public void constact(String message) {
		mediator.constact(message, this);
	}

	public void getMessage(String message) {
		System.out.println("房东：" + name + "得到信息：" + message);
	}
}
