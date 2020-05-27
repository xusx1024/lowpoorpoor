package HistoryOnSelfPattern;

public class Client {

	public static void main(String[] args) {
		Originator o = new Originator();
		o.changeState("state 0");

		MementoIF m = o.createMemento();

		o.changeState("state 1");

		o.restoreMemento(m);

	}

}
