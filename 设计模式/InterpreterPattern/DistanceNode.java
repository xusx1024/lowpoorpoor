package InterpreterPattern;

public class DistanceNode extends AbstractNode {

	private String distance;

	public DistanceNode(String distance) {
		super();
		this.distance = distance;
		res = distance;
	}

	@Override
	public String interpret() {
		return res;
	}

}
