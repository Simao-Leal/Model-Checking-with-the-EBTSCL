package claimlang.AST;

import java.util.List;

public class TimeOrderNode implements BasicNode {
	
	List<TimeComparisonNode> relations;

	public TimeOrderNode(List<TimeComparisonNode> relations) {
		this.relations = relations;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitTimeOrderNode(this);
	}
}
