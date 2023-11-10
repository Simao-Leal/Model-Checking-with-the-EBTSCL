package claimlang.AST;

import java.util.List;

public class TransitionSystemNode implements BasicNode{

	String id;
	List<BasicNode> statements;
	
	public TransitionSystemNode(String id, List<BasicNode> statements) {
		this.id = id;
		this.statements = statements;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitTransitionSystemNode(this);
	}

}
