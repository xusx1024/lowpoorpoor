package SimpleFactory;

import static org.junit.Assert.*;

public class Test {
	@org.junit.Test
	public void test() throws Exception {
		testing(Product.TYPEA);
		testing(Product.TYPEB);
		// testing(3);
	}

	public static void testing(int type) {
		Product p = ProductFactory.getTypeAConcreteProduct(type);
		assertNotEquals(p, null);
		p.use();
	}
}
