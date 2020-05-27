package ProxyPattern;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 * 
 * @author sxx.xu
 *
 */
public class ProxySubject implements InvocationHandler {

	private Subject s;

	public ProxySubject(Subject s) {
		super();
		this.s = s;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return method.invoke(s, args);
	}

}
