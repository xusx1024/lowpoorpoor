package StatePattern;

public class ConcreteStateB implements State{

	@Override
	public void handle() {
		System.out.println("state B is handleing");
	}

}
