package CommandPattern;

public class ConcreteCommand2 extends Command {

	private Receiver receiver;

	public ConcreteCommand2(Receiver receiver) {
		super();
		this.receiver = receiver;
	}

	@Override
	public void execute() {
		System.out.println("具体命令类，通常会转调接收者对象的相应方法，让接收者来真正执行功能。");
		receiver.action();
	}

}
