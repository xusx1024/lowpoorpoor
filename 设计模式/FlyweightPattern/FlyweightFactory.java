package FlyweightPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlyweightFactory {

	private Map<Character, Flyweight> files = new HashMap<>();

	/**
	 * 单纯享元工厂方法
	 * 
	 * @param state
	 * @return
	 */
	public Flyweight factory(Character state) {

		Flyweight fly = files.get(state);
		if (fly == null) {
			fly = new ConcreteFlyweight(state);
			files.put(state, fly);
		} else {
			System.out.println(state + "==>状态对应对象已经存在");
		}
		return fly;
	}

	public Flyweight factory(List<Character> compositeState) {
		ConcreteCompositeFlyweight fly = new ConcreteCompositeFlyweight();
		for (Character c : compositeState) {
			fly.add(c, this.factory(c));
		}
		return fly;
	}
}
