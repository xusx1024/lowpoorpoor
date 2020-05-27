package ObserverPattern;

import java.util.ArrayList;

public abstract class AllyControlCenter {
	protected String allyName;// 战队名称
	protected ArrayList<Observer> players = new ArrayList<>();
	 

	public void setAllyName(String allyName) {
		this.allyName = allyName;
	}

	public String getAllyName() {
		return allyName;
	}

	public void join(Observer obs) {
		System.out.println(obs.getName() + "加入" + this.getAllyName() + "战队！");
		players.add(obs);
	}

	public void quit(Observer obs) {
		System.out.println(obs.getName() + "退出" + this.getAllyName() + "战队！");
		players.remove(obs);
	}

	public abstract void notifyObserver(String name);
}
