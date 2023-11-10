package claimlang.AST;

import java.util.List;

public class StateNode implements BasicNode {

	String id;
	boolean initial;
	List<BasicNode> statements;
	
	public StateNode(String id, boolean initial, List<BasicNode> statements) {
		this.id = id;
		this.initial = initial;
		this.statements = statements;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitStateNode(this);
	}
}
