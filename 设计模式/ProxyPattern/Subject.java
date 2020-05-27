package ProxyPattern;

/**
 * 如果搞静态代理,这里是抽象类或者接口都无所谓,如果静态代理,这里必须为接口类型,否则类型转换异常.
 * 
 * @author sxx.xu
 *
 */
public interface Subject {
	void operation();
}
