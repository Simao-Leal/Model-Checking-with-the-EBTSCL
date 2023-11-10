package claimlang.AST;

public class FormulaLiteralNode implements BasicNode {

	String formula;
	
	public FormulaLiteralNode(String formula) {
		this.formula = formula;
	}

	@Override
	public void accept(Visitor v) {
		v.visitFormulaLitaralNode(this);
	}

}
