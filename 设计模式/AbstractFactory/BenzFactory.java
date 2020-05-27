package AbstractFactory;

public class BenzFactory implements AbstractFactory {

	@Override
	public Car getCar() {
		return new Benz();
	}

	@Override
	public Tyre getTyre() {
		return new Goodyear();
	}

}
