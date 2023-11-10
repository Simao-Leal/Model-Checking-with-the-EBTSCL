package claimlang.AST;

public class TimeLessThanNode extends TimeComparisonNode {
	
	public TimeLessThanNode(String idLeft, String idRight) {
		this.idLeft = idLeft;
		this.idRight = idRight;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visitTimeLessThanNode(this);
	}

}
