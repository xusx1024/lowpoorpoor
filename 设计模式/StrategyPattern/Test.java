package StrategyPattern;

public class Test {

	public static void main(String[] args) {
		Context context = new Context(new ConcreteStrategyA());
		context.algorithm();
		
		context = new Context(new ConcreteStrategyB());
		context.algorithm();
	}
}
