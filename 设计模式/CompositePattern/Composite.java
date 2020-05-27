package CompositePattern;

import java.util.ArrayList;
import java.util.List;

public class Composite implements Component {

	private List<Component> childComponents = new ArrayList<>();
	private String name;

	public Composite(String name) {
		this.name = name;
	}

	public void addChild(Component child) {
		childComponents.add(child);
	}

	public List<Component> getChild() {
		return childComponents;
	}

	public void removeChild(int index) {
		childComponents.remove(index);
	}

	@Override
	public void printStruct(String preStr) {
		System.out.println(preStr + "+" + this.name);

		if (this.childComponents != null) {
			preStr += " ";
			for (Component child : childComponents) {
				child.printStruct(preStr);
			}
		}
	}

}
