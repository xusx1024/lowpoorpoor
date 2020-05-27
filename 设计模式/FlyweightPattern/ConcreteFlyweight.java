package FlyweightPattern;

public class ConcreteFlyweight implements Flyweight {

	private Character intrinsicState = null;

	/**
	 * 通过构造注入
	 * 
	 * @param intrinsicState
	 *            内蕴状态
	 */
	public ConcreteFlyweight(Character intrinsicState) {
		this.intrinsicState = intrinsicState;
	}

	/**
	 * 外蕴状态，维护在客户端，不会影响到内蕴状态
	 */
	@Override
	public void operation(String state) {
		System.out.println("Intrinsic state = " + this.intrinsicState);
		System.out.println("Extrinsic state = " + state);
	}

}
