package StatePattern.BankDemo;

/**
 * 账户受限状态
 * 
 * @author sxx.xu
 *
 */
public class RestrictedState extends AccountState {

	public RestrictedState(Account acc) {
		this.acc = acc;
	}

	public RestrictedState(AccountState state) {
		this.acc = state.acc;
	}

	@Override
	public void deposit(double amount) {
		acc.setBalance(acc.getBalance() + amount);
		stateCheck();
	}

	@Override
	public void withdraw(double amount) {
		System.out.println("账户受限，取款失败");
	}

	@Override
	public void computeInterest() {
		System.out.println("计算利息");
	}

	@Override
	public void stateCheck() {
		if (acc.getBalance() > 0) {
			acc.setState(new NormalState(this));
		} else if (acc.getBalance() > -2000) {
			acc.setState(new OverdraftState(this));
		}
	}

}
