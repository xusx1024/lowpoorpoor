package ChainOfResponsibility;

public class Client {

	public static void main(String[] args) {
		Approver director,vicePresident,president,congress;
		
		director = new Director("Peter");
		vicePresident = new VicePresident("Walker");
		president = new President("Frank");
		congress = new Congress("Hall");
		
		director.setSuccessor(vicePresident);
		vicePresident.setSuccessor(president);
		president.setSuccessor(congress);
		
		
		PurchaseRequest pr0 = new PurchaseRequest(45000, 1, "造船厂拆除工作");
		director.processRequest(pr0);
		
		PurchaseRequest pr1 = new PurchaseRequest(60000, 2, "教育法案修订");
		director.processRequest(pr1);
		
		PurchaseRequest pr2 = new PurchaseRequest(160000, 3, "滴入式中东外交政策");
		director.processRequest(pr2);
		
		PurchaseRequest pr3 = new PurchaseRequest(800000, 4, "向ICO恐怖主义组织宣战！");
		director.processRequest(pr3);
	}
}
