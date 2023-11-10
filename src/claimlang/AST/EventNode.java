package claimlang.AST;

public class EventNode implements BasicNode{
	
	String id;

	public EventNode(String id) {
		this.id = id;
	}

	@Override
	public void accept(Visitor v) {
		v.visitEventNode(this);
	}
}
