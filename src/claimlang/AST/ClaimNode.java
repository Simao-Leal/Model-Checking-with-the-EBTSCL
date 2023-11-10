package claimlang.AST;

public class ClaimNode implements BasicNode{
	
	String idAgent;
	boolean isMinus; 
	String idTime;
	String idProp;
	
	public ClaimNode(String idAgent, boolean isMinus, String idTime, String idProp) {
		this.idAgent = idAgent;
		this.isMinus = isMinus;
		this.idTime = idTime;
		this.idProp = idProp;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitClaimNode(this);
	}
}
