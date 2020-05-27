package FactoryMethod.logDemo;

class DataBaseLog extends Log {

	@Override
	public boolean writeLog(String str) {
		System.out.println(String.format("try to write %s into database...", str));
		return false;
	}

	@Override
	public boolean queryLog(String str) {
		System.out.println(String.format("try to query: %s from database...", str));
		return false;
	}

}
