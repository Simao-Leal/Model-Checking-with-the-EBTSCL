package claimlang.AST;
public class TSAssignmentNode implements BasicNode {

	String id;
	BasicNode ts;
	
	public TSAssignmentNode(String id, BasicNode ts) {
		this.id = id;
		this.ts = ts;
	}

	@Override
	public void accept(Visitor v) {
		v.visitTSAssignmentNode(this);
	}
}
