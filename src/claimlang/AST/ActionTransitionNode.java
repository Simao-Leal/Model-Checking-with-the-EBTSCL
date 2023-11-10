/*
 * The EventTransitionSystem class used to be called ActionTransitionSystem.
 * This is why there are many references to action_statements, action_state, action_transitions, etc.
 * The classes in the AST are also called ActionStateNode, etc. It would be nice to change these all
 * to EventStateNode, etc. (basically replace action with event in many places) which I shall do when
 * I have time. But for now it seems like it would take a lot of time. As this code is not very substancial
 * within the thesis (it is literally just syntatic sugar), I will handle this later. 
 */

package claimlang.AST;

public class ActionTransitionNode implements BasicNode{
	
	String event;
	TransitionNode transitions;
	
	public ActionTransitionNode(String event, TransitionNode transitions) {
		this.event = event;
		this.transitions = transitions;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitActionTransitionNode(this);
	}
	
}
