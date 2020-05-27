package CommandPattern.AudioPlayer;

public class RewindCommand extends Command {
	Radio radio;

	public RewindCommand(Radio radio) {
		super();
		this.radio = radio;
	}

	@Override
	public void execute() {
		radio.rewindAction();
	}

}
