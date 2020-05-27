package CommandPattern.AudioPlayer;

public class Julia {
	public static void main(String[] args) {
		Radio radio = new Radio();
		Command command = new PlayCommand(radio);
		Keyboard keyboard = new Keyboard(command);
		keyboard.action();

		keyboard.setCommand(new RewindCommand(radio));
		keyboard.action();

		keyboard.setCommand(new StopCommand(radio));
		keyboard.action();
	}
}
