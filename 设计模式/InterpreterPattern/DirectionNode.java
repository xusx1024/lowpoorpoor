package InterpreterPattern;

/**
 * 方向。terminal expression
 * 
 * @author sxx.xu
 *
 */
public class DirectionNode extends AbstractNode {
	private String direction;

	public DirectionNode(String direction) {
		super();
		this.direction = direction;
	}

	@Override
	public String interpret() {

		if (direction.equalsIgnoreCase("up")) {
			res = "向上";
		} else if (direction.equalsIgnoreCase("down")) {
			res = "向下";
		} else if (direction.equalsIgnoreCase("left")) {
			res = "向左";
		} else if (direction.equalsIgnoreCase("right")) {
			res = "向右";
		}
		return res;
	}

}
