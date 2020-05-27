package FactoryMethod;

public class FactoryB extends Factory {

	@Override
	public Product create() {
		return new ProductB();
	}

}
