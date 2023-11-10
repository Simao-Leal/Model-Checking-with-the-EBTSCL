package claimlang.AST;

import java.util.LinkedList;
import java.util.List;

public class TransitionNode implements BasicNode{
	
	List<String> states;

	public TransitionNode(String sourceId, String targetId) {
		states = new LinkedList<>();
		states.add(sourceId);
		states.add(targetId);
	}
	
	public TransitionNode prepend(String sourceId) {
		states.add(0, sourceId);
		return this;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitTransitionNode(this);
	}
}
