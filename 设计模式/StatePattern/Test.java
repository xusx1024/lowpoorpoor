package StatePattern;

public class Test {

	public static void main(String[] args) {
		Context context = new Context();
		context.changeState(new ConcreteStateA());//这里面的参数是根据程序的运行状态自动传入，调用者不必知道其细节
		context.changeState(new ConcreteStateB());//如果此时是策略模式，此处需要调用者明白自己传入的何种策略及其区别
	}

}
