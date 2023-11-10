package claimlang.AST;
public class ATSAssignmentNode implements BasicNode {

	String id;
	BasicNode ats;
	
	public ATSAssignmentNode(String id, BasicNode ats) {
		this.id = id;
		this.ats = ats;
	}

	@Override
	public void accept(Visitor v) {
		v.visitATSAssignmentNode(this);
	}
}