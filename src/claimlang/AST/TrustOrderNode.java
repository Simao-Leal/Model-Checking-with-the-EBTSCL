package claimlang.AST;

import java.util.List;

public class TrustOrderNode implements BasicNode {
	
	List<TrustLessThanNode> relations;

	public TrustOrderNode(List<TrustLessThanNode> relations) {
		this.relations = relations;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitTrustOrderNode(this);
	}
}
