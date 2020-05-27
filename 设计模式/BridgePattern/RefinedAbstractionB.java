package BridgePattern;

public class RefinedAbstractionB extends Abstraction {

	public RefinedAbstractionB(Implementor impl) {
		super(impl);
		super.operation();
	}
 
}
