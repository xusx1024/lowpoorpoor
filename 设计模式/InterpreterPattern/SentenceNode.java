package InterpreterPattern;

public class SentenceNode extends AbstractNode {

	private AbstractNode direction;
	private AbstractNode action;
	private AbstractNode distance;

	public SentenceNode(AbstractNode direction, AbstractNode action, AbstractNode distance) {
		super();
		this.direction = direction;
		this.action = action;
		this.distance = distance;
	}

	@Override
	public String interpret() {
		return direction.interpret() + action.interpret() + distance.interpret();
	}

}
