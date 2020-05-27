package StrategyPattern;

public class Context {

	private Strategy mStrategy;

	public Context(Strategy s){
		this.mStrategy = s;
	}
	
	public void algorithm(){
		mStrategy.algorithm();
	}
}
