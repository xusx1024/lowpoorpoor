package InterpreterPattern;

public class Client {

	public static void main(String[] args) {
		String instruction = "up move 5 and down run 10 and left move 5";
		InstructionHandler handler = new InstructionHandler();

		handler.handle(instruction);
		String output = handler.output();
		System.out.println(output);
	}
}
