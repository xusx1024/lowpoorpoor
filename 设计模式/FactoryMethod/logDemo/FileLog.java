package FactoryMethod.logDemo;

class FileLog extends Log {

	@Override
	public boolean writeLog(String str) {
		if(str.isEmpty())return false;
		System.out.println(String.format("try to write %s into file...", str));
		return true;
	}

	@Override
	public boolean queryLog(String str) {
		System.out.println(String.format("try to query: %s from file...", str));
		return false;
	}

}
