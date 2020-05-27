package BridgePattern;

public class Abstraction {
	private Implementor impl;

	public Abstraction(Implementor impl) {
		super();
		this.impl = impl;
	}

	public void operation() {
		impl.operationImpl();
	}
}
