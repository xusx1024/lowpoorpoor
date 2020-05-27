package CommandPattern.AudioPlayer;

public class PlayCommand extends Command {

	Radio radio;

	public PlayCommand(Radio radio) {
		super();
		this.radio = radio;
	}

	@Override
	public void execute() {
		radio.playAction();
	}

}
