package InterpreterPattern;

public class ActionNode extends AbstractNode {

	private String action;
	
	public ActionNode(String action) {
		super();
		this.action = action;
	}

	@Override
	public String interpret() {
		if(action.equalsIgnoreCase("move")){
			res = "移动";
		}else if(action.equalsIgnoreCase("run")){
			res = "快速移动";
		}
		return res;
	}

}
