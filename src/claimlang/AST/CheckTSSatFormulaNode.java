package claimlang.AST;

public class CheckTSSatFormulaNode implements BasicNode {

	PrintableNode ts;
	BasicNode formula;
	
	public CheckTSSatFormulaNode(PrintableNode ts, BasicNode formula) {
		this.ts = ts;
		this.formula = formula;
	}

	@Override
	public void accept(Visitor v) {
		v.visitCheckTSSatFormulaNode(this);
	}
}