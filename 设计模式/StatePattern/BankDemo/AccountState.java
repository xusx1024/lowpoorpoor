package StatePattern.BankDemo;

public abstract class AccountState {

	protected Account acc;
	
	/**
	 * 存款
	 * @param amount
	 */
	public abstract void deposit(double amount);
	
	/**
	 * 取款
	 * @param amount
	 */
	public abstract void withdraw(double amount);
	
	/**
	 * 计算利息
	 */
	public abstract void computeInterest();
	
	/**
	 * 状态检查
	 */
	public abstract void stateCheck();
	
}
