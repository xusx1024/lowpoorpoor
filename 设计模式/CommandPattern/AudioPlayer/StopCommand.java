package CommandPattern.AudioPlayer;

public class StopCommand extends Command {
	Radio radio;

	public StopCommand(Radio radio) {
		super();
		this.radio = radio;
	}

	@Override
	public void execute() {
		radio.stopAction();
	}

}
