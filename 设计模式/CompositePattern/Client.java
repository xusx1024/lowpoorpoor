package CompositePattern;

/**
 * 树枝节点有树叶节点所没有的add,remove,get方法<br/>
 * 
 * 这样做法是安全的，客户端不可能错误的调用叶子节点的add,remove,get<br/>
 * 
 * 缺点是不够透明>
 * @author sxx.xu
 *
 */
public class Client {

	public static void main(String[] args) {
		Composite root = new Composite("国家");

		Composite China = new Composite("中国");
		Composite usa = new Composite("美国");

		Leaf leaf1 = new Leaf("北京");
		Leaf leaf2 = new Leaf("上海");

		Leaf leaf3 = new Leaf("华盛顿");
		Leaf leaf4 = new Leaf("纽约");

		root.addChild(China);
		root.addChild(usa);

		China.addChild(leaf1);
		China.addChild(leaf2);

		usa.addChild(leaf3);
		usa.addChild(leaf4);

		root.printStruct("");
	}
}
