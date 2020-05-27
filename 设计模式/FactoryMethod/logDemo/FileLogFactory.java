package FactoryMethod.logDemo;

public class FileLogFactory extends LogFactory {

	@Override
	public Log create() {
		return new FileLog();
	}

}
