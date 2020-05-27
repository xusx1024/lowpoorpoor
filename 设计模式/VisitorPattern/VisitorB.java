package VisitorPattern;

public class VisitorB implements Visitor {

	@Override
	public void visit(NodeA node) {
		System.out.println("VisitorB:" + node.operationA());
	}

	@Override
	public void visit(NodeB node) {
		System.out.println("VisitorB:" + node.operationB());
	}

}
