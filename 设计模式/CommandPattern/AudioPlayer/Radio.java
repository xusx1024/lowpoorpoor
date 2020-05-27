package CommandPattern.AudioPlayer;

/**
 * 相当于receiver,receiver的各个功能应该分开。
 * receiver也可以省略，在对应的ConcreteCommand中实现对应的操作。
 * 
 * @author sxx.xu
 *
 */
public class Radio {

	public void playAction() {
		System.out.println("radio played!");
	}

	public void rewindAction() {
		System.out.println("radio rewinded!");
	}

	public void stopAction() {
		System.out.println("radio stoped!");
	}
}
