package FactoryMethod;

import static org.junit.Assert.*;

public class Test {

	@org.junit.Test
	public void test() throws Exception {
		testing();
	}

	public static void testing() {
		Product p = new FactoryA().create();
		assertNotEquals(p, null);
		p.use();
		p = new FactoryB().create();
		assertNotEquals(p, null);
		p.use();
	}
}
