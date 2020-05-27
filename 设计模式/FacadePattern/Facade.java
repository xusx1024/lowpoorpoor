package FacadePattern;

/**
 * 不要通过继承一个门面类，为某个子系统增加行为。<br/>
 * 
 * 使用装饰、适配器等为具体的子系统添加行为。<br/>
 * 
 * Facade只是为子系统提供一个集中化和简化的沟通管道，不能向子系统中添加行为。<br/>
 * 
 * final只能阻止继承，不能阻止通过其他方式添加行为，这个需要依靠开发者的自律的，是个缺点。<br/>
 * 
 * 外观类可以有多个，因此可以引入抽象外观类来对系统进行改进。<br/>
 * 
 * 这样又和策略模式相似了，策略是行为型，外观是对象型。
 * @author sxx.xu
 *
 */
public final class Facade {

	public void operation() {
		SystemA a = new SystemA();
		SystemB b = new SystemB();

		a.operationA();
		b.operationB();
	}
}
