package InterpreterPattern;

import java.util.Stack;

public class InstructionHandler {

	private String instruction;
	private AbstractNode node;

	public void handle(String instruction) {
		AbstractNode left = null, right = null;
		AbstractNode direction = null, action = null, distance = null;
		Stack<AbstractNode> stack = new Stack<>();
		String[] words = instruction.split(" ");
		for (int i = 0; i < words.length; i++) {
			if (words[i].equalsIgnoreCase("and")) {
				left = stack.pop();
				String word1 = words[++i];
				direction = new DirectionNode(word1);
				String word2 = words[++i];
				action = new ActionNode(word2);
				String word3 = words[++i];
				distance = new DistanceNode(word3);

				right = new SentenceNode(direction, action, distance);
				stack.push(new AndNode(left, right));

				System.out.println("with and left:" + left.interpret());
				System.out.println("with and right:" + right.interpret());
			} else {
				String word1 = words[i];
				direction = new DirectionNode(word1);
				String word2 = words[++i];
				action = new ActionNode(word2);
				String word3 = words[++i];
				distance = new DistanceNode(word3);
				left = new SentenceNode(direction, action, distance);
				stack.push(left);
				System.out.println("no and:" + left.interpret());
			}
		}
		this.node = stack.pop();
	}

	public String output() {
		return node.interpret();
	}
}
