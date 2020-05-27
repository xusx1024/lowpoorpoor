package StatePattern;

public class ConcreteStateA implements State{

	@Override
	public void handle() {
		System.out.println("state A is handleing");
	}

}
