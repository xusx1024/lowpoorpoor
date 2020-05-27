package CompositePattern.clear;

import java.util.List;

public abstract class Component {

	public void addChild(Component child) {
		throw new UnsupportedOperationException("对象不支持此功能");
	}

	public void removeChild(int index) {
		throw new UnsupportedOperationException("对象不支持此功能");
	}

	public List<Component> getChild() {
		throw new UnsupportedOperationException("对象不支持此功能");
	}

	public abstract void printStruct(String preStr);
}
