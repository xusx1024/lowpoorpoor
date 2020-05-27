package VisitorPattern;

public class NodeA extends Node {

	@Override
	public void accept(Visitor vistor) {
		vistor.visit(this);
	}

	public String operationA() {
		return "NodeA";
	}
}
