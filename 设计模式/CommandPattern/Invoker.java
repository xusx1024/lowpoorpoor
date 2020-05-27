package CommandPattern;

public class Invoker {
	private Command command;

	public Invoker(Command command) {
		 System.out.println("Invoker 构造注入命令对象");
		this.command = command;
	}

	public void action() {
		command.execute();
	}
}
