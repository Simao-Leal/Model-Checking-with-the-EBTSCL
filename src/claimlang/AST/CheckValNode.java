package claimlang.AST;

public class CheckValNode implements BasicNode {

	BasicNode formula;
	
	public CheckValNode(BasicNode formula) {
		this.formula = formula;
	}

	@Override
	public void accept(Visitor v) {
		v.visitCheckValNode(this);
	}
}