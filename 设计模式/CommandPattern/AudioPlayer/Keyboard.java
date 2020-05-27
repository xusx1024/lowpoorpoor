package CommandPattern.AudioPlayer;

public class Keyboard {

	private Command command;

	public Keyboard(Command command) {
		super();
		this.command = command;
	}

	
	public Command getCommand() {
		return command;
	}


	public void setCommand(Command command) {
		this.command = command;
	}


	public void action() {
		command.execute();
	}
}
