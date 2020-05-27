package ProxyPattern;

/**
 * 静态代理
 * 
 * @author sxx.xu
 *
 */
public class Proxy implements Subject {
	private RealSubject rs;

	@Override
	public void operation() {
		rs = new RealSubject();
		preOperation();
		rs.operation();
		afterOperation();
	}

	private void preOperation() {
		System.out.println("pre opreation");
	}

	private void afterOperation() {
		System.out.println("after operation");
	}

}
