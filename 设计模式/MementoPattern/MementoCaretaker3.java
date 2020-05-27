package MementoPattern;

/**
 *  备忘录模式中，管理者只负责管理，是不可以修改备忘录的<br/>
 *  
 *  所以，使用标识接口，管理者便不能修改备忘录的内容
 * 
 * @author sxx.xu
 *
 */
public class MementoCaretaker3 {
	private MementoIF memento;

	public MementoIF getMemento() {
		return memento;
	}

	public void setMemento(MementoIF memento) {
		this.memento = memento;
	}

}
