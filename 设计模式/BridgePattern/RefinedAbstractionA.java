package BridgePattern;

public class RefinedAbstractionA extends Abstraction {

	public RefinedAbstractionA(Implementor impl) {
		super(impl);
		super.operation();
	}
 
}
