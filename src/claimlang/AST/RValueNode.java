package claimlang.AST;

public class RValueNode implements PrintableNode {

	String id;
	
	public RValueNode(String id) {
		this.id = id;
	}

	@Override
	public void accept(Visitor v) {
		v.visitRValueNode(this);
	}

	@Override
	public String print() {
		return id;
	}

}
