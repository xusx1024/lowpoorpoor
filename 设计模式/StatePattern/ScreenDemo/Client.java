package StatePattern.ScreenDemo;

public class Client {
	public static void main(String[] args) {
		Screen screen = new Screen();
		for(int i=0;i<3;i++){
			screen.onClick();
		}
	}
}
