package FactoryMethod;

public class FactoryA extends Factory {

	@Override
	public Product create() {
		return new ProductA();
	}

}
