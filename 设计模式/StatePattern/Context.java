package StatePattern;

public class Context {

	public void changeState(State state){
		state.handle();
	}
}
