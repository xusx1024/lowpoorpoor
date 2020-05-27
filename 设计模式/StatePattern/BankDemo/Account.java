package StatePattern.BankDemo;

public class Account {

	private AccountState state;
	private String owner;
	private double balance = 0;

	public Account(String owner, double balance) {
		this.owner = owner;
		this.balance = balance;
		this.state = new NormalState(this);
		System.out.println(this.owner + "开户，初始金额：" + balance);
		System.out.println("");
	}

	public void setState(AccountState state) {
		this.state = state;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void deposit(double amount) {
		System.out.println(this.owner + "存款：" + balance);
		state.deposit(amount);
		System.err.println("现在余额为：" + balance);
		System.out.println("现在账户状态为：" + this.state.getClass().getName());
		System.out.println("");
	}

	public void withdraw(double amount) {
		System.out.println(this.owner + "取款：" + balance);
		state.withdraw(amount);
		System.err.println("现在余额为：" + balance);
		System.out.println("现在账户状态为：" + this.state.getClass().getName());
		System.out.println("");
	}

	public void computeInterest() {
		state.computeInterest();
	}
}
