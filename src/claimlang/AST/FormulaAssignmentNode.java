package claimlang.AST;

public class FormulaAssignmentNode implements BasicNode {

	String id;
	BasicNode formula;
	
	public FormulaAssignmentNode(String id, BasicNode formula) {
		this.id = id;
		this.formula = formula;
	}

	@Override
	public void accept(Visitor v) {
		v.visitFormulaAssignmentNode(this);
	}

}
