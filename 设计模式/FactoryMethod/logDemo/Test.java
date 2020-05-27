package FactoryMethod.logDemo;

import static org.junit.Assert.*;

public class Test {

	@org.junit.Test
	public void test() throws Exception {
		create();
	}

	public static void create() {
		Log log = new FileLogFactory().create();
		assertEquals(log.writeLog("test file log"), true);
		assertEquals(log.writeLog(""), false);
	}
}
