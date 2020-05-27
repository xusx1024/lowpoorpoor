package ProxyPattern;

public class RealSubject implements Subject {

	@Override
	public void operation() {
		System.out.println("do some thing");
	}

}
