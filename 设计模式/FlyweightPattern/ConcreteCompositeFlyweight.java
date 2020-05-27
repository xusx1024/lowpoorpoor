package FlyweightPattern;

import java.util.HashMap;
import java.util.Map;

/**
 * 组合享元角色
 * 
 * @author sxx.xu
 *
 */
public class ConcreteCompositeFlyweight implements Flyweight {

	private Map<Character, Flyweight> files = new HashMap<>();

	public void add(Character key, Flyweight fly) {
		files.put(key, fly);
	}

	@Override
	public void operation(String state) {
		Flyweight fly = null;

		for (Object o : files.keySet()) {
			fly = files.get(o);
			fly.operation(state);
		}

	}

}
