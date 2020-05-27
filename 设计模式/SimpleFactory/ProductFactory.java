package SimpleFactory;

public class ProductFactory {

	public static Product getTypeAConcreteProduct(int type) {
		switch (type) {
		case Product.TYPEA:
			return new ConcreteProductA();
		case Product.TYPEB:
			return new ConcreteProductB();
		default:
			return null;
		}
	}
}
