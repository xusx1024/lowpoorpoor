package InterpreterPattern;

public class AndNode extends AbstractNode {

	private AbstractNode left;
	private AbstractNode right;

	public AndNode(AbstractNode left, AbstractNode right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public String interpret() {
		return left.interpret() + "再"+right.interpret();
	}

}
