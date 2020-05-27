package ProxyPattern;

public class Client {
	public static void main(String[] args) {
		// Subject s = new Proxy();
		// s.operation();

		Subject real = new RealSubject();
		ProxySubject ps = new ProxySubject(real);
		Subject sub = (Subject) java.lang.reflect.Proxy.newProxyInstance(real.getClass().getClassLoader(),
				real.getClass().getInterfaces(), ps);

		sub.operation();
	}
}
