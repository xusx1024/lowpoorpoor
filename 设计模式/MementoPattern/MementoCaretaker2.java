package MementoPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现多步回退<br/>
 * 实现撤销操作<br/>
 * 实现重做操作<br/>
 * 
 * @author sxx.xu
 *
 */
public class MementoCaretaker2 {
	private List<ChessmanMemento> mementolist = new ArrayList<>();

	public ChessmanMemento getMemento(int i) {
		return mementolist.get(i);
	}

	public void setMemento(ChessmanMemento m) {
		mementolist.add(m);
	}
}
