package FactoryMethod.logDemo;

public class DataBaseLogFactory extends LogFactory {

	@Override
	public Log create() {
		return new DataBaseLog();
	}

}
