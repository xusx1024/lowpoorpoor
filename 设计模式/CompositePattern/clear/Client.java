package CompositePattern.clear;

/**
 * 对客户端而言，都是Component对象，可以透明操作，面向抽象编程
 * 
 * @author sxx.xu
 *
 */
public class Client {

	public static void main(String[] args) {
		Component root = new Composite("国家");

		Component China = new Composite("中国");
		Component usa = new Composite("美国");

		Component leaf1 = new Leaf("北京");
		Component leaf2 = new Leaf("上海");

		Component leaf3 = new Leaf("华盛顿");
		Component leaf4 = new Leaf("纽约");

		root.addChild(China);
		root.addChild(usa);

		China.addChild(leaf1);
		China.addChild(leaf2);

		usa.addChild(leaf3);
		usa.addChild(leaf4);

		/**
		 * 叶子节点调用add、get、remove方法，父类直接抛出不支持的异常
		 */
		// leaf1.addChild(China);
		root.printStruct("");
	}
}
