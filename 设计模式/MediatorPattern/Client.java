package MediatorPattern;

public class Client {

	public static void main(String[] args) {
		ConcreteMediator mediator = new ConcreteMediator();

		HouseOwner houseOwner = new HouseOwner("Jack", mediator);
		Tenant tenant = new Tenant("Lucy", mediator);
		
		 mediator.setHouseOwner((HouseOwner) houseOwner);
		 mediator.setTenant((Tenant) tenant);
		 
		 tenant.constact("you jump?");
		 houseOwner.constact("I jump!");
		 
	}
}
