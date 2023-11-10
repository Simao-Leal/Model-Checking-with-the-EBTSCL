package claimlang.AST;

public class TrustLessThanNode implements BasicNode {

	public String idLeft;
	public String idProp;
	public String idRight;
	
	public TrustLessThanNode(String idLeft, String idProp, String idRight) {
		this.idLeft = idLeft;
		this.idProp = idProp;
		this.idRight = idRight;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitTrustLessThanNode(this);
	}

}
