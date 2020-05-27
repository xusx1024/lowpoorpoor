package HistoryOnSelfPattern;

public class Originator {
	private String state;

	public void changeState(String state) {
		this.state = state;
		System.out.println("change state to:" + state);
	}

	public Memento createMemento() {
		return new Memento(this);
	}

	public void restoreMemento(MementoIF m) {
		Memento mm = (Memento) m;
		changeState(mm.state);
	}

	private class Memento implements MementoIF {
		private String state;

		public Memento(Originator o) {
			super();
			this.state = o.state;
		}

		private String getState() {
			return state;
		}
	}
}
