package VisitorPattern;

public class NodeB extends Node {

	@Override
	public void accept(Visitor vistor) {
		vistor.visit(this);
	}

	public String operationB() {
		return "NodeB";
	}

}
