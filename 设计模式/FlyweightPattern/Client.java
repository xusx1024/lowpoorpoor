package FlyweightPattern;

import java.util.ArrayList;
import java.util.List;

public class Client {

	public static void main(String[] args) {
		FlyweightFactory factory = new FlyweightFactory();
		Flyweight fly = factory.factory('a');
		fly.operation("1st");

		fly = factory.factory('b');
		fly.operation("2end");

		fly = factory.factory('a');
		fly.operation("3rd");

		///////////////////////////////////////////////////

		List<Character> compositeState = new ArrayList<>();
		compositeState.add('a');
		compositeState.add('b');
		compositeState.add('c');
		compositeState.add('a');
		compositeState.add('b');

		Flyweight compositeFly1 = factory.factory(compositeState);
		Flyweight compositeFly2 = factory.factory(compositeState);
		compositeFly1.operation("composite call");
		System.out.println("-------------------------------");
		System.out.println("复合享元模式是否可以共享对象：" + (compositeFly1 == compositeFly2));
		Character state = 'a';
		Flyweight fly1 = factory.factory(state);
		Flyweight fly2 = factory.factory(state);
		System.out.println("单纯享元模式是否可以共享对象：" + (fly1 == fly2));
	}

}
