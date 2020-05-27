package AbstractFactory;

import static org.junit.Assert.*;

public class Test {

	@org.junit.Test
	public void test() {
		// fail("Not yet implemented");
		testing();
	}

	public static void testing() {
		AbstractFactory af = new AudiFactory();
		af.getCar().brand();
		af.getTyre().brand();

		af = new BenzFactory();
		af.getCar().brand();
		af.getTyre().brand();
	}
}
