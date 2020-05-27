package AbstractFactory;

public class AudiFactory implements AbstractFactory {

	@Override
	public Car getCar() {
		 
		return new Audi();
	}

	@Override
	public Tyre getTyre() {
		 
		return new Michelin();
	}

}
