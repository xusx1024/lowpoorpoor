package StatePattern.ScreenDemo;

public class Screen {
	private State currentState, normalState, largerState, largestState;

	public Screen() {
		this.normalState = new NormalState();
		this.largerState = new LargerState();
		this.largestState = new LargestState();
		this.currentState = normalState;
		this.currentState.display();
	}

	public void setState(State state) {
		this.currentState = state;
		currentState.display();
	}

	public void onClick() {
		if (currentState == normalState) {
			setState(largerState);
		} else if (currentState == largerState) {
			setState(largestState);
		} else if (currentState == largestState) {
			setState(normalState);
		}
	}
}
