package claimlang.AST;

public class CheckSatNode implements BasicNode {

	BasicNode formula;
	
	public CheckSatNode(BasicNode formula) {
		this.formula = formula;
	}

	@Override
	public void accept(Visitor v) {
		v.visitCheckSatNode(this);
	}

}
