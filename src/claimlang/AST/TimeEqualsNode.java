package claimlang.AST;

public class TimeEqualsNode extends TimeComparisonNode {
	
	public TimeEqualsNode(String idLeft, String idRight) {
		this.idLeft = idLeft;
		this.idRight = idRight;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitTimeEqualsNode(this);
	}

}

